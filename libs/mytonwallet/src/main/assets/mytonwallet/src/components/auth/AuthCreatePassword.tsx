import React, { memo } from '../../lib/teact/teact';
import { getActions } from '../../global';

import type { AuthMethod } from '../../global/types';

import buildClassName from '../../util/buildClassName';
import { getIsBiometricAuthSupported } from '../../util/capacitor';
import { ANIMATED_STICKERS_PATHS } from '../ui/helpers/animatedAssets';

import useHistoryBack from '../../hooks/useHistoryBack';
import useLang from '../../hooks/useLang';
import useLastCallback from '../../hooks/useLastCallback';

import AnimatedIconWithPreview from '../ui/AnimatedIconWithPreview';
import CreatePasswordForm from '../ui/CreatePasswordForm';

import styles from './Auth.module.scss';

interface OwnProps {
  isActive?: boolean;
  method?: AuthMethod;
  isLoading?: boolean;
}

const AuthCreatePassword = ({
  isActive,
  method,
  isLoading,
}: OwnProps) => {
  const { afterCreatePassword, resetAuth } = getActions();

  const lang = useLang();
  const isImporting = method !== 'createAccount';
  const formId = getFormId(method!);
  const withDescription = !getIsBiometricAuthSupported();

  useHistoryBack({
    isActive,
    onBack: resetAuth,
  });

  const handleSubmit = useLastCallback((password: string, isPasswordNumeric: boolean) => {
    afterCreatePassword({ password, isPasswordNumeric });
  });

  return (
    <div className={buildClassName(styles.container, styles.container_scrollable, 'custom-scroll')}>
      <AnimatedIconWithPreview
        play={isActive}
        tgsUrl={ANIMATED_STICKERS_PATHS.guard}
        previewUrl={ANIMATED_STICKERS_PATHS.guardPreview}
        noLoop={false}
        nonInteractive
        className={styles.sticker}
      />
      <div className={buildClassName(styles.title, !withDescription && styles.titleSmallMargin)}>
        {lang(withDescription ? 'Congratulations!' : 'Create Password')}
      </div>
      {withDescription && (
        <>
          <p className={styles.info}>
            <b>{lang(isImporting ? 'The wallet is imported' : 'The wallet is ready')}.</b>
          </p>
          <p className={styles.info}>
            {lang('Create a password to protect it.')}
          </p>
        </>
      )}

      <CreatePasswordForm
        isActive={isActive}
        isLoading={isLoading}
        formId={formId}
        onCancel={resetAuth}
        onSubmit={handleSubmit}
      />
    </div>
  );
};

// eslint-disable-next-line consistent-return
function getFormId(method: AuthMethod) {
  switch (method) {
    case 'createAccount':
      return 'auth_create_password';
    case 'importMnemonic':
      return 'auth_import_mnemonic_password';
    case 'importHardwareWallet':
      return 'auth_import_hardware_password';
  }
}

export default memo(AuthCreatePassword);
