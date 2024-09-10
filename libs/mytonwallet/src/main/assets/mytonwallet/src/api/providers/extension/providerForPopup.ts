import type { ExtensionMethodArgs, ExtensionMethods } from '../../extensionMethods/types';
import type { MethodArgs, Methods } from '../../methods/types';
import type { ApiInitArgs, OnApiUpdate } from '../../types';

import { POPUP_PORT } from './config';
import { createExtensionInterface } from '../../../util/createPostMessageInterface';
import { disconnectUpdater } from '../../common/helpers';
import * as extensionMethods from '../../extensionMethods';
import initExtensionMethods from '../../extensionMethods/init';
import * as methods from '../../methods';
import initMethods from '../../methods/init';

void createExtensionInterface(POPUP_PORT, (name: string, origin?: string, ...args: any[]) => {
  if (name === 'init') {
    void initMethods(args[0] as OnApiUpdate, args[1] as ApiInitArgs);
    return initExtensionMethods(args[0] as OnApiUpdate);
  } else {
    if (name in extensionMethods) {
      // @ts-ignore
      return extensionMethods[name](...args as ExtensionMethodArgs<keyof ExtensionMethods>);
    }

    const method = methods[name as keyof Methods];
    // @ts-ignore
    return method(...args as MethodArgs<keyof Methods>);
  }
}, undefined, disconnectUpdater);
