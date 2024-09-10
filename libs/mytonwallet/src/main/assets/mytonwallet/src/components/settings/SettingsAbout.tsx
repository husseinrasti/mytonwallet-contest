import React, { memo } from '../../lib/teact/teact';

import {
  APP_ENV_MARKER, APP_NAME, APP_REPO_URL, APP_VERSION, IS_EXTENSION,
} from '../../config';
import renderText from '../../global/helpers/renderText';
import buildClassName from '../../util/buildClassName';
import { handleOpenUrl } from '../../util/openUrl';

import useHistoryBack from '../../hooks/useHistoryBack';
import useLang from '../../hooks/useLang';
import useScrolledState from '../../hooks/useScrolledState';

import Button from '../ui/Button';
import Emoji from '../ui/Emoji';
import ModalHeader from '../ui/ModalHeader';

import styles from './Settings.module.scss';

import logoSrc from '../../assets/logo.svg';

interface OwnProps {
  isActive?: boolean;
  handleBackClick: () => void;
  isInsideModal?: boolean;
}

function SettingsAbout({ isActive, handleBackClick, isInsideModal }: OwnProps) {
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
          title={lang('About')}
          withNotch={isScrolled}
          onBackButtonClick={handleBackClick}
          className={styles.modalHeader}
        />
      ) : (
        <div className={buildClassName(styles.header, 'with-notch-on-scroll', isScrolled && 'is-scrolled')}>
          <Button isSimple isText onClick={handleBackClick} className={styles.headerBack}>
            <i className={buildClassName(styles.iconChevron, 'icon-chevron-left')} aria-hidden />
            <span>{lang('Back')}</span>
          </Button>
          <span className={styles.headerTitle}>{lang('About')}</span>
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
        <img src={logoSrc} alt={lang('Logo')} className={styles.logo} />
        <h2 className={styles.title}>
          {APP_NAME} {APP_VERSION} {APP_ENV_MARKER}
          <a href="https://mytonwallet.io/" target="_blank" className={styles.titleLink} rel="noreferrer">
            mytonwallet.io
          </a>
        </h2>
        <div className={buildClassName(styles.blockAbout, !isInsideModal && 'custom-scroll')}>
          <p className={styles.text}>
            {renderText(lang('$about_description1'))}
          </p>
          <p className={styles.text}>
            {renderText(lang('$about_description2'))}
          </p>
          {IS_EXTENSION ? (
            <>
              <h3 className={buildClassName(styles.text, styles.heading)}>
                <Emoji from="🥷" /> {lang('What is TON Proxy?')}
              </h3>
              <p className={styles.text}>
                {renderText(lang('$about_extension_description1'))}{' '}
                <a
                  href="https://telegra.ph/TON-Sites-TON-WWW-and-TON-Proxy-09-29-2"
                  target="_blank"
                  rel="noopener noreferrer"
                >
                  {lang('More info and demo.')}
                </a>
              </p>
              <h3 className={buildClassName(styles.text, styles.heading)}>
                <Emoji from="🦄" /> {lang('What is TON Magic?')}
              </h3>
              <p className={styles.text}>
                {renderText(lang('$about_extension_description2'))}
              </p>
              <p className={styles.text}>
                {lang('$about_extension_description3')}{' '}
                <a href="https://telegra.ph/Telegram--TON-11-10" target="_blank" rel="noopener noreferrer">
                  {lang('More info and demo.')}
                </a>
              </p>
            </>
          ) : (
            <>
              <h3 className={buildClassName(styles.text, styles.heading)}>
                {lang('$about_proxy_magic_title', { ninja: <Emoji from="🥷" />, unicorn: <Emoji from="🦄" /> })}
              </h3>
              <p className={styles.text}>
                {lang('$about_proxy_magic_description', {
                  extension_link: (
                    <a href="https://mytonwallet.io/" target="_blank" rel="noreferrer">
                      {renderText(lang('$about_extension_link_text'))}
                    </a>
                  ),
                })}
              </p>
            </>
          )}
          <h3 className={buildClassName(styles.text, styles.heading)}>
            <i className={buildClassName(styles.github, 'icon-github')} aria-hidden /> {lang('Is it open source?')}
          </h3>
          <p className={styles.text}>
            {lang('$about_wallet_github', {
              github_link: (
                <a href={APP_REPO_URL} target="_blank" rel="noreferrer">
                  {renderText(lang('$about_github_link_text'))}
                </a>
              ),
            })}
          </p>
          <h3 className={styles.heading}>
            <i
              className={buildClassName(styles.telegram, 'icon-telegram')}
              aria-hidden
            /> {lang('Is there a community?')}
          </h3>
          <p className={styles.text}>
            {lang('$about_wallet_community', {
              community_link: (
                <a
                  href={lang.code === 'ru' ? 'https://t.me/MyTonWalletRu' : 'https://t.me/MyTonWalletEn'}
                  target="_blank"
                  rel="noreferrer"
                >
                  {renderText(lang('$about_community_link_text'))}
                </a>
              ),
            })}
          </p>
        </div>
        <div className={styles.aboutFooterWrapper}>
          <div className={styles.aboutFooterContent}>
            <a
              href="https://mytonwallet.io/terms-of-use"
              target="_blank"
              rel="noreferrer"
              onClick={handleOpenUrl}
            >{lang('Terms of Use')}
            </a>
            <i className={styles.dotLarge} aria-hidden />
            <a
              href="https://mytonwallet.io/privacy-policy"
              target="_blank"
              rel="noreferrer"
              onClick={handleOpenUrl}
            >{lang('Privacy Policy')}
            </a>
          </div>
        </div>
      </div>
    </div>
  );
}

export default memo(SettingsAbout);
