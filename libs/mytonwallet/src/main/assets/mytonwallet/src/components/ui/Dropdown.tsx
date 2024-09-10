import React, { memo, useMemo } from '../../lib/teact/teact';

import buildClassName from '../../util/buildClassName';

import useFlag from '../../hooks/useFlag';
import useLang from '../../hooks/useLang';

import DropdownMenu from './DropdownMenu';
import Loading from './Loading';

import styles from './Dropdown.module.scss';

export interface DropdownItem {
  value: string;
  name: string;
  description?: string;
  icon?: string;
  fontIcon?: string;
  isDisabled?: boolean;
  isDangerous?: boolean;
  withSeparator?: boolean;
}

interface OwnProps {
  label?: string;
  selectedValue: string;
  items: DropdownItem[];
  className?: string;
  theme?: 'light';
  arrow?: 'caret' | 'chevron';
  menuPosition?: 'top' | 'bottom';
  menuPositionHorizontal?: 'right' | 'left';
  disabled?: boolean;
  shouldTranslateOptions?: boolean;
  onChange?: (value: string) => void;
  isLoading?: boolean;
}

const DEFAULT_ARROW = 'caret';
const DEFAULT_MENU_POSITION_HORIZONTAL = 'right';

function Dropdown({
  label,
  items,
  selectedValue,
  className,
  theme,
  arrow = DEFAULT_ARROW,
  menuPosition,
  menuPositionHorizontal = DEFAULT_MENU_POSITION_HORIZONTAL,
  disabled,
  shouldTranslateOptions,
  onChange,
  isLoading = false,
}: OwnProps): TeactJsx {
  const lang = useLang();
  const [isMenuOpen, openMenu, closeMenu] = useFlag();

  const selectedItem = useMemo(() => {
    return items.find((item) => item.value === selectedValue);
  }, [items, selectedValue]);

  if (!items.length) {
    return undefined;
  }

  const buttonArrowIcon = buildClassName(
    styles.buttonIcon,
    arrow === 'chevron' ? 'icon-chevron-down' : 'icon-caret-down',
  );

  const withMenu = items.length > 1;
  const isFullyInteractive = Boolean(label);
  const fullClassName = buildClassName(
    className,
    theme && styles[theme],
    withMenu && styles.interactive,
    isFullyInteractive && styles.wide,
    isFullyInteractive && disabled && styles.disabled,
  );
  const buttonFullClassName = buildClassName(
    styles.button,
    withMenu && styles.interactive,
    !isFullyInteractive && disabled && styles.disabled,
  );

  return (
    <div
      className={fullClassName}
      onClick={isFullyInteractive && !disabled && withMenu ? openMenu : undefined}
    >
      {label && <span className={styles.label}>{label}</span>}

      {isLoading ? (
        <Loading className={styles.spinner} />
      ) : (
        <button
          type="button"
          className={buttonFullClassName}
          onClick={!isFullyInteractive && withMenu ? openMenu : undefined}
          disabled={disabled}
        >
          {selectedItem?.icon && <img src={selectedItem.icon} alt="" className={styles.itemIcon} />}
          {selectedItem?.fontIcon && (
            <i
              className={buildClassName(`icon-${selectedItem.fontIcon}`, styles.fontIcon)}
              aria-hidden
            />
          )}
          <span className={buildClassName(styles.itemName, 'itemName')}>
            {shouldTranslateOptions ? lang(selectedItem!.name) : selectedItem!.name}
          </span>
          {withMenu && <i className={buttonArrowIcon} aria-hidden />}
        </button>
      )}

      {withMenu && (
        <DropdownMenu
          isOpen={isMenuOpen && !isLoading}
          menuPositionHorizontal={menuPositionHorizontal}
          menuPosition={menuPosition}
          items={items}
          shouldTranslateOptions={shouldTranslateOptions}
          selectedValue={selectedValue}
          onSelect={onChange}
          onClose={closeMenu}
        />
      )}
    </div>
  );
}

export default memo(Dropdown);
