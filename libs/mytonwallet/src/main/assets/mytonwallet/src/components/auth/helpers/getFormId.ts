import type { AuthMethod } from '../../../global/types';

// eslint-disable-next-line consistent-return
export function getFormId(method: AuthMethod) {
  switch (method) {
    case 'createAccount':
      return 'auth_create_password';
    case 'importMnemonic':
      return 'auth_import_mnemonic_password';
    case 'importHardwareWallet':
      return 'auth_import_hardware_password';
  }
}
