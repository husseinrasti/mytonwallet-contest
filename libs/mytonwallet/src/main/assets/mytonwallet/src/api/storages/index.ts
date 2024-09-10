import { StorageType } from './types';

import { IS_CAPACITOR, IS_EXTENSION } from '../../config';
import capacitorStorage from './capacitorStorage';
import extensionStorage from './extension';
import idb from './idb';
import localStorage from './localStorage';

export const storage = IS_EXTENSION ? extensionStorage : IS_CAPACITOR ? capacitorStorage : idb;

export default {
  [StorageType.IndexedDb]: idb,
  [StorageType.LocalStorage]: localStorage,
  [StorageType.ExtensionLocal]: extensionStorage,
  [StorageType.CapacitorStorage]: capacitorStorage,
};
