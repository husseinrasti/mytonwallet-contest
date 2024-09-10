import React, {
  memo, useEffect, useRef, useState,
} from '../../../lib/teact/teact';
import { getActions, withGlobal } from '../../../global';

import { IS_CAPACITOR } from '../../../config';
import { selectMnemonicForCheck } from '../../../global/actions/api/auth';
import buildClassName from '../../../util/buildClassName';
import { vibrateOnError, vibrateOnSuccess } from '../../../util/capacitor';
import isMnemonicPrivateKey from '../../../util/isMnemonicPrivateKey';
import resolveModalTransitionName from '../../../util/resolveModalTransitionName';
import { callApi } from '../../../api';

import useLang from '../../../hooks/useLang';
import useLastCallback from '../../../hooks/useLastCallback';

import MnemonicCheck from '../../auth/MnemonicCheck';
import MnemonicList from '../../auth/MnemonicList';
import MnemonicPrivateKey from '../../auth/MnemonicPrivateKey';
import SafetyRules from '../../auth/SafetyRules';
import Modal from '../../ui/Modal';
import ModalHeader from '../../ui/ModalHeader';
import PasswordForm from '../../ui/PasswordForm';
import Transition from '../../ui/Transition';

import modalStyles from '../../ui/Modal.module.scss';
import styles from './BackupModal.module.scss';

type OwnProps = {
  isOpen?: boolean;
  onClose: () => void;
};

type StateProps = {
  currentAccountId?: string;
};

enum SLIDES {
  confirm,
  password,
  mnemonic,
  check,
}

function BackupModal({
  isOpen, currentAccountId, onClose,
}: OwnProps & StateProps) {
  const { setIsBackupRequired, setIsPinAccepted, clearIsPinAccepted } = getActions();

  const lang = useLang();
  const [currentSlide, setCurrentSlide] = useState<number>(SLIDES.confirm);
  const [nextKey, setNextKey] = useState<number | undefined>(SLIDES.password);
  const [checkIndexes, setCheckIndexes] = useState<number[]>([]);

  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | undefined>();

  const mnemonicRef = useRef<string[] | undefined>(undefined);
  const noResetFullNativeOnBlur = currentSlide === SLIDES.confirm || currentSlide === SLIDES.password;

  useEffect(() => {
    mnemonicRef.current = undefined;
  }, [isOpen]);

  const handleSafetyConfirm = useLastCallback(() => {
    setCurrentSlide(SLIDES.password);
    setNextKey(SLIDES.mnemonic);
  });

  const handlePasswordSubmit = useLastCallback(async (password: string) => {
    setIsLoading(true);
    mnemonicRef.current = await callApi('getMnemonic', currentAccountId!, password);

    if (!mnemonicRef.current) {
      setError('Wrong password, please try again.');
      if (IS_CAPACITOR) {
        void vibrateOnError();
      }

      setIsLoading(false);
      return;
    }
    if (IS_CAPACITOR) {
      setIsPinAccepted();
      await vibrateOnSuccess(true);
      clearIsPinAccepted();
    }

    setIsLoading(false);
    setNextKey(SLIDES.check);
    setCurrentSlide(SLIDES.mnemonic);
  });

  const handleBackupErrorUpdate = useLastCallback(() => {
    setError(undefined);
  });

  const handleCheckMnemonic = useLastCallback(() => {
    setCheckIndexes(selectMnemonicForCheck());
    setCurrentSlide(SLIDES.check);
    setNextKey(undefined);
  });

  const handleRestartCheckMnemonic = useLastCallback(() => {
    setCurrentSlide(SLIDES.mnemonic);
    setNextKey(SLIDES.check);
  });

  const handleModalClose = useLastCallback(() => {
    setIsLoading(false);
    setError(undefined);
    setCurrentSlide(SLIDES.confirm);
    setNextKey(SLIDES.password);
  });

  const handleCheckMnemonicSubmit = useLastCallback(() => {
    setIsBackupRequired({ isMnemonicChecked: true });
    onClose();
  });

  // eslint-disable-next-line consistent-return
  function renderContent(isActive: boolean, isFrom: boolean, currentKey: number) {
    const mnemonic = mnemonicRef.current;

    switch (currentKey) {
      case SLIDES.confirm:
        return (
          <SafetyRules
            isActive={isActive}
            onSubmit={handleSafetyConfirm}
            onClose={onClose}
          />
        );

      case SLIDES.password:
        return (
          <>
            {!IS_CAPACITOR && <ModalHeader title={lang('Enter Password')} onClose={onClose} />}
            <PasswordForm
              isActive={isActive}
              isLoading={isLoading}
              error={error}
              withCloseButton={IS_CAPACITOR}
              submitLabel={lang('Back Up')}
              cancelLabel={lang('Cancel')}
              onSubmit={handlePasswordSubmit}
              onCancel={onClose}
              onUpdate={handleBackupErrorUpdate}
            />
          </>
        );

      case SLIDES.mnemonic:
        return mnemonic && isMnemonicPrivateKey(mnemonic) ? (
          <MnemonicPrivateKey
            privateKeyHex={mnemonic![0]}
            onClose={onClose}
          />
        ) : (
          <MnemonicList
            mnemonic={mnemonic}
            onNext={handleCheckMnemonic}
            onClose={onClose}
          />
        );

      case SLIDES.check:
        return (
          <MnemonicCheck
            isActive={isActive}
            isInModal
            mnemonic={mnemonicRef.current as string[]}
            checkIndexes={checkIndexes}
            buttonLabel={lang('Done')}
            onSubmit={handleCheckMnemonicSubmit}
            onCancel={handleRestartCheckMnemonic}
            onClose={onClose}
          />
        );
    }
  }

  return (
    <Modal
      isOpen={isOpen}
      hasCloseButton
      dialogClassName={styles.modalDialog}
      nativeBottomSheetKey="backup"
      forceFullNative={currentSlide === SLIDES.password}
      noResetFullNativeOnBlur={noResetFullNativeOnBlur}
      onClose={onClose}
      onCloseAnimationEnd={handleModalClose}
    >
      <Transition
        name={resolveModalTransitionName()}
        className={buildClassName(modalStyles.transition, 'custom-scroll')}
        slideClassName={modalStyles.transitionSlide}
        activeKey={currentSlide}
        nextKey={nextKey}
      >
        {renderContent}
      </Transition>
    </Modal>
  );
}

export default memo(withGlobal<OwnProps>((global): StateProps => {
  return { currentAccountId: global.currentAccountId };
})(BackupModal));
