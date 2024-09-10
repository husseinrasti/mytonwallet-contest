import type { NftItem } from 'tonapi-sdk-js';
import type { DictionaryValue } from '@ton/core';
import { BitReader } from '@ton/core/dist/boc/BitReader';
import { BitString } from '@ton/core/dist/boc/BitString';
import { Builder } from '@ton/core/dist/boc/Builder';
import { Cell } from '@ton/core/dist/boc/Cell';
import { Slice } from '@ton/core/dist/boc/Slice';
import { Dictionary } from '@ton/core/dist/dict/Dictionary';

import type { ApiNetwork, ApiNft, ApiParsedPayload } from '../../../types';
import type { DnsCategory } from '../constants';
import type { ApiTransactionExtra, JettonMetadata } from '../types';

import {
  DEBUG,
  LIQUID_JETTON,
  NFT_FRAGMENT_COLLECTIONS,
} from '../../../../config';
import { pick, range } from '../../../../util/iteratees';
import { logDebugError } from '../../../../util/logs';
import { fetchJsonMetadata, fixIpfsUrl } from '../../../../util/metadata';
import { checkHasScamLink, checkIsTrustedCollection } from '../../../common/addresses';
import { base64ToString, sha256 } from '../../../common/utils';
import {
  DNS_CATEGORY_HASH_MAP,
  DnsOpCode,
  JettonOpCode,
  LiquidStakingOpCode,
  NftOpCode,
  OpCode,
  OtherOpCode,
  SingleNominatorOpCode,
  VestingV1OpCode,
} from '../constants';
import { fixAddressFormat } from './apiV3';
import { buildTokenSlug } from './index';
import { fetchNftItems } from './tonapiio';
import {
  getDnsItemDomain, getJettonMinterData, resolveTokenMinterAddress, toBase64Address,
} from './tonCore';

const OFFCHAIN_CONTENT_PREFIX = 0x01;
const SNAKE_PREFIX = 0x00;

export function parseJettonWalletMsgBody(network: ApiNetwork, body?: string) {
  if (!body) return undefined;

  try {
    let slice = Cell.fromBase64(body).beginParse();
    const opCode = slice.loadUint(32);
    const queryId = slice.loadUint(64);

    if (opCode !== JettonOpCode.Transfer && opCode !== JettonOpCode.InternalTransfer) {
      return undefined;
    }

    const jettonAmount = slice.loadCoins();
    const address = slice.loadMaybeAddress();
    const responseAddress = slice.loadMaybeAddress();
    let forwardAmount: bigint | undefined;
    let comment: string | undefined;
    let encryptedComment: string | undefined;

    if (responseAddress) {
      if (opCode === JettonOpCode.Transfer) {
        slice.loadBit();
      }
      forwardAmount = slice.loadCoins();
      const isSeparateCell = slice.remainingBits && slice.loadBit();
      if (isSeparateCell && slice.remainingRefs) {
        slice = slice.loadRef().beginParse();
      }
      if (slice.remainingBits > 32) {
        const forwardOpCode = slice.loadUint(32);
        if (forwardOpCode === OpCode.Comment) {
          const buffer = readSnakeBytes(slice);
          comment = buffer.toString('utf-8');
        } else if (forwardOpCode === OpCode.Encrypted) {
          const buffer = readSnakeBytes(slice);
          encryptedComment = buffer.toString('base64');
        }
      }
    }

    return {
      operation: JettonOpCode[opCode] as keyof typeof JettonOpCode,
      queryId,
      jettonAmount,
      responseAddress,
      address: address ? toBase64Address(address, undefined, network) : undefined,
      forwardAmount,
      comment,
      encryptedComment,
    };
  } catch (err) {
    logDebugError('parseJettonWalletMsgBody', err);
  }

  return undefined;
}

export function fixBase64ImageData(data: string) {
  const decodedData = base64ToString(data);
  if (decodedData.includes('<svg')) {
    return `data:image/svg+xml;base64,${data}`;
  }
  return `data:image/png;base64,${data}`;
}

const dictSnakeBufferValue: DictionaryValue<Buffer> = {
  parse: (slice) => {
    const buffer = Buffer.from('');

    const sliceToVal = (s: Slice, v: Buffer, isFirst: boolean) => {
      if (isFirst && s.loadUint(8) !== SNAKE_PREFIX) {
        throw new Error('Only snake format is supported');
      }

      v = Buffer.concat([v, s.loadBuffer(s.remainingBits / 8)]);
      if (s.remainingRefs === 1) {
        v = sliceToVal(s.loadRef().beginParse(), v, false);
      }

      return v;
    };

    return sliceToVal(slice.loadRef().beginParse() as any, buffer, true);
  },
  serialize: () => {
    // pass
  },
};

const jettonOnChainMetadataSpec: {
  [key in keyof JettonMetadata]: 'utf8' | 'ascii' | undefined;
} = {
  uri: 'ascii',
  name: 'utf8',
  description: 'utf8',
  image: 'ascii',
  symbol: 'utf8',
  decimals: 'utf8',
};

export async function fetchJettonMetadata(network: ApiNetwork, address: string) {
  const { content } = await getJettonMinterData(network, address);

  let metadata: JettonMetadata;

  const slice = content.asSlice();
  const prefix = slice.loadUint(8);

  if (prefix === OFFCHAIN_CONTENT_PREFIX) {
    const bytes = readSnakeBytes(slice);
    const contentUri = bytes.toString('utf-8');
    metadata = await fetchJettonOffchainMetadata(contentUri);
  } else {
    // On-chain content
    metadata = await parseJettonOnchainMetadata(slice);
    if (metadata.uri) {
      // Semi-chain content
      const offchainMetadata = await fetchJettonOffchainMetadata(metadata.uri);
      metadata = { ...offchainMetadata, ...metadata };
    }
  }

  return metadata;
}

export async function parseJettonOnchainMetadata(slice: Slice): Promise<JettonMetadata> {
  const dict = slice.loadDict(Dictionary.Keys.Buffer(32), dictSnakeBufferValue);

  const res: { [s in keyof JettonMetadata]?: string } = {};

  for (const [key, value] of Object.entries(jettonOnChainMetadataSpec)) {
    const sha256Key = Buffer.from(await sha256(Buffer.from(key, 'ascii')));
    const val = dict.get(sha256Key)?.toString(value);

    if (val) {
      res[key as keyof JettonMetadata] = val;
    }
  }

  return res as JettonMetadata;
}

export async function fetchJettonOffchainMetadata(uri: string): Promise<JettonMetadata> {
  const metadata = await fetchJsonMetadata(uri);
  return pick(metadata, ['name', 'description', 'symbol', 'decimals', 'image', 'image_data']);
}

export async function parseWalletTransactionBody(
  network: ApiNetwork, transaction: ApiTransactionExtra,
): Promise<ApiTransactionExtra> {
  const body = transaction.extraData?.body;
  if (!body || transaction.comment || transaction.encryptedComment) {
    return transaction;
  }

  try {
    const slice = dataToSlice(body);

    if (slice.remainingBits > 32) {
      const address = transaction.isIncoming ? transaction.fromAddress : transaction.toAddress;

      const parsedPayload = await parsePayloadSlice(
        network, address, slice, false, transaction,
      );
      transaction.extraData!.parsedPayload = parsedPayload;

      if (parsedPayload?.type === 'comment') {
        transaction = {
          ...transaction,
          comment: parsedPayload.comment,
        };
      } else if (parsedPayload?.type === 'encrypted-comment') {
        transaction = {
          ...transaction,
          encryptedComment: parsedPayload.encryptedComment,
        };
      }
    }
  } catch (err) {
    logDebugError('parseTransactionBody', err);
  }

  return transaction;
}

export async function parsePayloadBase64(
  network: ApiNetwork,
  address: string,
  base64: string,
): Promise<ApiParsedPayload> {
  const slice = dataToSlice(base64);
  const result: ApiParsedPayload = { type: 'unknown', base64 };

  if (!slice) return result;

  return await parsePayloadSlice(network, address, slice, true) ?? result;
}

export async function parsePayloadSlice(
  network: ApiNetwork,
  address: string,
  slice: Slice,
  shouldLoadItems?: boolean,
  transactionDebug?: ApiTransactionExtra,
): Promise<ApiParsedPayload | undefined> {
  let opCode: number | undefined;
  try {
    opCode = slice.loadUint(32);

    if (opCode === OpCode.Comment) {
      const buffer = readSnakeBytes(slice);
      const comment = buffer.toString('utf-8');
      return { type: 'comment', comment };
    } else if (opCode === OpCode.Encrypted) {
      const buffer = readSnakeBytes(slice);
      const encryptedComment = buffer.toString('base64');
      return { type: 'encrypted-comment', encryptedComment };
    } else if (slice.remainingBits < 64) {
      return undefined;
    }

    const queryId = slice.loadUintBig(64);

    switch (opCode) {
      case JettonOpCode.Transfer: {
        const minterAddress = await resolveTokenMinterAddress(network, address);
        const slug = buildTokenSlug(minterAddress);

        const amount = slice.loadCoins();
        const destination = slice.loadAddress();
        const responseDestination = slice.loadMaybeAddress();

        if (!responseDestination) {
          return {
            type: 'tokens:transfer-non-standard',
            queryId,
            destination: toBase64Address(destination, undefined, network),
            amount,
            slug,
          };
        }

        const customPayload = slice.loadMaybeRef();
        const forwardAmount = slice.loadCoins();
        let forwardPayload = slice.loadMaybeRef();
        if (!forwardPayload && slice.remainingBits) {
          const builder = new Builder().storeBits(slice.loadBits(slice.remainingBits));
          range(0, slice.remainingRefs).forEach(() => {
            builder.storeRef(slice.loadRef());
          });
          forwardPayload = builder.endCell();
        }

        return {
          type: 'tokens:transfer',
          queryId,
          amount,
          destination: toBase64Address(destination, undefined, network),
          responseDestination: toBase64Address(responseDestination, undefined, network),
          customPayload: customPayload?.toBoc().toString('base64'),
          forwardAmount,
          forwardPayload: forwardPayload?.toBoc().toString('base64'),
          slug,
        };
      }
      case NftOpCode.TransferOwnership: {
        const newOwner = slice.loadAddress();
        const responseDestination = slice.loadAddress();
        const customPayload = slice.loadMaybeRef();
        const forwardAmount = slice.loadCoins();
        const forwardPayload = readForwardPayloadCell(slice);
        const comment = forwardPayload ? readComment(forwardPayload.asSlice()) : undefined;

        let nft: ApiNft | undefined;
        if (shouldLoadItems) {
          const [rawNft] = await fetchNftItems(network, [address]);
          if (rawNft) {
            nft = buildNft(network, rawNft);
          }
        }

        return {
          type: 'nft:transfer',
          queryId,
          newOwner: toBase64Address(newOwner, undefined, network),
          responseDestination: toBase64Address(responseDestination, undefined, network),
          customPayload: customPayload?.toBoc().toString('base64'),
          forwardAmount,
          forwardPayload: forwardPayload?.toBoc().toString('base64'),
          nftAddress: address,
          nftName: nft?.name,
          nft,
          comment,
        };
      }
      case NftOpCode.OwnershipAssigned: {
        const prevOwner = slice.loadAddress();
        const forwardPayload = readForwardPayloadCell(slice);
        const comment = forwardPayload ? readComment(forwardPayload.asSlice()) : undefined;

        let nft: ApiNft | undefined;
        if (shouldLoadItems) {
          const [rawNft] = await fetchNftItems(network, [address]);
          if (rawNft) {
            nft = buildNft(network, rawNft);
          }
        }

        return {
          type: 'nft:ownership-assigned',
          queryId,
          prevOwner: toBase64Address(prevOwner, undefined, network),
          comment,
          nftAddress: address,
          nft,
        };
      }
      case JettonOpCode.Burn: {
        const minterAddress = await resolveTokenMinterAddress(network, address);
        const slug = buildTokenSlug(minterAddress);

        const amount = slice.loadCoins();
        const addressObj = slice.loadAddress();
        const customPayload = slice.loadMaybeRef();
        const isLiquidUnstakeRequest = minterAddress === LIQUID_JETTON;

        return {
          type: 'tokens:burn',
          queryId,
          amount,
          address: toBase64Address(addressObj, undefined, network),
          customPayload: customPayload?.toBoc().toString('base64'),
          slug,
          isLiquidUnstakeRequest,
        };
      }
      case LiquidStakingOpCode.DistributedAsset: {
        return {
          type: 'liquid-staking:withdrawal-nft',
          queryId,
        };
      }
      case LiquidStakingOpCode.Withdrawal: {
        return {
          type: 'liquid-staking:withdrawal',
          queryId,
        };
      }
      case LiquidStakingOpCode.Deposit: {
        let appId: bigint | undefined;
        if (slice.remainingBits > 0) {
          appId = slice.loadUintBig(64);
        }
        return {
          type: 'liquid-staking:deposit',
          queryId,
          appId,
        };
      }
      case VestingV1OpCode.AddWhitelist: {
        const toAddress = slice.loadAddress();
        const addressString = shouldLoadItems
          ? await fixAddressFormat(network, toAddress.toRawString())
          : '';

        return {
          type: 'vesting:add-whitelist',
          queryId,
          address: addressString,
        };
      }
      case SingleNominatorOpCode.Withdraw: {
        const amount = slice.loadCoins();
        return {
          type: 'single-nominator:withdraw',
          queryId,
          amount,
        };
      }
      case SingleNominatorOpCode.ChangeValidator: {
        const toAddress = slice.loadAddress();
        const addressString = shouldLoadItems
          ? await fixAddressFormat(network, toAddress.toRawString())
          : '';

        return {
          type: 'single-nominator:change-validator',
          queryId,
          address: addressString,
        };
      }
      case LiquidStakingOpCode.Vote: {
        const votingAddress = slice.loadAddress();
        const expirationDate = slice.loadUint(48);
        const vote = slice.loadBit();
        const needConfirmation = slice.loadBit();

        return {
          type: 'liquid-staking:vote',
          queryId,
          votingAddress: toBase64Address(votingAddress, true),
          expirationDate,
          vote,
          needConfirmation,
        };
      }
      case DnsOpCode.ChangeRecord: {
        const hash = slice.loadBuffer(32).toString('hex');
        const category = Object.entries(DNS_CATEGORY_HASH_MAP)
          .find(([, value]) => hash === value)?.[0] as DnsCategory ?? 'unknown';
        const toAddress = slice.loadAddress();
        const domain = shouldLoadItems
          ? await getDnsItemDomain(network, toAddress)
          : '';

        if (category === 'wallet') {
          if (slice.remainingRefs > 0) {
            const dataSlice = slice.loadRef().beginParse();
            slice.endParse();

            const dataAddress = dataSlice.loadAddress();
            const flags = dataSlice.loadUint(8);

            const addressString = shouldLoadItems
              ? await fixAddressFormat(network, dataAddress.toRawString())
              : '';

            return {
              type: 'dns:change-record',
              queryId,
              record: {
                type: 'wallet',
                value: addressString,
                flags,
              },
              domain,
            };
          } else {
            return {
              type: 'dns:change-record',
              queryId,
              record: {
                type: 'wallet',
                value: undefined,
              },
              domain,
            };
          }
        } else if (slice.remainingRefs > 0) {
          const value = slice.loadRef();
          return {
            type: 'dns:change-record',
            queryId,
            record: category === 'unknown' ? {
              type: 'unknown',
              key: hash,
              value: value.toBoc().toString('base64'),
            } : {
              type: category,
              value: value.toBoc().toString('base64'),
            },
            domain,
          };
        } else {
          return {
            type: 'dns:change-record',
            queryId,
            record: category === 'unknown' ? {
              type: 'unknown',
              key: hash,
            } : {
              type: category,
            },
            domain,
          };
        }
      }
      case OtherOpCode.TokenBridgePaySwap: {
        const swapId = slice.loadBuffer(32).toString('hex');
        return {
          type: 'token-bridge:pay-swap',
          queryId,
          swapId,
        };
      }
    }
  } catch (err) {
    if (DEBUG) {
      const debugTxString = transactionDebug
        && `${transactionDebug.txId} ${new Date(transactionDebug.timestamp)}`;
      const opCodeHex = `0x${opCode?.toString(16).padStart(8, '0')}`;
      logDebugError('parsePayload', opCodeHex, debugTxString, '\n', err);
    }
  }

  return undefined;
}

function dataToSlice(data: string | Buffer | Uint8Array): Slice {
  let buffer: Buffer;
  if (typeof data === 'string') {
    buffer = Buffer.from(data, 'base64');
  } else if (data instanceof Buffer) {
    buffer = data;
  } else {
    buffer = Buffer.from(data);
  }

  try {
    return Cell.fromBoc(buffer)[0].beginParse();
  } catch (err: any) {
    if (err?.message !== 'Invalid magic') {
      throw err;
    }
  }

  return new Slice(new BitReader(new BitString(buffer, 0, buffer.length * 8)), []);
}

function readComment(slice: Slice) {
  if (slice.remainingBits < 32) {
    return undefined;
  }

  const opCode = slice.loadUint(32);
  if (opCode !== OpCode.Comment || (!slice.remainingBits && !slice.remainingRefs)) {
    return undefined;
  }

  const buffer = readSnakeBytes(slice);
  return buffer.toString('utf-8');
}

function readForwardPayloadCell(slice: Slice) {
  let forwardPayload = slice.loadBit() && slice.remainingRefs ? slice.loadRef() : undefined;

  if (!forwardPayload && slice.remainingBits) {
    const builder = new Builder().storeBits(slice.loadBits(slice.remainingBits));
    range(0, slice.remainingRefs).forEach(() => {
      builder.storeRef(slice.loadRef());
    });
    forwardPayload = builder.endCell();
  }

  return forwardPayload ?? undefined;
}

export function readSnakeBytes(slice: Slice) {
  let buffer = Buffer.alloc(0);

  while (slice.remainingBits >= 8) {
    buffer = Buffer.concat([buffer, slice.loadBuffer(slice.remainingBits / 8)]);
    if (slice.remainingRefs) {
      slice = slice.loadRef().beginParse();
    } else {
      break;
    }
  }

  return buffer;
}

export function buildNft(network: ApiNetwork, rawNft: NftItem): ApiNft | undefined {
  if (!rawNft.metadata) {
    return undefined;
  }

  try {
    const {
      address,
      index,
      collection,
      metadata,
      previews,
      sale,
      trust,
    } = rawNft;

    const {
      name, image, description, render_type: renderType,
    } = metadata as {
      name?: string;
      image?: string;
      description?: string;
      render_type?: string;
    };

    const collectionAddress = collection && toBase64Address(collection.address, true, network);
    let hasScamLink = false;

    if (!collectionAddress || !checkIsTrustedCollection(collectionAddress)) {
      for (const text of [name, description].filter(Boolean)) {
        if (checkHasScamLink(text)) {
          hasScamLink = true;
        }
      }
    }

    const isScam = hasScamLink || description === 'SCAM' || trust === 'blacklist';
    const isHidden = renderType === 'hidden' || isScam;
    const imageFromPreview = previews!.find((x) => x.resolution === '1500x1500')!.url;

    return {
      index,
      name,
      address: toBase64Address(address, true, network),
      image: fixIpfsUrl(imageFromPreview || image || ''),
      thumbnail: previews!.find((x) => x.resolution === '500x500')!.url,
      isOnSale: Boolean(sale),
      isHidden,
      isScam,
      description,
      ...(collection && {
        collectionAddress: toBase64Address(collection.address, true, network),
        collectionName: collection.name,
        isOnFragment: NFT_FRAGMENT_COLLECTIONS.has(collection.address),
      }),
    };
  } catch (err) {
    logDebugError('buildNft', err);
    return undefined;
  }
}
