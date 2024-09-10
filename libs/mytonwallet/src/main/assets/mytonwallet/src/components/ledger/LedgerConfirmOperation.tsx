import React, { memo, useEffect, useState } from '../../lib/teact/teact';

import { ANIMATED_STICKER_BIG_SIZE_PX } from '../../config';
import renderText from '../../global/helpers/renderText';
import buildClassName from '../../util/buildClassName';
import resolveModalTransitionName from '../../util/resolveModalTransitionName';
import { ANIMATED_STICKERS_PATHS } from '../ui/helpers/animatedAssets';

import useHistoryBack from '../../hooks/useHistoryBack';
import useLang from '../../hooks/useLang';

import AnimatedIconWithPreview from '../ui/AnimatedIconWithPreview';
import Button from '../ui/Button';
import ModalHeader from '../ui/ModalHeader';
import Transition from '../ui/Transition';

import modalStyles from '../ui/Modal.module.scss';
import styles from './LedgerModal.module.scss';

interface OwnProps {
  isActive?: boolean;
  text: string;
  error?: string;
  onClose: () => void;
  onTryAgain: () => void;
}

enum ConfirmTransactionState {
  Waiting,
  Error,
}

function LedgerConfirmOperation({
  isActive, text, error, onClose, onTryAgain,
}: OwnProps) {
  const lang = useLang();

  const [activeState, setActiveState] = useState(ConfirmTransactionState.Waiting);
  const [nextKey, setNextKey] = useState(ConfirmTransactionState.Error);

  useHistoryBack({
    isActive,
    onBack: onClose,
  });

  useEffect(() => {
    if (error) {
      setActiveState(ConfirmTransactionState.Error);
      setNextKey(ConfirmTransactionState.Waiting);
    } else {
      setActiveState(ConfirmTransactionState.Waiting);
      setNextKey(ConfirmTransactionState.Error);
    }
  }, [error]);

  function renderWaitingConfirm(isActiveSlide: boolean) {
    return (
      <>
        <ModalHeader title={lang('Confirm via Ledger')} onClose={onClose} />
        <div className={styles.container}>
          <AnimatedIconWithPreview
            size={ANIMATED_STICKER_BIG_SIZE_PX}
            play={isActiveSlide}
            noLoop={false}
            nonInteractive
            className={styles.sticker}
            tgsUrl={ANIMATED_STICKERS_PATHS.holdTon}
            previewUrl={ANIMATED_STICKERS_PATHS.holdTonPreview}
          />
          <div className={buildClassName(styles.textBlock, styles.textBlock_small)}>
            <span className={styles.text}>{text}</span>
          </div>
          <div className={buildClassName(styles.actionBlock, styles.actionBlock_single)}>
            <Button onClick={onClose} className={styles.button}>{lang('Cancel')}</Button>
          </div>
        </div>
      </>
    );
  }

  function renderTryAgain(isActiveSlide: boolean) {
    const isErrorDetailed = error === '$hardware_blind_sign_not_enabled';

    return (
      <>
        <ModalHeader title={lang('Confirm via Ledger')} onClose={onClose} />
        <div className={styles.container}>
          <AnimatedIconWithPreview
            size={ANIMATED_STICKER_BIG_SIZE_PX}
            play={isActiveSlide}
            noLoop={false}
            nonInteractive
            className={styles.sticker}
            tgsUrl={ANIMATED_STICKERS_PATHS.holdTon}
            previewUrl={ANIMATED_STICKERS_PATHS.holdTonPreview}
          />
          <div className={buildClassName(styles.declinedLabel, isErrorDetailed && styles.declinedLabelDetailed)}>
            {renderText(lang(error!))}
          </div>
          <div className={styles.actionBlock}>
            <Button onClick={onClose} className={styles.button}>{lang('Cancel')}</Button>
            <Button isPrimary onClick={onTryAgain} className={styles.button}>{lang('Try Again')}</Button>
          </div>
        </div>
      </>
    );
  }

  // eslint-disable-next-line consistent-return
  function renderContent(isActiveSlide: boolean, isFrom: boolean, currentKey: number) {
    switch (currentKey) {
      case ConfirmTransactionState.Waiting:
        return renderWaitingConfirm(isActiveSlide);
      case ConfirmTransactionState.Error:
        return renderTryAgain(isActiveSlide);
    }
  }

  return (
    <Transition
      name={resolveModalTransitionName()}
      className={buildClassName(modalStyles.transition, 'custom-scroll')}
      slideClassName={buildClassName(modalStyles.transitionSlide, styles.slide)}
      activeKey={activeState}
      nextKey={nextKey}
    >
      {renderContent}
    </Transition>
  );
}

export default memo(LedgerConfirmOperation);
