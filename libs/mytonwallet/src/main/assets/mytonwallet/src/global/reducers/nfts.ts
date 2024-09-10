import type { ApiNft } from '../../api/types';
import type { GlobalState } from '../types';

import { selectAccountState } from '../selectors';
import { updateAccountState } from './misc';

export function addNft(global: GlobalState, accountId: string, nft: ApiNft) {
  const nftAddress = nft.address;
  const nfts = selectAccountState(global, accountId)!.nfts;
  const orderedAddresses = (nfts?.orderedAddresses ?? []).filter((address) => address !== nftAddress);

  return updateAccountState(global, accountId, {
    nfts: {
      ...nfts,
      byAddress: { ...nfts?.byAddress, [nftAddress]: nft },
      orderedAddresses: [nftAddress, ...orderedAddresses],
    },
  });
}

export function removeNft(global: GlobalState, accountId: string, nftAddress: string) {
  const nfts = selectAccountState(global, accountId)!.nfts;
  const orderedAddresses = (nfts?.orderedAddresses ?? []).filter((address) => address !== nftAddress);
  const selectedAddresses = (nfts?.selectedAddresses ?? []).filter((address) => address !== nftAddress);
  const { [nftAddress]: removedNft, ...byAddress } = nfts?.byAddress ?? {};

  return updateAccountState(global, accountId, {
    nfts: {
      ...nfts,
      byAddress,
      orderedAddresses,
      selectedAddresses,
    },
  });
}

export function updateNft(global: GlobalState, accountId: string, nftAddress: string, partial: Partial<ApiNft>) {
  const nfts = selectAccountState(global, accountId)!.nfts;
  const nft = nfts?.byAddress[nftAddress];
  if (!nfts || !nft) return global;

  return updateAccountState(global, accountId, {
    nfts: {
      ...nfts,
      byAddress: {
        ...nfts.byAddress,
        [nftAddress]: { ...nft, ...partial },
      },
    },
  });
}

export function addToSelectedAddresses(global: GlobalState, accountId: string, nftAddresses: string[]) {
  const nfts = selectAccountState(global, accountId)!.nfts;
  const selectedAddresses = [...(nfts?.selectedAddresses ?? []), ...nftAddresses];

  return updateAccountState(global, accountId, {
    nfts: {
      ...nfts!,
      selectedAddresses,
    },
  });
}

export function removeFromSelectedAddresses(global: GlobalState, accountId: string, nftAddress: string) {
  const nfts = selectAccountState(global, accountId)!.nfts;
  const selectedAddresses = (nfts?.selectedAddresses ?? []).filter((address) => address !== nftAddress);

  return updateAccountState(global, accountId, {
    nfts: {
      ...nfts!,
      selectedAddresses: selectedAddresses.length ? selectedAddresses : undefined,
    },
  });
}
