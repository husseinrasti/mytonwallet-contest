import type { TeactNode } from '../../lib/teact/teact';
import React, {
  memo, useLayoutEffect, useRef,
} from '../../lib/teact/teact';

import { FRACTION_DIGITS } from '../../config';
import buildClassName from '../../util/buildClassName';

import useLastCallback from '../../hooks/useLastCallback';

import { buildContentHtml, getInputRegex } from './RichNumberInput';

import styles from './Input.module.scss';

type OwnProps = {
  id?: string;
  labelText?: string;
  value?: string;
  error?: string;
  suffix?: string;
  zeroValue?: string;
  decimals?: number;
  className?: string;
  inputClassName?: string;
  labelClassName?: string;
  valueClassName?: string;
  children?: TeactNode;
};

function RichNumberInput({
  id,
  labelText,
  value,
  error,
  suffix,
  zeroValue,
  decimals = FRACTION_DIGITS,
  className,
  inputClassName,
  labelClassName,
  valueClassName,
  children,
}: OwnProps) {
  // eslint-disable-next-line no-null/no-null
  const contentRef = useRef<HTMLInputElement | null>(null);
  const prevValueRef = useRef<string>('');

  const renderValue = useLastCallback((inputValue = '', noFallbackToPrev = false) => {
    const contentEl = contentRef.current!;

    const valueRegex = getInputRegex(decimals);
    const values = inputValue.toString().match(valueRegex);

    // eslint-disable-next-line no-null/no-null
    if (values === null || values.length < 4 || values[0] === '') {
      if (noFallbackToPrev || inputValue === '') {
        prevValueRef.current = '';
        contentEl.innerText = '';
      } else {
        renderValue(prevValueRef.current, true);
      }

      return;
    }

    prevValueRef.current = inputValue;

    contentEl.innerHTML = buildContentHtml({
      values, suffix, decimals, withRadix: true,
    });
  });

  useLayoutEffect(() => {
    if (value) {
      renderValue(value);
    } else if (zeroValue) {
      contentRef.current!.innerHTML = zeroValue;
    }
  }, [decimals, renderValue, value, zeroValue]);

  const inputWrapperFullClass = buildClassName(
    styles.input__wrapper,
    inputClassName,
  );
  const inputFullClass = buildClassName(
    styles.input,
    styles.input_rich,
    styles.disabled,
    error && styles.error,
    valueClassName,
  );
  const labelTextClassName = buildClassName(
    styles.label,
    labelClassName,
  );

  return (
    <div className={buildClassName(styles.wrapper, className)}>
      {error && (
        <label className={buildClassName(styles.label, styles.label_error, styles.error)} htmlFor={id}>{error}</label>
      )}
      {labelText && (
        <label
          className={labelTextClassName}
          htmlFor={id}
          id={`${id}Label`}
        >
          {labelText}
        </label>
      )}
      <div className={inputWrapperFullClass}>
        <div
          ref={contentRef}
          id={id}
          className={inputFullClass}
        />
        {children}
      </div>
    </div>
  );
}

export default memo(RichNumberInput);
