import React, { memo, useEffect, useState } from '../../../lib/teact/teact';
import { getActions } from '../../../global';

import { BiometricsState } from '../../../global/types';

import { ANIMATED_STICKER_HUGE_SIZE_PX } from '../../../config';
import buildClassName from '../../../util/buildClassName';
import resolveModalTransitionName from '../../../util/resolveModalTransitionName';
import { IS_ELECTRON } from '../../../util/windowEnvironment';
import { ANIMATED_STICKERS_PATHS } from '../../ui/helpers/animatedAssets';

import useLang from '../../../hooks/useLang';
import useLastCallback from '../../../hooks/useLastCallback';
import useModalTransitionKeys from '../../../hooks/useModalTransitionKeys';

import AnimatedIconWithPreview from '../../ui/AnimatedIconWithPreview';
import Button from '../../ui/Button';
import Modal from '../../ui/Modal';
import ModalHeader from '../../ui/ModalHeader';
import PasswordForm from '../../ui/PasswordForm';
import Transition from '../../ui/Transition';

import modalStyles from '../../ui/Modal.module.scss';
import styles from './Biometrics.module.scss';

interface OwnProps {
  isOpen: boolean;
  state: BiometricsState;
  error?: string;
  onClose: NoneToVoidFunction;
}

const STICKER_SIZE = 180;

function TurnOn({
  isOpen, state, error, onClose,
}: OwnProps) {
  const { enableBiometrics } = getActions();

  const lang = useLang();
  const [localError, setLocalError] = useState<string | undefined>();
  const { renderingKey, nextKey, updateNextKey } = useModalTransitionKeys(state, isOpen);
  const shouldDisablePasswordForm = Boolean(state !== BiometricsState.TurnOnPasswordConfirmation);

  useEffect(() => {
    if (isOpen) {
      setLocalError('');
    }
  }, [isOpen]);

  const handleClearError = useLastCallback(() => {
    setLocalError(undefined);
  });

  const handleSubmit = useLastCallback((password: string) => {
    if (shouldDisablePasswordForm) {
      return;
    }

    try {
      enableBiometrics({ password });
    } catch (err: any) {
      setLocalError(err.message || 'Unknown error.');
    }
  });

  // eslint-disable-next-line consistent-return
  function renderContent(isActive: boolean, isFrom: boolean, currentKey: number) {
    switch (currentKey) {
      case BiometricsState.TurnOnPasswordConfirmation:
        return (
          <>
            <ModalHeader title={lang('Turn On Biometrics')} onClose={onClose} />
            <PasswordForm
              isActive={isActive}
              isLoading={false}
              error={error || localError}
              operationType="passcode"
              help={lang('Enabling biometric confirmation will reset the password.')}
              submitLabel={lang('Continue')}
              onSubmit={handleSubmit}
              onCancel={onClose}
              onUpdate={handleClearError}
            />
          </>
        );

      case BiometricsState.TurnOnRegistration:
        return (
          <>
            <ModalHeader title={lang('Biometric Registration')} onClose={onClose} />
            <div className={modalStyles.transitionContent}>
              <AnimatedIconWithPreview
                tgsUrl={ANIMATED_STICKERS_PATHS.holdTon}
                previewUrl={ANIMATED_STICKERS_PATHS.holdTonPreview}
                play={isActive}
                size={STICKER_SIZE}
                nonInteractive
                noLoop={false}
                className={styles.sticker}
              />

              <div className={styles.step}>{lang('Step 1 of 2. Registration')}</div>

              <div className={modalStyles.buttons}>
                <Button onClick={onClose} className={modalStyles.customCancelButton}>
                  {lang('Cancel')}
                </Button>
              </div>
            </div>
          </>
        );

      case BiometricsState.TurnOnVerification:
        return (
          <>
            <ModalHeader title={lang('Biometric Registration')} onClose={onClose} />
            <div className={modalStyles.transitionContent}>
              <AnimatedIconWithPreview
                tgsUrl={ANIMATED_STICKERS_PATHS.holdTon}
                previewUrl={ANIMATED_STICKERS_PATHS.holdTonPreview}
                play={isActive}
                size={STICKER_SIZE}
                nonInteractive
                noLoop={false}
                className={styles.sticker}
              />

              <div className={styles.step}>
                {lang(IS_ELECTRON ? 'Verification' : 'Step 2 of 2. Verification')}
              </div>

              <div className={modalStyles.buttons}>
                <Button onClick={onClose} className={modalStyles.customCancelButton}>
                  {lang('Cancel')}
                </Button>
              </div>
            </div>
          </>
        );

      case BiometricsState.TurnOnComplete:
        return (
          <>
            <ModalHeader title={lang('Biometrics Enabled')} onClose={onClose} />
            <div className={modalStyles.transitionContent}>
              <AnimatedIconWithPreview
                tgsUrl={ANIMATED_STICKERS_PATHS.yeee}
                previewUrl={ANIMATED_STICKERS_PATHS.yeeePreview}
                play={isActive}
                size={ANIMATED_STICKER_HUGE_SIZE_PX}
                nonInteractive
                noLoop={false}
                className={buildClassName(styles.sticker, styles.stickerHuge)}
              />

              <div className={modalStyles.buttons}>
                <Button isPrimary onClick={onClose} className={modalStyles.customSubmitButton}>
                  {lang('Done')}
                </Button>
              </div>
            </div>
          </>
        );
    }
  }

  return (
    <Modal
      hasCloseButton
      isOpen={isOpen}
      dialogClassName={styles.modalDialog}
      onClose={onClose}
    >
      <Transition
        name={resolveModalTransitionName()}
        className={buildClassName(modalStyles.transition, 'custom-scroll')}
        slideClassName={modalStyles.transitionSlide}
        activeKey={renderingKey}
        nextKey={nextKey}
        onStop={updateNextKey}
      >
        {renderContent}
      </Transition>
    </Modal>
  );
}

export default memo(TurnOn);
