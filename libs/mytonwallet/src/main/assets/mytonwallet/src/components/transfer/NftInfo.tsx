import React, { memo, type TeactNode, useMemo } from '../../lib/teact/teact';
import { getActions, getGlobal } from '../../global';

import { MediaType, type NftTransfer } from '../../global/types';

import { ANIMATED_STICKER_TINY_SIZE_PX, TON_EXPLORER_NAME } from '../../config';
import buildClassName from '../../util/buildClassName';
import { openUrl } from '../../util/openUrl';
import { shortenAddress } from '../../util/shortenAddress';
import { getTonExplorerNftUrl } from '../../util/url';
import { ANIMATED_STICKERS_PATHS } from '../ui/helpers/animatedAssets';

import useLang from '../../hooks/useLang';
import useLastCallback from '../../hooks/useLastCallback';

import AnimatedIconWithPreview from '../ui/AnimatedIconWithPreview';

import styles from './NftInfo.module.scss';

interface OwnProps {
  nft?: NftTransfer;
  isStatic?: boolean;
  withTonExplorer?: boolean;
}

function NftInfo({
  nft, isStatic, withTonExplorer,
}: OwnProps) {
  const { openMediaViewer } = getActions();
  const lang = useLang();

  const tonExplorerTitle = useMemo(() => {
    return (lang('Open on %ton_explorer_name%', {
      ton_explorer_name: TON_EXPLORER_NAME,
    }) as TeactNode[]
    ).join('');
  }, [lang]);

  const handleClickInfo = (event: React.MouseEvent) => {
    event.stopPropagation();
    const url = getTonExplorerNftUrl(nft!.address, getGlobal().settings.isTestnet)!;

    openUrl(url);
  };

  const handleClick = useLastCallback(() => {
    openMediaViewer({ mediaId: nft!.address, mediaType: MediaType.Nft });
  });

  if (!nft) {
    return (
      <div className={buildClassName(styles.root, isStatic && styles.rootStatic)}>
        <AnimatedIconWithPreview
          play
          noLoop={false}
          nonInteractive
          size={ANIMATED_STICKER_TINY_SIZE_PX}
          className={styles.thumbnail}
          tgsUrl={ANIMATED_STICKERS_PATHS.wait}
          previewUrl={ANIMATED_STICKERS_PATHS.waitPreview}
        />

        <div className={styles.info}>
          <div className={styles.title}>NFT</div>
          <div className={styles.description}>
            {lang('Name and collection are not available. Please check back later.')}
          </div>
        </div>
      </div>
    );
  }

  const name = nft.name || shortenAddress(nft.address);

  function renderContent() {
    return (
      <>
        <img src={nft!.thumbnail} alt={name} className={styles.thumbnail} />

        <div className={styles.info}>
          <div className={styles.title}>
            {name}
            {
              withTonExplorer && (
                <i
                  className={buildClassName(styles.icon, 'icon-tonexplorer-small')}
                  onClick={handleClickInfo}
                  title={tonExplorerTitle}
                  aria-label={tonExplorerTitle}
                  role="button"
                  tabIndex={0}
                />
              )
            }
          </div>
          <div className={styles.description}>{nft!.collectionName}</div>
        </div>
      </>
    );
  }

  return (
    <div
      role="button"
      tabIndex={0}
      aria-label={lang('NFT')}
      className={buildClassName(styles.root, isStatic && styles.rootStatic)}
      onClick={handleClick}
    >
      {renderContent()}
    </div>
  );
}

export default memo(NftInfo);
