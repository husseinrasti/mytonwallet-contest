import extension from 'webextension-polyfill';

import { IS_FIREFOX_EXTENSION, PROXY_HOSTS } from '../../config';
import { sample } from '../../util/random';
import { storage } from '../storages';
import { updateSites } from './sites';

type ProxyType = 'http' | 'https' | 'socks' | 'socks5';

type Proxy = {
  type: ProxyType;
  host: string;
  port: string;
};

type FirefoxProxyInfo = Proxy & {
  username?: string;
  password?: string;
  proxyDNS?: boolean;
  failoverTimeout?: number;
  proxyAuthorizationHeader?: string;
  connectionIsolationKey?: string;
};

const proxies: Proxy[] = (PROXY_HOSTS ?? '').split(' ').map(((hostWithPort) => {
  const [host, port] = hostWithPort.split(':');
  return { type: 'http', host, port };
}));
const proxy = sample(proxies);

// eslint-disable-next-line max-len
const PROXY_PAC_SCRIPT = `function FindProxyForURL(url, host) {
  return host.endsWith('.ton') || host.endsWith('.adnl') || host.endsWith('.bag')
    ? 'PROXY ${proxy.host}:${proxy.port}'
    : 'DIRECT';
}`;

let isProxyEnabled = false;

export async function initExtension() {
  const isTonProxyEnabled = await storage.getItem('isTonProxyEnabled');
  void doProxy(isTonProxyEnabled);

  const isDeeplinkHookEnabled = await storage.getItem('isDeeplinkHookEnabled');
  doDeeplinkHook(isDeeplinkHookEnabled);
}

export function onFullLogout() {
  return storage.removeItem('dapps');
}

export function doProxy(isEnabled: boolean) {
  if (!PROXY_HOSTS) {
    return;
  }

  if (isProxyEnabled === isEnabled) {
    return;
  }

  isProxyEnabled = isEnabled;
  void storage.setItem('isTonProxyEnabled', isEnabled);

  if (IS_FIREFOX_EXTENSION) {
    if (isEnabled) {
      extension.proxy.onRequest.addListener(firefoxOnRequest, {
        urls: ['*://*.ton/*', '*://*.adnl/*', '*://*.bag/*'],
      });
    } else {
      extension.proxy.onRequest.removeListener(firefoxOnRequest);
    }
  } else if (isEnabled) {
    void extension.proxy.settings.set({
      scope: 'regular',
      value: {
        mode: 'pac_script',
        pacScript: {
          data: PROXY_PAC_SCRIPT,
        },
      },
    });
  } else {
    void extension.proxy.settings.clear({
      scope: 'regular',
    });
  }
}

function firefoxOnRequest(): FirefoxProxyInfo | FirefoxProxyInfo[] {
  return proxies;
}

export function doMagic(isEnabled: boolean) {
  void storage.setItem('isTonMagicEnabled', isEnabled);

  updateSites({
    type: 'updateTonMagic',
    isEnabled,
  });
}

export function doDeeplinkHook(isEnabled: boolean) {
  void storage.setItem('isDeeplinkHookEnabled', isEnabled);

  updateSites({
    type: 'updateDeeplinkHook',
    isEnabled,
  });
}
