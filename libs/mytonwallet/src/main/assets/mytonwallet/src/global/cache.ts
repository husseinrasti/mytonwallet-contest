import { addCallback, removeCallback } from '../lib/teact/teactn';

import type {
  AccountState, GlobalState, TokenPeriod, UserToken,
} from './types';

import {
  DEBUG,
  DEFAULT_DECIMAL_PLACES,
  GLOBAL_STATE_CACHE_DISABLED,
  GLOBAL_STATE_CACHE_KEY,
  IS_CAPACITOR,
  MAIN_ACCOUNT_ID,
  TONCOIN_SLUG,
} from '../config';
import { buildAccountId, parseAccountId } from '../util/account';
import { bigintReviver } from '../util/bigint';
import {
  cloneDeep, mapValues, pick, pickTruthy,
} from '../util/iteratees';
import { onBeforeUnload, onIdle, throttle } from '../util/schedulers';
import { IS_ELECTRON } from '../util/windowEnvironment';
import { getIsTxIdLocal } from './helpers';
import { addActionHandler, getGlobal } from './index';
import { INITIAL_STATE, STATE_VERSION } from './initialState';
import { selectAccountTokens } from './selectors';

import { isHeavyAnimating } from '../hooks/useHeavyAnimationCheck';

const UPDATE_THROTTLE = IS_CAPACITOR ? 500 : 5000;
const ACTIVITIES_LIMIT = 20;
const ACTIVITY_TOKENS_LIMIT = 30;

const updateCacheThrottled = throttle(() => onIdle(() => updateCache()), UPDATE_THROTTLE, false);
const updateCacheForced = () => updateCache(true);

let isCaching = false;
let unsubscribeFromBeforeUnload: NoneToVoidFunction | undefined;
let preloadedData: Partial<GlobalState> | undefined;

export function initCache() {
  if (GLOBAL_STATE_CACHE_DISABLED) {
    return;
  }

  addActionHandler('afterSignIn', setupCaching);

  addActionHandler('afterSignOut', (global, actions, payload) => {
    const { isFromAllAccounts } = payload || {};
    if (!isFromAllAccounts) return;

    preloadedData = pick(global, ['swapTokenInfo', 'tokenInfo', 'restrictions']);

    clearCaching();

    localStorage.removeItem(GLOBAL_STATE_CACHE_KEY);
  });

  addActionHandler('cancelCaching', clearCaching);
}

function setupCaching() {
  if (isCaching) return;

  isCaching = true;

  addCallback(updateCacheThrottled);
  unsubscribeFromBeforeUnload = onBeforeUnload(updateCacheForced, true);
  window.addEventListener('blur', updateCacheForced);

  updateCacheForced();
}

function clearCaching() {
  if (!isCaching) return;

  window.removeEventListener('blur', updateCacheForced);
  unsubscribeFromBeforeUnload?.();
  removeCallback(updateCacheThrottled);

  isCaching = false;
}

export function loadCache(initialState: GlobalState): GlobalState {
  if (GLOBAL_STATE_CACHE_DISABLED) {
    return initialState;
  }

  if (DEBUG) {
    // eslint-disable-next-line no-console
    console.time('global-state-cache-read');
  }

  const json = localStorage.getItem(GLOBAL_STATE_CACHE_KEY);
  let cached = json ? JSON.parse(json, bigintReviver) as GlobalState : undefined;

  if (DEBUG) {
    // eslint-disable-next-line no-console
    console.timeEnd('global-state-cache-read');
  }

  if (cached) {
    try {
      migrateCache(cached, initialState);
    } catch (err) {
      // eslint-disable-next-line no-console
      console.error(err);

      cached = undefined;
    }
  }

  return {
    ...initialState,
    ...preloadedData,
    ...cached,
  };
}

function migrateCache(cached: GlobalState, initialState: GlobalState) {
  // Pre-fill settings with defaults
  cached.settings = {
    ...initialState.settings,
    ...cached.settings,
  };

  if (cached.stateVersion === STATE_VERSION) {
    return;
  }

  // Migration to multi-accounts
  if (!cached.byAccountId) {
    cached.accounts = {
      byId: {
        [MAIN_ACCOUNT_ID]: {
          address: (cached as any).addresses.byAccountId[MAIN_ACCOUNT_ID],
          title: 'Main Account',
        },
      },
    };

    delete (cached as any).addresses;

    cached.byAccountId = {};
    cached.byAccountId[MAIN_ACCOUNT_ID] = {
      isBackupRequired: Boolean((cached as any).isBackupRequired),
      currentTokenSlug: (cached as any).currentTokenSlug as string,
      currentTokenPeriod: (cached as any).currentTokenPeriod as TokenPeriod,
    };

    if ('balances' in cached) {
      cached.byAccountId[MAIN_ACCOUNT_ID].balances = (cached as any).balances.byAccountId[MAIN_ACCOUNT_ID];
      delete (cached as any).balances;
    }

    if ('transactions' in cached) {
      (cached.byAccountId[MAIN_ACCOUNT_ID] as any).transactions = (cached as any).transactions;
      delete (cached as any).transactions;
    }

    if ('nfts' in cached) {
      cached.byAccountId[MAIN_ACCOUNT_ID].nfts = (cached as any).nfts;
      delete (cached as any).nfts;
    }

    if ('savedAddresses' in cached) {
      cached.byAccountId[MAIN_ACCOUNT_ID].savedAddresses = (cached as any).savedAddresses;
      delete (cached as any).savedAddresses;
    }

    if ('backupWallet' in cached) {
      delete (cached as any).backupWallet;
    }
  }

  if (
    (!cached.currentAccountId || !cached.byAccountId[cached.currentAccountId]) && Object.keys(cached.byAccountId).length
  ) {
    cached.currentAccountId = Object.keys(cached.byAccountId)[0];
  }

  // Initializing the v1
  if (!cached.stateVersion && cached.accounts && Object.keys(cached.accounts).length > 0) {
    cached.stateVersion = 1;
  }

  if (cached.stateVersion === 1) {
    cached.stateVersion = 2;

    if (cached.tokenInfo?.bySlug) {
      cached.tokenInfo.bySlug = {
        toncoin: {
          ...cached.tokenInfo.bySlug.toncoin,
          decimals: DEFAULT_DECIMAL_PLACES,
        },
      };
    }

    if (cached.byAccountId) {
      Object.values(cached.byAccountId).forEach((accountState) => {
        if (accountState.balances?.bySlug) {
          accountState.balances.bySlug = pick(accountState.balances!.bySlug, ['toncoin']);
        }
        if ((accountState as any).transactions) {
          delete (accountState as any).transactions;
        }
      });
    }
  }

  if (cached.stateVersion === 2) {
    cached.stateVersion = 3;

    // Normalization of MAIN_ACCOUNT_ID '0' => '0-ton-mainnet'
    const oldId = '0';
    const newId = MAIN_ACCOUNT_ID;
    if (cached.accounts && oldId in cached.accounts.byId) {
      if (cached.currentAccountId === oldId) {
        cached.currentAccountId = newId;
      }
      cached.accounts.byId[newId] = cached.accounts.byId[oldId];
      delete cached.accounts.byId[oldId];
      cached.byAccountId[newId] = cached.byAccountId[oldId];
      delete cached.byAccountId[oldId];
    }

    // Add testnet accounts
    if (cached.accounts) {
      for (const accountId of Object.keys(cached.accounts.byId)) {
        const testnetAccountId = buildAccountId({
          ...parseAccountId(accountId),
          network: 'testnet',
        });
        cached.accounts.byId[testnetAccountId] = cloneDeep(cached.accounts.byId[accountId]);
        cached.byAccountId[testnetAccountId] = {};
      }
    }
  }

  if (cached.stateVersion === 3) {
    cached.stateVersion = 4;

    if (cached.byAccountId) {
      for (const accountId of Object.keys(cached.byAccountId)) {
        delete (cached.byAccountId[accountId] as any).transactions;
      }
    }
  }

  if (cached.stateVersion === 4) {
    cached.stateVersion = 5;

    cached.staking = {
      ...initialState.staking,
    };
  }

  if (cached.stateVersion === 5) {
    cached.stateVersion = 6;

    if (cached.byAccountId) {
      for (const accountId of Object.keys(cached.byAccountId)) {
        delete (cached.byAccountId[accountId] as any).transactions;
      }
    }
  }

  if (cached.stateVersion === 6) {
    cached.stateVersion = 7;

    if (cached.byAccountId) {
      for (const accountId of Object.keys(cached.byAccountId)) {
        delete (cached.byAccountId[accountId] as any).transactions;
      }
    }
  }

  if (cached.stateVersion === 7) {
    if (cached.byAccountId) {
      for (const accountId of Object.keys(cached.byAccountId)) {
        delete (cached.byAccountId[accountId] as any).backupWallet;
      }
    }

    cached.stateVersion = 8;
  }

  if (cached.stateVersion === 8) {
    if (cached.settings && IS_ELECTRON) {
      cached.settings.isDeeplinkHookEnabled = true;
    }

    cached.stateVersion = 9;
  }

  function clearActivities() {
    if (cached.byAccountId) {
      for (const accountId of Object.keys(cached.byAccountId)) {
        delete (cached.byAccountId[accountId] as any).activities;
      }
    }
  }

  if (cached.stateVersion === 9) {
    clearActivities();
    cached.stateVersion = 10;
  }

  if (cached.stateVersion === 10) {
    if ((cached.settings as any).areTokensWithNoBalanceHidden === undefined) {
      (cached.settings as any).areTokensWithNoBalanceHidden = true;
    }
    cached.stateVersion = 11;
  }

  if (cached.stateVersion === 11) {
    clearActivities();
    cached.stateVersion = 12;
  }

  if (cached.stateVersion === 12) {
    if (cached.byAccountId) {
      for (const accountId of Object.keys(cached.byAccountId)) {
        delete cached.byAccountId[accountId].activities;

        const { balances } = cached.byAccountId[accountId];
        if (balances) {
          // eslint-disable-next-line @typescript-eslint/no-loop-func
          balances.bySlug = Object.entries(balances.bySlug).reduce((acc, [slug, balance]) => {
            acc[slug] = BigInt(balance);
            return acc;
          }, {} as Record<string, bigint>);
        }
      }
    }
    cached.stateVersion = 13;
  }

  if (cached.stateVersion === 13) {
    const { areTokensWithNoPriceHidden, areTokensWithNoBalanceHidden } = cached.settings as any as {
      areTokensWithNoPriceHidden?: boolean;
      areTokensWithNoBalanceHidden?: boolean;
    };

    cached.settings.areTokensWithNoCostHidden = Boolean(areTokensWithNoPriceHidden || areTokensWithNoBalanceHidden);
    cached.stateVersion = 14;
  }

  if (cached.stateVersion === 14) {
    clearActivities();
    cached.stateVersion = 15;
  }

  if (cached.stateVersion === 15) {
    clearActivities();
    cached.stateVersion = 16;
  }

  if (cached.stateVersion === 16) {
    clearActivities();
    cached.stateVersion = 17;
  }

  if (cached.stateVersion === 17) {
    clearActivities();
    cached.stateVersion = 18;
  }

  if (cached.stateVersion === 18 || cached.stateVersion === 19) {
    for (const accountId of Object.keys(cached.byAccountId)) {
      cached.byAccountId[accountId].currentTokenPeriod = '1D';
    }
    cached.stateVersion = 20;
  }

  if (cached.stateVersion === 20) {
    clearActivities();
    cached.stateVersion = 21;
  }

  if (cached.stateVersion === 21) {
    clearActivities();
    cached.stateVersion = 22;
  }

  if (cached.stateVersion === 22) {
    clearActivities();
    cached.stateVersion = 23;
  }

  if (cached.stateVersion === 23) {
    if (!('isSortByValueEnabled' in cached.settings)) {
      cached.settings.isSortByValueEnabled = initialState.settings.isSortByValueEnabled;
    }
    cached.stateVersion = 24;
  }
  // When adding migration here, increase `STATE_VERSION`
}

function updateCache(force?: boolean) {
  if (GLOBAL_STATE_CACHE_DISABLED || !isCaching || (!force && isHeavyAnimating())) {
    return;
  }

  const global = getGlobal();
  const reducedGlobal: GlobalState = {
    ...INITIAL_STATE,
    ...pick(global, [
      'tokenInfo',
      'settings',
      'currentAccountId',
      'stateVersion',
      'restrictions',
    ]),
    accounts: {
      byId: global.accounts?.byId || {},
    },
    byAccountId: reduceByAccountId(global),
  };

  const json = JSON.stringify(reducedGlobal);
  localStorage.setItem(GLOBAL_STATE_CACHE_KEY, json);
}

function reduceByAccountId(global: GlobalState) {
  return Object.entries(global.byAccountId).reduce((acc, [accountId, state]) => {
    acc[accountId] = pick(state, [
      'balances',
      'isBackupRequired',
      'currentTokenSlug',
      'currentTokenPeriod',
      'savedAddresses',
      'staking',
      'stakingHistory',
      'activeContentTab',
      'landscapeActionsActiveTabIndex',
      'browserHistory',
      'blacklistedNftAddresses',
      'whitelistedNftAddresses',
    ]);

    const accountTokens = selectAccountTokens(global, accountId);
    acc[accountId].activities = reduceAccountActivities(state.activities, accountTokens);

    return acc;
  }, {} as GlobalState['byAccountId']);
}

function reduceAccountActivities(activities?: AccountState['activities'], tokens?: UserToken[]) {
  const { idsBySlug, newestTransactionsBySlug, byId } = activities || {};
  if (!tokens || !idsBySlug || !byId) return undefined;

  const reducedSlugs = tokens.slice(0, ACTIVITY_TOKENS_LIMIT).map(({ slug }) => slug);
  if (!reducedSlugs.includes(TONCOIN_SLUG)) {
    reducedSlugs.push(TONCOIN_SLUG);
  }

  const reducedIdsBySlug = mapValues(pickTruthy(idsBySlug, reducedSlugs), (ids) => {
    const result: string[] = [];

    let visibleIdCount = 0;

    ids
      .filter((id) => !getIsTxIdLocal(id) && Boolean(byId[id]))
      .forEach((id) => {
        if (visibleIdCount === ACTIVITIES_LIMIT) return;

        if (!byId[id].shouldHide) {
          visibleIdCount += 1;
        }

        result.push(id);
      });

    return result;
  });

  const reducedNewestTransactionsBySlug = newestTransactionsBySlug
    ? pick(newestTransactionsBySlug, reducedSlugs)
    : undefined;

  const reducedIds = Object.values(reducedIdsBySlug).flat();
  const reducedById = pick(byId, reducedIds);

  return {
    byId: reducedById,
    idsBySlug: reducedIdsBySlug,
    newestTransactionsBySlug: reducedNewestTransactionsBySlug,
  };
}
