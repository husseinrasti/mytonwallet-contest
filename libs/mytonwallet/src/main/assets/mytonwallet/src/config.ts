import type { ApiBaseCurrency, ApiSwapAsset, ApiWalletVersion } from './api/types';
import type { LangItem, TokenPeriod } from './global/types';

export const APP_ENV = process.env.APP_ENV;

export const APP_NAME = process.env.APP_NAME || 'MyTonWallet';
export const APP_VERSION = process.env.APP_VERSION!;
export const APP_ENV_MARKER = APP_ENV === 'staging' ? 'Beta' : APP_ENV === 'development' ? 'Dev' : undefined;

export const DEBUG = APP_ENV !== 'production' && APP_ENV !== 'perf' && APP_ENV !== 'test';
export const DEBUG_MORE = false;

export const IS_PRODUCTION = APP_ENV === 'production';
export const IS_TEST = APP_ENV === 'test';
export const IS_PERF = APP_ENV === 'perf';
export const IS_EXTENSION = process.env.IS_EXTENSION === '1';
export const IS_FIREFOX_EXTENSION = process.env.IS_FIREFOX_EXTENSION === '1';
export const IS_PACKAGED_ELECTRON = process.env.IS_PACKAGED_ELECTRON === '1';
export const IS_CAPACITOR = process.env.IS_CAPACITOR === '1';
export const IS_ANDROID_DIRECT = process.env.IS_ANDROID_DIRECT === '1';

export const ELECTRON_HOST_URL = 'https://dumb-host';
export const INACTIVE_MARKER = '[Inactive]';
export const PRODUCTION_URL = 'https://mytonwallet.app';
export const BETA_URL = 'https://beta.mytonwallet.app';
export const APP_REPO_URL = 'https://github.com/mytonwalletorg/mytonwallet';
export const BASE_URL = process.env.BASE_URL;

export const BOT_USERNAME = process.env.BOT_USERNAME || 'MyTonWalletBot';

export const SWAP_FEE_ADDRESS = process.env.SWAP_FEE_ADDRESS || 'UQDUkQbpTVIgt7v66-JTFR-3-eXRFz_4V66F-Ufn6vOg0GOp';

export const STRICTERDOM_ENABLED = DEBUG && !IS_PACKAGED_ELECTRON;

export const DEBUG_ALERT_MSG = 'Shoot!\nSomething went wrong, please see the error details in Dev Tools Console.';

export const PIN_LENGTH = 4;
export const NATIVE_BIOMETRICS_USERNAME = 'MyTonWallet';
export const NATIVE_BIOMETRICS_SERVER = 'https://mytonwallet.app';

export const MNEMONIC_COUNT = 24;
export const PRIVATE_KEY_HEX_LENGTH = 64;
export const MNEMONIC_CHECK_COUNT = 3;

export const MOBILE_SCREEN_MAX_WIDTH = 700; // px

export const ANIMATION_END_DELAY = 50;

export const ANIMATED_STICKER_TINY_SIZE_PX = 70;
export const ANIMATED_STICKER_SMALL_SIZE_PX = 110;
export const ANIMATED_STICKER_MIDDLE_SIZE_PX = 120;
export const ANIMATED_STICKER_DEFAULT_PX = 150;
export const ANIMATED_STICKER_BIG_SIZE_PX = 156;
export const ANIMATED_STICKER_HUGE_SIZE_PX = 192;

export const TON_SYMBOL = 'TON';

export const DEFAULT_LANDSCAPE_ACTION_TAB_ID = 0;

export const DEFAULT_DECIMAL_PLACES = 9;
export const WHOLE_PART_DELIMITER = ' '; // https://www.compart.com/en/unicode/U+202F

export const DEFAULT_SLIPPAGE_VALUE = 5;

export const GLOBAL_STATE_CACHE_DISABLED = false;
export const GLOBAL_STATE_CACHE_KEY = 'mytonwallet-global-state';

export const ANIMATION_LEVEL_MIN = 0;
export const ANIMATION_LEVEL_MED = 1;
export const ANIMATION_LEVEL_MAX = 2;
export const ANIMATION_LEVEL_DEFAULT = ANIMATION_LEVEL_MAX;
export const THEME_DEFAULT = 'system';

export const MAIN_ACCOUNT_ID = '0-ton-mainnet';

export const TONHTTPAPI_MAINNET_URL = process.env.TONHTTPAPI_MAINNET_URL
  || 'https://tonhttpapi.mytonwallet.org/api/v2/jsonRPC';
export const TONHTTPAPI_MAINNET_API_KEY = process.env.TONHTTPAPI_MAINNET_API_KEY;
export const ELECTRON_TONHTTPAPI_MAINNET_API_KEY = process.env.ELECTRON_TONHTTPAPI_MAINNET_API_KEY;
export const TONHTTPAPI_V3_MAINNET_API_URL = process.env.TONHTTPAPI_V3_MAINNET_API_KEY
  || 'https://tonhttpapi-v3.mytonwallet.org/api/v3';
export const TONAPIIO_MAINNET_URL = process.env.TONAPIIO_MAINNET_URL || 'https://tonapiio.mytonwallet.org';

export const TONHTTPAPI_TESTNET_URL = process.env.TONHTTPAPI_TESTNET_URL
  || 'https://tonhttpapi-testnet.mytonwallet.org/api/v2/jsonRPC';
export const TONHTTPAPI_TESTNET_API_KEY = process.env.TONHTTPAPI_TESTNET_API_KEY;
export const ELECTRON_TONHTTPAPI_TESTNET_API_KEY = process.env.ELECTRON_TONHTTPAPI_TESTNET_API_KEY;
export const TONHTTPAPI_V3_TESTNET_API_URL = process.env.TONHTTPAPI_V3_TESTNET_API_KEY
  || 'https://tonhttpapi-v3-testnet.mytonwallet.org/api/v3';
export const TONAPIIO_TESTNET_URL = process.env.TONAPIIO_TESTNET_URL || 'https://tonapiio-testnet.mytonwallet.org';

export const BRILLIANT_API_BASE_URL = process.env.BRILLIANT_API_BASE_URL || 'https://api.mytonwallet.org';

export const FRACTION_DIGITS = 9;
export const SHORT_FRACTION_DIGITS = 2;

export const SUPPORT_USERNAME = 'MyTonWalletSupport';
export const MY_TON_WALLET_PROMO_URL = 'https://mytonwallet.io';
export const TELEGRAM_WEB_URL = 'https://web.telegram.org/a/';
export const TON_EXPLORER_BASE_MAINNET_URL = 'https://tonviewer.com/';
export const TON_EXPLORER_BASE_TESTNET_URL = 'https://testnet.tonviewer.com/';
export const TON_EXPLORER_NAME = 'Tonviewer';
export const TOKEN_EXPLORER_MAINNET_URL = 'https://tonviewer.com/{address}?section=jetton';
export const TOKEN_EXPLORER_TESTNET_URL = 'https://testnet.tonviewer.com/{address}?section=jetton';
export const TOKEN_EXPLORER_NAME = 'TonViewer';
export const GETGEMS_BASE_MAINNET_URL = 'https://getgems.io/';
export const GETGEMS_BASE_TESTNET_URL = 'https://testnet.getgems.io/';
export const EMPTY_HASH_VALUE = 'NOHASH';

export const CHANGELLY_SUPPORT_EMAIL = 'support@changelly.com';
export const CHANGELLY_LIVE_CHAT_URL = 'https://changelly.com/';
export const CHANGELLY_SECURITY_EMAIL = 'security@changelly.com';
export const CHANGELLY_TERMS_OF_USE = 'https://changelly.com/terms-of-use';
export const CHANGELLY_PRIVACY_POLICY = 'https://changelly.com/privacy-policy';
export const CHANGELLY_AML_KYC = 'https://changelly.com/aml-kyc';
export const CHANGELLY_WAITING_DEADLINE = 3 * 60 * 60 * 1000; // 3 hour

export const TONCOIN_SLUG = 'toncoin';
export const DEFAULT_SWAP_SECOND_TOKEN_SLUG = 'ton-eqcxe6mutq'; // USD₮
export const DEFAULT_CEX_SWAP_SECOND_TOKEN_SLUG = 'usdtrx';

export const PROXY_HOSTS = process.env.PROXY_HOSTS;

export const TINY_TRANSFER_MAX_COST = 0.01;

export const LANG_CACHE_NAME = 'mtw-lang-126';

export const LANG_LIST: LangItem[] = [{
  langCode: 'en',
  name: 'English',
  nativeName: 'English',
  rtl: false,
}, {
  langCode: 'es',
  name: 'Spanish',
  nativeName: 'Español',
  rtl: false,
}, {
  langCode: 'ru',
  name: 'Russian',
  nativeName: 'Русский',
  rtl: false,
}, {
  langCode: 'zh-Hans',
  name: 'Chinese (Simplified)',
  nativeName: '简体',
  rtl: false,
}, {
  langCode: 'zh-Hant',
  name: 'Chinese (Traditional)',
  nativeName: '繁體',
  rtl: false,
}, {
  langCode: 'tr',
  name: 'Turkish',
  nativeName: 'Türkçe',
  rtl: false,
}, {
  langCode: 'de',
  name: 'German',
  nativeName: 'Deutsch',
  rtl: false,
}, {
  langCode: 'th',
  name: 'Thai',
  nativeName: 'ไทย',
  rtl: false,
}, {
  langCode: 'uk',
  name: 'Ukrainian',
  nativeName: 'Українська',
  rtl: false,
}, {
  langCode: 'pl',
  name: 'Polish',
  nativeName: 'Polski',
  rtl: false,
}];

export const STAKING_CYCLE_DURATION_MS = 131072000; // 36.4 hours
export const VALIDATION_PERIOD_MS = 65536000; // 18.2 h.
export const ONE_TON = 1000000000n;
export const MIN_BALANCE_FOR_UNSTAKE = 1020000000n; // 1.02 TON
export const STAKING_FORWARD_AMOUNT = ONE_TON;
export const DEFAULT_FEE = 15000000n; // 0.015 TON

export const STAKING_POOLS = process.env.STAKING_POOLS ? process.env.STAKING_POOLS.split(' ') : [];
export const LIQUID_POOL = process.env.LIQUID_POOL || 'EQD2_4d91M4TVbEBVyBF8J1UwpMJc361LKVCz6bBlffMW05o';
export const LIQUID_JETTON = process.env.LIQUID_JETTON || 'EQCqC6EhRJ_tpWngKxL6dV0k6DSnRUrs9GSVkLbfdCqsj6TE';
export const STAKING_MIN_AMOUNT = ONE_TON;
export const NOMINATORS_STAKING_MIN_AMOUNT = ONE_TON * 10001n;

export const TONCONNECT_PROTOCOL_VERSION = 2;
export const TONCONNECT_WALLET_JSBRIDGE_KEY = 'mytonwallet';

export const NFT_FRAGMENT_COLLECTIONS = new Set([
  '0:0e41dc1dc3c9067ed24248580e12b3359818d83dee0304fabcf80845eafafdb2', // Anonymous Telegram Numbers
  '0:80d78a35f955a14b679faa887ff4cd5bfc0f43b4a4eea2a7e6927f3701b273c2', // Telegram Usernames
]);

export const TON_DNS_COLLECTION = 'EQC3dNlesgVD8YbAazcauIrXBPfiVhMMr5YYk2in0Mtsz0Bz';
export const MYCOIN_TOKEN = 'EQCFVNlRb-NHHDQfv3Q9xvDXBLJlay855_xREsq5ZDX6KN-w';
export const MYCOIN_SLUG = 'ton-eqcfvnlrbn';
export const MYCOIN_TOKEN_TESTNET = 'kQAWlxpEbwhCDFX9gp824ee2xVBhAh5VRSGWfbNFDddAbQoQ';
export const MYCOIN_SLUG_TESTNET = 'ton-kqawlxpebw';

export const TOKEN_INFO = {
  toncoin: {
    name: 'Toncoin',
    symbol: TON_SYMBOL,
    slug: TONCOIN_SLUG,
    cmcSlug: TONCOIN_SLUG,
    quote: {
      slug: TONCOIN_SLUG,
      price: 1.95,
      priceUsd: 1.95,
      percentChange24h: 0,
    },
    decimals: DEFAULT_DECIMAL_PLACES,
  },
};

export const TON_BLOCKCHAIN = 'ton';

export const INIT_SWAP_ASSETS: Record<string, ApiSwapAsset> = {
  toncoin: {
    name: 'Toncoin',
    symbol: TON_SYMBOL,
    blockchain: TON_BLOCKCHAIN,
    slug: TONCOIN_SLUG,
    decimals: DEFAULT_DECIMAL_PLACES,
    price: 0,
    priceUsd: 0,
    isPopular: true,
  },
  'ton-eqcxe6mutq': {
    name: 'Tether USD',
    symbol: 'USD₮',
    blockchain: TON_BLOCKCHAIN,
    slug: 'ton-eqcxe6mutq',
    decimals: 9,
    // eslint-disable-next-line max-len
    image: 'https://cache.tonapi.io/imgproxy/T3PB4s7oprNVaJkwqbGg54nexKE0zzKhcrPv8jcWYzU/rs:fill:200:200:1/g:no/aHR0cHM6Ly90ZXRoZXIudG8vaW1hZ2VzL2xvZ29DaXJjbGUucG5n.webp',
    contract: 'EQCxE6mUtQJKFnGfaROTKOt1lZbDiiX1kCixRv7Nw2Id_sDs',
    price: 0,
    priceUsd: 0,
    isPopular: true,
  },
};

export const MULTITAB_DATA_CHANNEL_NAME = 'mtw-multitab';
export const ACTIVE_TAB_STORAGE_KEY = 'mtw-active-tab';

export const INDEXED_DB_NAME = 'keyval-store';
export const INDEXED_DB_STORE_NAME = 'keyval';

export const WINDOW_PROVIDER_CHANNEL = 'windowProvider';

export const MIN_ASSETS_TAB_VIEW = 5;

export const DEFAULT_PRICE_CURRENCY = 'USD';
export const SHORT_CURRENCY_SYMBOL_MAP = {
  USD: '$',
  EUR: '€',
  RUB: '₽',
  CNY: '¥',
};
export const CURRENCY_LIST: { value: ApiBaseCurrency; name: string }[] = [
  {
    value: 'USD',
    name: 'US Dollar',
  }, {
    value: 'EUR',
    name: 'Euro',
  }, {
    value: 'RUB',
    name: 'Ruble',
  }, {
    value: 'CNY',
    name: 'Yuan',
  }, {
    value: 'BTC',
    name: 'Bitcoin',
  }, {
    value: TON_SYMBOL,
    name: 'Toncoin',
  },
];

export const BURN_ADDRESS = 'UQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAJKZ';

export const DEFAULT_WALLET_VERSION: ApiWalletVersion = 'W5';
export const POPULAR_WALLET_VERSIONS: ApiWalletVersion[] = ['v3R1', 'v3R2', 'v4R2', 'W5'];

export const DEFAULT_TIMEOUT = 10000;
export const DEFAULT_RETRIES = 3;
export const DEFAULT_ERROR_PAUSE = 500;

export const HISTORY_PERIODS: TokenPeriod[] = ['1D', '7D', '1M', '3M', '1Y', 'ALL'];

export const BROWSER_HISTORY_LIMIT = 10;

export const NFT_BATCH_SIZE = 4;
export const NOTCOIN_VOUCHERS_ADDRESS = 'EQDmkj65Ab_m0aZaW8IpKw4kYqIgITw_HRstYEkVQ6NIYCyW';
export const BURN_CHUNK_DURATION_APPROX_SEC = 30;
export const NOTCOIN_FORWARD_TON_AMOUNT = 30000000n; // 0.03 TON
export const NOTCOIN_EXCHANGERS = [
  'EQAPZauWVPUcm2hUJT9n36pxznEhl46rEn1bzBXN0RY_yiy2',
  'EQASgm0Qv3h2H2mF0W06ikPqYq2ctT3dyXMJH_svbEKKB3iZ',
  'EQArlmP-RhVIG2yAFGZyPZfM3m0YccxmpvoRi6sgRzWnAA0s',
  'EQA6pL-spYqZp1Ck6o3rpY45Cl-bvLMW_j3qdVejOkUWpLnm',
  'EQBJ_ehYjumQKbXfWUue1KHKXdTm1GuYJB0Fj2ST_DwORvpd',
  'EQBRmYSjxh9xlZpUqEmGjF5UjukI9v_Cm2kCTu4CoBn3XkOD',
  'EQBkiqncd7AFT5_23H-RoA2Vynk-Nzq_dLoeMVRthAU9RF0p',
  'EQB_OzTHXbztABe0QHgr4PtAV8T64LR6aDunXgaAoihOdxwO',
  'EQCL-x5kLg6tKVNGryItTuj6tG3FH5mhUEu0xRqQc-kbEmbe',
  'EQCZh2yJ46RaQH3AYmjEA8SMMXi77Oein4-3lvqkHseIAhD-',
  'EQChKo5IK3iNqUHUGDB9gtzjCjMTPtmsFqekuCA2MdreVEyu',
  'EQC6DNCBv076TIliRMfOt20RpbS7rNKDfSky3WrFEapFt8AH',
  'EQDE_XFZOYae_rl3ZMsgBCtRSmYhl8B4y2BZEP7oiGBDhlgy',
  'EQDddqpGA2ePXQF47A2DSL3GF6ZzIVmimfM2d16cdymy2noT',
  'EQDv0hNNAamhYltCh3pTJrq3oRB9RW2ZhEYkTP6fhj5BtZNu',
  'EQD2mP7zgO7-imUJhqYry3i07aJ_SR53DaokMupfAAobt0Xw',
] as const;

export const CLAIM_ADDRESS = 'EQB3zOTvPi1PmwdcTpqSfFKZnhi1GNKEVJM-LdoAirdLtash';
export const CLAIM_AMOUNT = 30000000n; // 0.03 TON
export const CLAIM_COMMENT = 'claim';

// eslint-disable-next-line max-len
export const RE_LINK_TEMPLATE = /((ftp|https?):\/\/)?(?<host>(www\\.)?[-a-zA-Z0-9@:%._+~#=]{1,256}\.[a-zA-Z][-a-zA-Z0-9]{1,62})\b([-a-zA-Z0-9()@:%_+.,~#?&/=]*)/g;
// eslint-disable-next-line max-len
export const RE_TG_BOT_MENTION = /telegram[:\s-]*((@[a-z0-9_]+)|(https:\/\/)?(t\.me|telegram\.me|telegram\.dog)\/[a-z0-9_]+)/mig;

export const DIESEL_ADDRESS = process.env.DIESEL_ADDRESS || 'EQDUkQbpTVIgt7v66-JTFR-3-eXRFz_4V66F-Ufn6vOg0D5s';
export const DIESEL_TOKENS = new Set([
  'EQAvlWFDxGF2lXm67y4yzC17wYKD9A0guwPkMs1gOsM__NOT', // NOT
  'EQCxE6mUtQJKFnGfaROTKOt1lZbDiiX1kCixRv7Nw2Id_sDs', // USDT
  'EQCvxJy4eG8hyHBFsZ7eePxrRsUQSFE_jpptRAYBmcG_DOGS', // DOGS
]);

export const TINY_TOKENS = DIESEL_TOKENS;
