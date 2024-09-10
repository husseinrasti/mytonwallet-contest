import { BottomSheet } from 'native-bottom-sheet';
import React, { memo, useEffect } from '../../lib/teact/teact';
import { getActions, withGlobal } from '../../global';

import type { GlobalState } from '../../global/types';

import buildClassName from '../../util/buildClassName';
import { getIsFaceIdAvailable, vibrateOnError } from '../../util/capacitor';
import { IS_DELEGATED_BOTTOM_SHEET } from '../../util/windowEnvironment';

import useEffectWithPrevDeps from '../../hooks/useEffectWithPrevDeps';
import useLastCallback from '../../hooks/useLastCallback';
import usePrevious from '../../hooks/usePrevious';

import PinPadButton from './PinPadButton';

import styles from './PinPad.module.scss';

interface OwnProps {
  isActive?: boolean;
  title: string;
  type?: 'error' | 'success';
  value: string;
  length?: number;
  className?: string;
  onBiometricsClick?: NoneToVoidFunction;
  onChange: (value: string) => void;
  onClearError?: NoneToVoidFunction;
  onSubmit: (pin: string) => void;
}

type StateProps = Pick<GlobalState['settings'], 'authConfig'> & {
  isPinAccepted?: boolean;
};

const DEFAULT_PIN_LENGTH = 4;
const RESET_STATE_DELAY_MS = 1500;

function PinPad({
  isActive,
  title,
  type,
  value,
  length = DEFAULT_PIN_LENGTH,
  onBiometricsClick,
  isPinAccepted,
  className,
  onChange,
  onClearError,
  onSubmit,
}: OwnProps & StateProps) {
  const { clearIsPinAccepted } = getActions();

  const isFaceId = getIsFaceIdAvailable();
  const canRenderBackspace = value.length > 0;
  const isSuccess = type === 'success' || isPinAccepted;
  const prevIsPinAccepted = usePrevious(isPinAccepted);
  const arePinButtonsDisabled = isSuccess
    || (value.length === length && type !== 'error'); // Allow pincode entry in case of an error

  const titleClassName = buildClassName(
    styles.title,
    type === 'error' && styles.error,
    isSuccess && styles.success,
  );

  useEffect(() => {
    if (prevIsPinAccepted && !isPinAccepted && length === value.length) {
      onChange('');
    }
  }, [isPinAccepted, length, onChange, prevIsPinAccepted, value.length]);

  useEffect(() => {
    return () => {
      clearIsPinAccepted();
    };
  }, []);

  // Fix for iOS, enable fast pinpad button presses
  useEffectWithPrevDeps(([prevIsActive]) => {
    if (!IS_DELEGATED_BOTTOM_SHEET) return;
    if (isActive) {
      void BottomSheet.clearScrollPatch();
    } else if (prevIsActive) {
      void BottomSheet.applyScrollPatch();
    }
  }, [isActive]);

  useEffect(() => {
    if (type !== 'error') return undefined;

    const timeoutId = window.setTimeout(() => {
      if (value.length === length) {
        onChange('');
      }
      onClearError?.();
    }, RESET_STATE_DELAY_MS);
    void vibrateOnError();

    return () => {
      window.clearTimeout(timeoutId);
    };
  }, [length, onChange, onClearError, type, value.length]);

  const handleClick = useLastCallback((char: string) => {
    if (value.length === length || value.length === 0) {
      onClearError?.();
    }

    if (value.length === length) {
      onChange(char);

      return;
    }

    const newValue = `${value}${char}`;
    onChange(newValue);

    if (newValue.length === length) {
      onSubmit(newValue);
    }
  });

  const handleBackspaceClick = useLastCallback(() => {
    onClearError?.();
    if (!value.length) return;

    onChange(value.slice(0, -1));
  });

  function renderDots() {
    const dotsClassName = buildClassName(
      styles.dots,
      type === 'error' && styles.dotsError,
      isSuccess && styles.dotsLoading,
    );

    return (
      <div className={dotsClassName} aria-hidden>
        {Array.from({ length }, (_, i) => (
          <div
            key={i}
            className={buildClassName(
              styles.dot,
              i < value.length && styles.dotFilled,
              type === 'error' && styles.error,
              isSuccess && styles.success,
            )}
          />
        ))}
      </div>
    );
  }

  return (
    <div className={buildClassName(styles.root, className)}>
      <div className={titleClassName}>{title}</div>
      {renderDots()}

      <div className={styles.grid}>
        <PinPadButton value="1" onClick={handleClick} isDisabled={arePinButtonsDisabled} />
        <PinPadButton value="2" onClick={handleClick} isDisabled={arePinButtonsDisabled} />
        <PinPadButton value="3" onClick={handleClick} isDisabled={arePinButtonsDisabled} />
        <PinPadButton value="4" onClick={handleClick} isDisabled={arePinButtonsDisabled} />
        <PinPadButton value="5" onClick={handleClick} isDisabled={arePinButtonsDisabled} />
        <PinPadButton value="6" onClick={handleClick} isDisabled={arePinButtonsDisabled} />
        <PinPadButton value="7" onClick={handleClick} isDisabled={arePinButtonsDisabled} />
        <PinPadButton value="8" onClick={handleClick} isDisabled={arePinButtonsDisabled} />
        <PinPadButton value="9" onClick={handleClick} isDisabled={arePinButtonsDisabled} />
        {!onBiometricsClick ? <span /> : (
          <PinPadButton onClick={onBiometricsClick} isDisabled={isSuccess}>
            <i className={buildClassName('icon', isFaceId ? 'icon-face-id' : 'icon-touch-id')} aria-hidden />
          </PinPadButton>
        )}
        <PinPadButton value="0" onClick={handleClick} isDisabled={arePinButtonsDisabled} />
        <PinPadButton
          className={!canRenderBackspace && styles.buttonHidden}
          isDisabled={!canRenderBackspace || isSuccess}
          onClick={handleBackspaceClick}
        >
          <i className="icon icon-backspace" aria-hidden />
        </PinPadButton>
      </div>
    </div>
  );
}

export default memo(withGlobal<OwnProps>(
  (global) => {
    const { isPinAccepted } = global;

    return {
      isPinAccepted,
    };
  },
)(PinPad));
