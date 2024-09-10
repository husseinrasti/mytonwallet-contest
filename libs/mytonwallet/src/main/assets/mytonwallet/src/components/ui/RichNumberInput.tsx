import type { TeactNode } from '../../lib/teact/teact';
import React, {
  memo, useLayoutEffect, useRef, useState,
} from '../../lib/teact/teact';

import { DEFAULT_DECIMAL_PLACES, FRACTION_DIGITS, WHOLE_PART_DELIMITER } from '../../config';
import { forceMeasure, requestMutation } from '../../lib/fasterdom/fasterdom';
import buildClassName from '../../util/buildClassName';
import { saveCaretPosition } from '../../util/saveCaretPosition';

import useFlag from '../../hooks/useFlag';
import useLang from '../../hooks/useLang';
import useLastCallback from '../../hooks/useLastCallback';

import styles from './Input.module.scss';

type OwnProps = {
  id?: string;
  labelText?: React.ReactNode;
  value?: string;
  hasError?: boolean;
  isLoading?: boolean;
  suffix?: string;
  className?: string;
  inputClassName?: string;
  labelClassName?: string;
  valueClassName?: string;
  cornerClassName?: string;
  children?: TeactNode;
  onChange?: (value?: string) => void;
  onBlur?: NoneToVoidFunction;
  onFocus?: NoneToVoidFunction;
  onPressEnter?: (e: React.KeyboardEvent<HTMLDivElement>) => void;
  decimals?: number;
  disabled?: boolean;
  isStatic?: boolean;
};

const MIN_LENGTH_FOR_SHRINK = 5;
const MIN_SIZE_SCALE = 0.25; // 12px
const measureEl = document.createElement('div');

function RichNumberInput({
  id,
  labelText,
  hasError,
  isLoading = false,
  suffix,
  value,
  children,
  className,
  inputClassName,
  labelClassName,
  valueClassName,
  cornerClassName,
  onChange,
  onBlur,
  onFocus,
  onPressEnter,
  decimals = FRACTION_DIGITS,
  disabled = false,
  isStatic = false,
}: OwnProps) {
  // eslint-disable-next-line no-null/no-null
  const inputRef = useRef<HTMLInputElement | null>(null);
  const lang = useLang();
  const [hasFocus, markHasFocus, unmarkHasFocus] = useFlag(false);
  const [isContentEditable, setContentEditable] = useState(!disabled);
  const isFontShrinkedRef = useRef(false);

  const updateFontScale = useLastCallback((content: string) => {
    const input = inputRef.current!;

    forceMeasure(() => {
      const { clientWidth: width } = input;
      measureEl.className = buildClassName(input.className, 'measure-hidden');
      measureEl.style.width = `${width}px`;
      measureEl.innerHTML = content;
      document.body.appendChild(measureEl);
      let delta = 1;

      while (delta > MIN_SIZE_SCALE) {
        measureEl.style.setProperty('--base-font-size', delta.toString());
        if (measureEl.scrollWidth <= width) {
          break;
        }
        delta -= 0.05;
      }

      isFontShrinkedRef.current = delta < 1;
      document.body.removeChild(measureEl);
      measureEl.className = '';
      input.style.setProperty('--base-font-size', delta.toString());
    });
  });

  const handleLoadingHtml = useLastCallback((input: HTMLInputElement, parts?: RegExpMatchArray) => {
    const newHtml = parts ? buildContentHtml({ values: parts, suffix, decimals }) : '';
    input.innerHTML = newHtml;
    setContentEditable(false);

    return newHtml;
  });

  const handleNumberHtml = useLastCallback((input: HTMLInputElement, parts?: RegExpMatchArray) => {
    const newHtml = parts ? buildContentHtml({ values: parts, suffix, decimals }) : '';
    const restoreCaretPosition = document.activeElement === inputRef.current
      ? saveCaretPosition(input, decimals)
      : undefined;

    input.innerHTML = newHtml;
    setContentEditable(!disabled);
    restoreCaretPosition?.();

    return newHtml;
  });

  const updateHtml = useLastCallback((parts?: RegExpMatchArray) => {
    const input = inputRef.current!;
    const content = isLoading ? handleLoadingHtml(input, parts) : handleNumberHtml(input, parts);
    const textContent = parts?.[0] || '';

    if (textContent.length > MIN_LENGTH_FOR_SHRINK || isFontShrinkedRef.current) {
      updateFontScale(content);
    }
    if (content.length) {
      input.classList.remove(styles.isEmpty);
    } else {
      input.classList.add(styles.isEmpty);
    }
  });

  useLayoutEffect(() => {
    if (value === undefined) {
      updateHtml();
    } else {
      updateHtml(getParts(value));
    }
  }, [updateHtml, value]);

  function handleChange(e: React.FormEvent<HTMLDivElement>) {
    const inputValue = e.currentTarget.innerText.trim();
    const newValue = clearValue(inputValue, decimals);
    const parts = getParts(newValue, decimals);
    const isEmpty = inputValue === '';

    requestMutation(() => {
      if (!parts && !isEmpty && value) {
        updateHtml(getParts(value, decimals));
      } else {
        updateHtml(parts);
      }

      if ((newValue || isEmpty) && newValue !== value) {
        onChange?.(newValue);
      }
    });
  }

  const handleFocus = useLastCallback(() => {
    if (disabled) return;

    markHasFocus();
    onFocus?.();
  });

  const handleBlur = useLastCallback(() => {
    if (disabled) return;

    unmarkHasFocus();
    onBlur?.();
  });

  const handleKeyDown = (e: React.KeyboardEvent<HTMLDivElement>) => {
    if (e.key === 'Enter' && onPressEnter) {
      onPressEnter(e);
    }
  };

  const inputWrapperFullClass = buildClassName(
    styles.input__wrapper,
    isStatic && styles.inputWrapperStatic,
    hasError && styles.error,
    hasFocus && styles.input__wrapper_hasFocus,
    inputClassName,
  );
  const inputFullClass = buildClassName(
    styles.input,
    styles.input_rich,
    !value && styles.isEmpty,
    valueClassName,
    isLoading && styles.isLoading,
  );
  const labelTextClassName = buildClassName(
    styles.label,
    hasError && styles.error,
    labelClassName,
  );
  const cornerFullClass = buildClassName(
    cornerClassName,
    hasFocus && styles.swapCorner,
    hasError && styles.swapCorner_error,
  );

  return (
    <div className={buildClassName(styles.wrapper, className)}>
      {Boolean(labelText) && (
        <label
          className={labelTextClassName}
          htmlFor={id}
          id={`${id}Label`}
        >
          {labelText}
        </label>
      )}
      <div className={inputWrapperFullClass}>
        {/* eslint-disable-next-line jsx-a11y/control-has-associated-label */}
        <div
          ref={inputRef}
          contentEditable={isContentEditable}
          id={id}
          role="textbox"
          aria-required
          aria-placeholder={lang('Amount value')}
          aria-labelledby={labelText ? `${id}Label` : undefined}
          tabIndex={0}
          inputMode="decimal"
          className={inputFullClass}
          onKeyDown={handleKeyDown}
          onChange={handleChange}
          onFocus={handleFocus}
          onBlur={handleBlur}
        />
        {children}
      </div>
      {cornerClassName && <div className={cornerFullClass} />}
    </div>
  );
}

function getParts(value: string, decimals = DEFAULT_DECIMAL_PLACES) {
  const regex = getInputRegex(decimals);
  return value.match(regex) || undefined;
}

export function getInputRegex(decimals: number) {
  if (!decimals) return /^(\d+)$/;
  return new RegExp(`^(\\d+)([.,])?(\\d{1,${decimals}})?`);
}

function clearValue(value: string, decimals: number) {
  return value
    .replace(',', '.') // Replace comma to point
    .replace(/[^\d.]/, '') // Remove incorrect symbols
    .match(getInputRegex(decimals))?.[0] // Trim extra decimal places
    .replace(/^0+(?=([1-9]|0\.))/, '') // Trim extra zeros at beginning
    .replace(/^0+$/, '0') // Trim extra zeros (if only zeros are entered)
    ?? '';
}

export function buildContentHtml({
  values,
  suffix,
  decimals = FRACTION_DIGITS,
  withRadix = false,
}: {
  values: RegExpMatchArray;
  suffix?: string;
  decimals?: number;
  withRadix?: boolean;
}) {
  let [, wholePart] = values;
  const [, , dotPart, fractionPart] = values;

  const fractionStr = (fractionPart || dotPart) ? `.${(fractionPart || '').substring(0, decimals)}` : '';
  const suffixStr = suffix ? `&thinsp;${suffix}` : '';

  if (withRadix) {
    wholePart = wholePart.replace(/\d(?=(\d{3})+($|\.))/g, `$&${WHOLE_PART_DELIMITER}`);
  }

  return `${wholePart}<span class="${styles.fractional}">${fractionStr}${suffixStr}</span>`;
}

export default memo(RichNumberInput);
