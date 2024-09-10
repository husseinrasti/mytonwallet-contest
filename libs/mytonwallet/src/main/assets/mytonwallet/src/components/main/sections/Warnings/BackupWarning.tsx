import React, { memo } from '../../../../lib/teact/teact';
import { withGlobal } from '../../../../global';

import type { Theme } from '../../../../global/types';

import buildClassName from '../../../../util/buildClassName';

import useBrowserUiColor from '../../../../hooks/useBrowserUiColor';
import { useDeviceScreen } from '../../../../hooks/useDeviceScreen';
import useLang from '../../../../hooks/useLang';
import useShowTransition from '../../../../hooks/useShowTransition';

import styles from './Warnings.module.scss';

type OwnProps = {
  isRequired: boolean;
  onOpenBackupWallet: () => void;
};

type StateProps = {
  theme: Theme;
};

const UI_BG_RED_LIGHT = '#F36A6B';
const UI_BG_RED_DARK = '#C44646';

function BackupWarning({ isRequired, theme, onOpenBackupWallet }: OwnProps & StateProps) {
  const { shouldRender, transitionClassNames } = useShowTransition(isRequired, undefined, true);
  const { isLandscape } = useDeviceScreen();

  const lang = useLang();

  useBrowserUiColor({
    isActive: isRequired,
    currentTheme: theme,
    lightColor: UI_BG_RED_LIGHT,
    darkColor: UI_BG_RED_DARK,
  });

  const handleClick = () => {
    onOpenBackupWallet();
  };

  if (!shouldRender) {
    return undefined;
  }

  return (
    <div
      className={buildClassName(styles.wrapper, isLandscape && styles.wrapper_landscape, transitionClassNames)}
      onClick={handleClick}
    >
      {lang('Wallet is not backed up')}
      <i className={buildClassName(styles.icon, 'icon-chevron-right')} aria-hidden />
      <p className={styles.text}>
        {lang('Back up wallet to have full access to it')}
      </p>
    </div>
  );
}

export default memo(
  withGlobal<OwnProps>(
    (global): StateProps => {
      return {
        theme: global.settings.theme,
      };
    },
    (global, _, stickToFirst) => stickToFirst(global.currentAccountId),
  )(BackupWarning),
);
