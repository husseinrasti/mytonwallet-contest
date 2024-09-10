import React, { memo } from '../../lib/teact/teact';

import { MNEMONIC_COUNT } from '../../config';
import renderText from '../../global/helpers/renderText';
import buildClassName from '../../util/buildClassName';

import useHistoryBack from '../../hooks/useHistoryBack';
import useLang from '../../hooks/useLang';

import Button from '../ui/Button';
import Emoji from '../ui/Emoji';
import ModalHeader from '../ui/ModalHeader';

import modalStyles from '../ui/Modal.module.scss';
import styles from './Auth.module.scss';

type OwnProps = {
  isActive?: boolean;
  mnemonic?: string[];
  onClose: NoneToVoidFunction;
  onNext: NoneToVoidFunction;
};

function MnemonicList({
  isActive, mnemonic, onNext, onClose,
}: OwnProps) {
  const lang = useLang();

  useHistoryBack({
    isActive,
    onBack: onClose,
  });

  return (
    <div className={modalStyles.transitionContentWrapper}>
      <ModalHeader title={lang('%1$d Secret Words', MNEMONIC_COUNT) as string} onClose={onClose} />
      <div className={buildClassName(styles.mnemonicContainer, modalStyles.transitionContent, 'custom-scroll')}>
        <p className={buildClassName(styles.info, styles.small)}>
          {renderText(lang('$mnemonic_list_description'))}
        </p>
        <p className={buildClassName(styles.info, styles.small)}>
          <Emoji from="⚠️" />{' '}{renderText(lang('$mnemonic_warning'))}
        </p>
        <ol className={styles.words}>
          {mnemonic?.map((word) => (
            <li className={styles.word}>{word}</li>
          ))}
        </ol>

        <div className={modalStyles.buttons}>
          <Button isPrimary onClick={onNext}>{lang('Let\'s Check')}</Button>
        </div>
      </div>
    </div>
  );
}

export default memo(MnemonicList);
