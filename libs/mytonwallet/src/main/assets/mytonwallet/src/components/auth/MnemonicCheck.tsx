import type { FormEvent } from 'react';
import React, {
  memo, useEffect, useState,
} from '../../lib/teact/teact';

import { MNEMONIC_CHECK_COUNT } from '../../config';
import renderText from '../../global/helpers/renderText';
import buildClassName from '../../util/buildClassName';
import { areSortedArraysEqual } from '../../util/iteratees';

import useHistoryBack from '../../hooks/useHistoryBack';
import useLang from '../../hooks/useLang';
import useLastCallback from '../../hooks/useLastCallback';

import InputMnemonic from '../common/InputMnemonic';
import Button from '../ui/Button';
import ModalHeader from '../ui/ModalHeader';

import modalStyles from '../ui/Modal.module.scss';
import styles from './Auth.module.scss';

type OwnProps = {
  isActive?: boolean;
  isInModal?: boolean;
  mnemonic?: string[];
  checkIndexes?: number[];
  buttonLabel: string;
  onSubmit: NoneToVoidFunction;
  onCancel: NoneToVoidFunction;
  onClose: NoneToVoidFunction;
};

function MnemonicCheck({
  isActive, isInModal, mnemonic, checkIndexes, buttonLabel, onCancel, onSubmit, onClose,
}: OwnProps) {
  const lang = useLang();
  const [words, setWords] = useState<Record<number, string>>({});
  const [hasMnemonicError, setHasMnemonicError] = useState(false);

  useEffect(() => {
    if (isActive) {
      setWords({});
      setHasMnemonicError(false);
    }
  }, [isActive]);

  useHistoryBack({
    isActive,
    onBack: onCancel,
  });

  const handleSetWord = useLastCallback((value: string, index: number) => {
    setWords({
      ...words,
      [index]: value?.toLowerCase(),
    });
  });

  const handleMnemonicCheckSubmit = useLastCallback((e: FormEvent) => {
    e.preventDefault();
    const answer = mnemonic && checkIndexes?.map((index) => mnemonic[index]);
    if (answer && areSortedArraysEqual(answer, Object.values(words))) {
      onSubmit();
    } else {
      setHasMnemonicError(true);
    }
  });

  return (
    <div className={modalStyles.transitionContentWrapper}>
      <ModalHeader title={lang('Let\'s Check!')} onClose={onClose} />
      <div className={buildClassName(modalStyles.transitionContent, 'custom-scroll')}>
        <p className={buildClassName(styles.info, styles.small)}>
          {lang('Let’s make sure your secrets words are recorded correctly.')}
        </p>

        <p className={buildClassName(styles.info, styles.small)}>
          {renderText(lang('$mnemonic_check_words_list', checkIndexes?.map((n) => n + 1)?.join(', ')))}
        </p>

        <form onSubmit={handleMnemonicCheckSubmit} id="check_mnemonic_form">
          {checkIndexes!.map((key, i) => (
            <InputMnemonic
              key={key}
              id={`check-mnemonic-${i}`}
              nextId={i + 1 < MNEMONIC_CHECK_COUNT ? `check-mnemonic-${i + 1}` : undefined}
              labelText={`${key + 1}`}
              value={words[key]}
              isInModal={isInModal}
              suggestionsPosition={i > 1 ? 'top' : undefined}
              inputArg={key}
              className={styles.checkMnemonicInput}
              onInput={handleSetWord}
            />
          ))}
        </form>

        {hasMnemonicError && (
          <div className={buildClassName(styles.error, styles.small)}>
            {renderText(lang('$mnemonic_check_error'))}
          </div>
        )}

        <div className={modalStyles.buttons}>
          <Button onClick={onCancel} className={modalStyles.button}>{lang('Back')}</Button>
          <Button isPrimary forFormId="check_mnemonic_form" className={modalStyles.button}>{buttonLabel}</Button>
        </div>
      </div>
    </div>
  );
}

export default memo(MnemonicCheck);
