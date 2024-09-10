import React, { memo, useState } from '../../lib/teact/teact';

import buildClassName from '../../util/buildClassName';
import { vibrate } from '../../util/capacitor';

import styles from './PinPad.module.scss';

interface OwnProps {
  value?: any;
  children?: React.ReactNode;
  className?: string;
  isDisabled?: boolean;
  onClick?: (value?: any) => void;
}

const CLICKED_TIMEOUT_MS = 200;

function PinPadButton({
  value, children, className, isDisabled, onClick,
}: OwnProps) {
  const [isClicked, setIsClicked] = useState(false);

  const handleClick = () => {
    void vibrate();
    onClick?.(value);
    setIsClicked(true);
    setTimeout(() => {
      setIsClicked(false);
    }, CLICKED_TIMEOUT_MS);
  };
  return (
    <div
      onMouseDown={!isDisabled ? handleClick : undefined}
      className={buildClassName(styles.button, isClicked && styles.buttonActive, className)}
      role="button"
      tabIndex={0}
    >
      {value || children}
    </div>
  );
}

export default memo(PinPadButton);
