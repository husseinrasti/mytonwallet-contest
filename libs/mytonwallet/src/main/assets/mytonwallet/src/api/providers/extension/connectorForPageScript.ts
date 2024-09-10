import type { Connector } from '../../../util/PostMessageConnector';
import type {
  LegacyDappMethodResponse,
  LegacyDappMethods,
  SiteMethodResponse,
  SiteMethods,
} from '../../extensionMethods/types';
import type { OnApiSiteUpdate } from '../../types/dappUpdates';

import { PAGE_CONNECTOR_CHANNEL } from './config';
import { logDebugError } from '../../../util/logs';
import { createConnector } from '../../../util/PostMessageConnector';

let connector: Connector;

type Methods = SiteMethods & LegacyDappMethods;
type MethodResponse<T extends keyof Methods> = (
  T extends keyof SiteMethods
    ? SiteMethodResponse<T>
    : T extends keyof LegacyDappMethods
      ? LegacyDappMethodResponse<T>
      : never
  );

export function initApi(onUpdate: OnApiSiteUpdate) {
  connector = createConnector(window, onUpdate, PAGE_CONNECTOR_CHANNEL, window.location.href);
  return connector;
}

export function callApi<T extends keyof Methods>(methodName: T, ...args: any[]) {
  if (!connector) {
    logDebugError('API is not initialized');
    return undefined;
  }

  const promise = connector.request({
    name: methodName,
    args,
  });

  return promise as MethodResponse<T>;
}
