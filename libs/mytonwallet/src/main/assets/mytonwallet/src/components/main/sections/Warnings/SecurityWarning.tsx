import React, { memo } from '../../../../lib/teact/teact';
import { getActions, withGlobal } from '../../../../global';

import { MY_TON_WALLET_PROMO_URL } from '../../../../config';
import buildClassName from '../../../../util/buildClassName';
import { openUrl } from '../../../../util/openUrl';

import { useDeviceScreen } from '../../../../hooks/useDeviceScreen';
import useLang from '../../../../hooks/useLang';
import useLastCallback from '../../../../hooks/useLastCallback';
import useShowTransition from '../../../../hooks/useShowTransition';

import styles from './Warnings.module.scss';

type StateProps = {
  isSecurityWarningHidden?: boolean;
};

function SecurityWarning({ isSecurityWarningHidden }: StateProps) {
  const { closeSecurityWarning } = getActions();

  const { shouldRender, transitionClassNames } = useShowTransition(!isSecurityWarningHidden, undefined, true);
  const { isLandscape } = useDeviceScreen();

  const lang = useLang();

  function handleClick() {
    openUrl(MY_TON_WALLET_PROMO_URL);
  }

  const handleClose = useLastCallback((e: React.MouseEvent<HTMLButtonElement>) => {
    e.stopPropagation();

    closeSecurityWarning();
  });

  if (!shouldRender) {
    return undefined;
  }

  return (
    <div
      className={buildClassName(styles.wrapper, isLandscape && styles.wrapper_landscape, transitionClassNames)}
      onClick={handleClick}
    >
      {lang('Consider More Secure Version')}
      <i className={buildClassName(styles.icon, 'icon-chevron-right')} aria-hidden />
      <p className={styles.text}>{lang('Install our native app or browser extension.')}</p>

      <button type="button" className={styles.closeButton} aria-label={lang('Close')} onClick={handleClose}>
        <i className="icon-close" aria-hidden />
      </button>
    </div>
  );
}

export default memo(
  withGlobal((global): StateProps => {
    return {
      isSecurityWarningHidden: global.settings.isSecurityWarningHidden,
    };
  })(SecurityWarning),
);
