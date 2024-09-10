import type { ApiWalletVersion } from '../../types';
import type { ContractInfo, ContractName } from './types';
import { Workchain } from '../../types';

export const TOKEN_TRANSFER_AMOUNT = 50000000n; // 0.05 TON
export const TINY_TOKEN_TRANSFER_AMOUNT = 18000000n; // 0.018 TON
export const TOKEN_TRANSFER_FORWARD_AMOUNT = 1n; // 0.000000001 TON

export const NFT_TRANSFER_AMOUNT = 100000000n; // 0.1 TON
export const NFT_TRANSFER_FORWARD_AMOUNT = 1n; // 0.000000001 TON

export const STAKE_COMMENT = 'd';
export const UNSTAKE_COMMENT = 'w';

export const ATTEMPTS = 5;

export const DEFAULT_DECIMALS = 9;
export const DEFAULT_IS_BOUNCEABLE = true;
export const WALLET_IS_BOUNCEABLE = false;

// Fee may change, so we add 5% for more reliability. This is only safe for low-fee blockchains such as TON.
export const FEE_FACTOR = 1.05;

export const ALL_WALLET_VERSIONS: ApiWalletVersion[] = [
  'simpleR1', 'simpleR2', 'simpleR3', 'v2R1', 'v2R2', 'v3R1', 'v3R2', 'v4R2', 'W5',
];

export const WORKCHAIN = Workchain.BaseChain;
export const TRANSFER_TIMEOUT_SEC = 600; // 10 min.

export enum OpCode {
  Comment = 0,
  Encrypted = 0x2167da4b,
}

export enum JettonOpCode {
  Transfer = 0xf8a7ea5,
  TransferNotification = 0x7362d09c,
  InternalTransfer = 0x178d4519,
  Excesses = 0xd53276db,
  Burn = 0x595f07bc,
  BurnNotification = 0x7bdd97de,
}

export enum NftOpCode {
  TransferOwnership = 0x5fcc3d14,
  OwnershipAssigned = 0x05138d91,
}

export enum LiquidStakingOpCode {
  // Pool
  RequestLoan = 0xe642c965,
  LoanRepayment = 0xdfdca27b,
  Deposit = 0x47d54391,
  Withdraw = 0x319B0CDC,
  Withdrawal = 0x0a77535c,
  DeployController = 0xb27edcad,
  Touch = 0x4bc7c2df,
  Donate = 0x73affe21,
  // NFT
  DistributedAsset = 0xdb3b8abd,
  // Jetton
  Vote = 0x69fb306c,
}

export enum VestingV1OpCode {
  AddWhitelist = 0x7258a69b,
}

export enum SingleNominatorOpCode {
  Withdraw = 0x1000,
  ChangeValidator = 0x1001,
}

export enum DnsOpCode {
  ChangeRecord = 0x4eb1f0f9,
}

export enum OtherOpCode {
  TokenBridgePaySwap = 0x8,
}

export enum ContractType {
  Wallet = 'wallet',
  Staking = 'staking',
}

export enum DnsCategory {
  DnsNextResolver = 'dns_next_resolver',
  Wallet = 'wallet',
  Site = 'site',
  BagId = 'storage',
  Unknown = 'unknown',
}

export const DNS_CATEGORY_HASH_MAP = {
  dns_next_resolver: '19f02441ee588fdb26ee24b2568dd035c3c9206e11ab979be62e55558a1d17ff',
  wallet: 'e8d44050873dba865aa7c170ab4cce64d90839a34dcfd6cf71d14e0205443b1b',
  site: 'fbae041b02c41ed0fd8a4efb039bc780dd6af4a1f0c420f42561ae705dda43fe',
  storage: '49a25f9feefaffecad0fcd30c50dc9331cff8b55ece53def6285c09e17e6f5d7',
} as const;

export const DNS_ZONES_MAP = {
  '.t.me': 'EQCA14o1-VWhS2efqoh_9M1b_A9DtKTuoqfmkn83AbJzwnPi',
  '.ton': 'EQC3dNlesgVD8YbAazcauIrXBPfiVhMMr5YYk2in0Mtsz0Bz',
  '.vip': 'EQBWG4EBbPDv4Xj7xlPwzxd7hSyHMzwwLB5O6rY-0BBeaixS',
  '.gram': 'EQAic3zPce496ukFDhbco28FVsKKl2WUX_iJwaL87CBxSiLQ',
} as const;

export const KnownContracts: Record<ContractName, ContractInfo> = {
  simpleR1: {
    name: 'simpleR1',
    hash: '3232dc55b02b3d2a9485adc151cf29c50b94c374d3571cb59390d761b87af8bd',
    type: ContractType.Wallet,
  },
  simpleR2: {
    name: 'simpleR2',
    hash: '672ce2b01d2fd487a5e0528611e7e4fc11867148cc13ff772bd773b72fb368df',
    type: ContractType.Wallet,
  },
  simpleR3: {
    name: 'simpleR3',
    hash: 'd95417233f66ae218317f533630cbbddc677d6d893d5722be6947c8fad8e9d52',
    type: ContractType.Wallet,
  },
  v2R1: {
    name: 'v2R1',
    hash: 'fb3bd539b7e50166f1cfdc0bbd298b1c88f6b261fe5ee61343ea47ab4b256029',
    type: ContractType.Wallet,
  },
  v2R2: {
    name: 'v2R2',
    hash: 'b584b6106753b7f34709df505be603e463a44ff6a85adf7fec4e26453c325983',
    type: ContractType.Wallet,
  },
  v3R1: {
    name: 'v3R1',
    hash: '11d123ed5c2055128e75a9ef4cf1e837e6d14a9c079c39939885c78dc13626e6',
    type: ContractType.Wallet,
  },
  v3R2: {
    name: 'v3R2',
    hash: 'df7bf014ee7ac0c38da19ef1b7fa054e2cc7a4513df1f1aa295109cf3606ac14',
    type: ContractType.Wallet,
  },
  v4R1: {
    name: 'v4R1',
    hash: '1bc0dfa40956c911616f8a5db09ecc217601bae48d7d3f9311562c5afcb66dcf',
    type: ContractType.Wallet,
  },
  v4R2: {
    name: 'v4R2',
    hash: '5659ce2300f4a09a37b0bdee41246ded52474f032c1d6ffce0d7d31b18b7b2b1',
    type: ContractType.Wallet,
  },
  W5: {
    name: 'W5',
    hash: '7e94eaaeaaa423b9396e79747038c42edc4fe98dce65094071f0e0ad2df22fd5',
    type: ContractType.Wallet,
  },
  highloadV2: {
    name: 'highloadV2',
    hash: 'fcd7d1f3b3847f0b9bd44bc64a2256c03450979dd1646a24fbc874b075392d6e',
    type: ContractType.Wallet,
  },
  nominatorPool: {
    name: 'nominatorPool',
    hash: '26faa2d0fd2a8197ea36ded8dc50ad081cce5244207e9b05c08c1bb655527bff',
    type: ContractType.Staking,
  },
  multisig: {
    name: 'multisig',
    hash: '45d890485cdd6b152bcbbe3fb2e16d2df82f6da840440a5b9f34ea13cb0b92d2',
    type: ContractType.Wallet,
  },
  multisigV2: {
    name: 'multisigV2',
    hash: 'eb1323c5544d5bf26248dc427d108d722d5c2922dd97dd0bdf903c4cea73ca97',
    type: ContractType.Wallet,
  },
  vesting: {
    name: 'vesting',
    hash: '69dc931958f7aa203c4a7bfcf263d25d2d828d573184b542a65dd55c8398ad83',
    type: ContractType.Wallet,
  },
  multisigNew: {
    name: 'multisigNew',
    hash: '7cb3678880388acff45d74b2e7e7544caa8039d20b49f57c75b53c051b6fa30f',
    type: ContractType.Wallet,
  },
  dedustPool: {
    name: 'dedustPool',
    hash: 'f216ded2b43d32e2d487db6fa6e4d2387f0ef1d7b53ec1ad85f0b4feb8e4ed62',
    isSwapAllowed: true,
  },
  dedustVaultNative: {
    name: 'dedustVaultNative',
    hash: '64a42ad66688097422901ae6188670f0d6292ad3bdb4139289666f24187e86cb',
    isSwapAllowed: true,
  },
  dedustVaultJetton: {
    name: 'dedustVaultJetton',
    hash: '5bc82f0c5972ccc6732e98cbe31ea4795da818f9e06c991331568182a8362307',
    isSwapAllowed: true,
  },
  stonPtonWallet: {
    name: 'stonPtonWallet',
    hash: '6ccbf71a3ed9c7355f84a698a44a7406574bfb8aa34d4bbd86ab75ee9c994880',
    isSwapAllowed: true,
  },
  stonRouter: {
    name: 'stonRouter',
    hash: '14ce618a0e9a94adc99fa6e975219ddd675425b30dfa9728f98714c8dc55f9da',
    isSwapAllowed: true,
  },
  megatonWtonMaster: {
    name: 'megatonWtonMaster',
    hash: '4c9790d808ea4470614e021f76c40529efe2fbce8138da4284a29b5f1943ef19',
    isSwapAllowed: true,
  },
  megatonRouter: {
    name: 'megatonRouter',
    hash: '5d5f0e3ed9602d1ba96006ead98cb5e9b53f49ce4a5cf675e06e4d440b7d267c',
    isSwapAllowed: true,
  },
};
