import React, { memo } from '../../lib/teact/teact';

import { ANIMATED_STICKER_MIDDLE_SIZE_PX } from '../../config';
import renderText from '../../global/helpers/renderText';
import buildClassName from '../../util/buildClassName';
import { ANIMATED_STICKERS_PATHS } from '../ui/helpers/animatedAssets';

import useHistoryBack from '../../hooks/useHistoryBack';
import useLang from '../../hooks/useLang';
import useScrolledState from '../../hooks/useScrolledState';

import AnimatedIconWithPreview from '../ui/AnimatedIconWithPreview';
import Button from '../ui/Button';
import ModalHeader from '../ui/ModalHeader';

import styles from './Settings.module.scss';

interface OwnProps {
  isActive: boolean;
  handleBackClick: () => void;
  isInsideModal?: boolean;
}

function SettingsDisclaimer({ isActive, handleBackClick, isInsideModal }: OwnProps) {
  const lang = useLang();

  useHistoryBack({
    isActive,
    onBack: handleBackClick,
  });

  const {
    handleScroll: handleContentScroll,
    isScrolled,
  } = useScrolledState();

  return (
    <div className={styles.slide}>
      {isInsideModal ? (
        <ModalHeader
          title=""
          withNotch={isScrolled}
          onBackButtonClick={handleBackClick}
        />
      ) : (
        <div className={buildClassName(styles.header, 'with-notch-on-scroll', isScrolled && 'is-scrolled')}>
          <Button isSimple isText onClick={handleBackClick} className={styles.headerBack}>
            <i className={buildClassName(styles.iconChevron, 'icon-chevron-left')} aria-hidden />
            <span>{lang('Back')}</span>
          </Button>
        </div>
      )}
      <div
        className={buildClassName(
          styles.content,
          isInsideModal && 'custom-scroll',
          !isInsideModal && styles.content_noScroll,
        )}
        onScroll={isInsideModal ? handleContentScroll : undefined}
      >
        <div className={styles.stickerAndTitle}>
          <AnimatedIconWithPreview
            play={isActive}
            tgsUrl={ANIMATED_STICKERS_PATHS.snitch}
            previewUrl={ANIMATED_STICKERS_PATHS.snitchPreview}
            noLoop={false}
            nonInteractive
            size={ANIMATED_STICKER_MIDDLE_SIZE_PX}
          />
          <div className={styles.sideTitle}>{lang('Use Responsibly')}</div>
        </div>
        <div className={buildClassName(styles.blockAbout, !isInsideModal && 'custom-scroll')}>
          <p className={styles.text}>{renderText(lang('$auth_responsibly_description1'))}</p>
          <p className={styles.text}>{renderText(lang('$auth_responsibly_description2'))}</p>
          <p className={styles.text}>{renderText(lang('$auth_responsibly_description3'))}</p>
          <p className={styles.text}>{renderText(lang('$auth_responsibly_description4'))}</p>
        </div>
      </div>
    </div>
  );
}

export default memo(SettingsDisclaimer);
