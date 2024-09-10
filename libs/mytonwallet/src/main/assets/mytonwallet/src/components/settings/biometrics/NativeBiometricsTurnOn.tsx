import React, { memo, useEffect, useState } from '../../../lib/teact/teact';
import { getActions, withGlobal } from '../../../global';

import { PIN_LENGTH } from '../../../config';
import buildClassName from '../../../util/buildClassName';
import { ANIMATED_STICKERS_PATHS } from '../../ui/helpers/animatedAssets';

import { useDeviceScreen } from '../../../hooks/useDeviceScreen';
import useEffectWithPrevDeps from '../../../hooks/useEffectWithPrevDeps';
import useHistoryBack from '../../../hooks/useHistoryBack';
import useLang from '../../../hooks/useLang';
import useLastCallback from '../../../hooks/useLastCallback';

import AnimatedIconWithPreview from '../../ui/AnimatedIconWithPreview';
import Button from '../../ui/Button';
import PinPad from '../../ui/PinPad';

import styles from '../Settings.module.scss';

interface OwnProps {
  isActive?: boolean;
  handleBackClick: NoneToVoidFunction;
}

interface StateProps {
  isPinAccepted?: boolean;
  error?: string;
  isNativeBiometricsEnabled?: boolean;
}

function NativeBiometricsTurnOn({
  isActive,
  isPinAccepted,
  error,
  isNativeBiometricsEnabled,
  handleBackClick,
}: OwnProps & StateProps) {
  const { enableNativeBiometrics, clearNativeBiometricsError } = getActions();

  const lang = useLang();
  const { isSmallHeight } = useDeviceScreen();
  const [pin, setPin] = useState<string>('');
  const pinPadType = pin.length !== PIN_LENGTH
    ? undefined
    : (isPinAccepted ? 'success' : (error ? 'error' : undefined));
  const pinTitle = isPinAccepted
    ? 'Correct'
    : (error && pin.length === PIN_LENGTH ? error : 'Enter your code');

  useEffect(() => {
    if (!isActive) return;

    setPin('');
  }, [isActive]);

  useEffectWithPrevDeps(([prevIsEnabled]) => {
    if (isNativeBiometricsEnabled && !prevIsEnabled) {
      handleBackClick();
    }
  }, [isNativeBiometricsEnabled, handleBackClick]);

  useHistoryBack({
    isActive,
    onBack: handleBackClick,
  });

  const handleSubmit = useLastCallback((password: string) => {
    enableNativeBiometrics({ password });
  });

  return (
    <div className={styles.slide}>
      <div className={buildClassName(styles.content, styles.contentFullSize)}>
        <Button
          isSimple
          isText
          onClick={handleBackClick}
          className={buildClassName(styles.headerBack, styles.headerBackInContent)}
        >
          <i className={buildClassName(styles.iconChevron, 'icon-chevron-left')} aria-hidden />
          <span>{lang('Back')}</span>
        </Button>

        <div className={styles.pinPadHeader}>
          <AnimatedIconWithPreview
            play={isActive}
            tgsUrl={ANIMATED_STICKERS_PATHS.guard}
            previewUrl={ANIMATED_STICKERS_PATHS.guardPreview}
            noLoop={false}
            nonInteractive
            className={styles.stickerNativeBiometric}
          />

          {!isSmallHeight && <div className={styles.pinPadTitle}>{lang('Confirm Passcode')}</div>}
        </div>

        <PinPad
          isActive={isActive}
          onClearError={clearNativeBiometricsError}
          title={lang(pinTitle)}
          type={pinPadType}
          length={PIN_LENGTH}
          value={pin}
          onChange={setPin}
          onSubmit={handleSubmit}
        />
      </div>
    </div>
  );
}

export default memo(withGlobal<OwnProps>((global): StateProps => {
  const {
    nativeBiometricsError,
    isPinAccepted,
    settings: { authConfig },
  } = global;

  return {
    isPinAccepted,
    error: nativeBiometricsError,
    isNativeBiometricsEnabled: !!authConfig && authConfig.kind === 'native-biometrics',
  };
})(NativeBiometricsTurnOn));
