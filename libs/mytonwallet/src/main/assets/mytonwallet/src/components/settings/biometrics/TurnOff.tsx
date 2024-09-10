import React, { memo } from '../../../lib/teact/teact';
import { getActions } from '../../../global';

import { BiometricsState } from '../../../global/types';

import { ANIMATED_STICKER_HUGE_SIZE_PX } from '../../../config';
import buildClassName from '../../../util/buildClassName';
import resolveModalTransitionName from '../../../util/resolveModalTransitionName';
import { ANIMATED_STICKERS_PATHS } from '../../ui/helpers/animatedAssets';

import useLang from '../../../hooks/useLang';
import useLastCallback from '../../../hooks/useLastCallback';
import useModalTransitionKeys from '../../../hooks/useModalTransitionKeys';

import AnimatedIconWithPreview from '../../ui/AnimatedIconWithPreview';
import Button from '../../ui/Button';
import CreatePasswordForm from '../../ui/CreatePasswordForm';
import Modal from '../../ui/Modal';
import ModalHeader from '../../ui/ModalHeader';
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

function TurnOff({
  isOpen, state, error, onClose,
}: OwnProps) {
  const { disableBiometrics } = getActions();

  const lang = useLang();
  const { renderingKey, nextKey, updateNextKey } = useModalTransitionKeys(state, isOpen);

  const handleSubmit = useLastCallback((password: string, isPasswordNumeric?: boolean) => {
    disableBiometrics({ password, isPasswordNumeric });
  });

  // eslint-disable-next-line consistent-return
  function renderContent(isActive: boolean, isFrom: boolean, currentKey: number) {
    switch (currentKey) {
      case BiometricsState.TurnOffBiometricConfirmation:
        return (
          <>
            <ModalHeader title={lang('Biometric Confirmation')} onClose={onClose} />
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

              {error ? (
                <div className={styles.error}>{lang(error)}</div>
              ) : (
                <div className={styles.step}>{lang('Please verify the operation.')}</div>
              )}

              <div className={modalStyles.buttons}>
                <Button onClick={onClose} className={modalStyles.customSubmitButton}>
                  {lang('Cancel')}
                </Button>
              </div>
            </div>
          </>
        );

      case BiometricsState.TurnOffCreatePassword:
        return (
          <>
            <ModalHeader title={lang('Create Password')} onClose={onClose} />
            <div className={modalStyles.transitionContent}>
              <AnimatedIconWithPreview
                tgsUrl={ANIMATED_STICKERS_PATHS.guard}
                previewUrl={ANIMATED_STICKERS_PATHS.guardPreview}
                play={isActive}
                size={STICKER_SIZE}
                nonInteractive
                noLoop={false}
                className={styles.sticker}
              />
              <CreatePasswordForm
                isActive={isActive}
                formId="biometrics-create-password"
                onSubmit={handleSubmit}
                onCancel={onClose}
              />
            </div>
          </>
        );

      case BiometricsState.TurnOffComplete:
        return (
          <>
            <ModalHeader title={lang('Biometrics Disabled')} onClose={onClose} />
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

export default memo(TurnOff);
