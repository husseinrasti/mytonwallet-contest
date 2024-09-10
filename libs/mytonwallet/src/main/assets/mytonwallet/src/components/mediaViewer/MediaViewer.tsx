import React, {
  memo, useEffect, useLayoutEffect, useMemo,
} from '../../lib/teact/teact';
import { getActions, withGlobal } from '../../global';

import type { ApiNft } from '../../api/types';
import { MediaType } from '../../global/types';

import { ANIMATION_END_DELAY } from '../../config';
import { selectCurrentAccountState } from '../../global/selectors';
import buildClassName from '../../util/buildClassName';
import captureEscKeyListener from '../../util/captureEscKeyListener';
import { MEMO_EMPTY_ARRAY } from '../../util/memo';
import { animateClosing, animateOpening, ANIMATION_DURATION } from './helpers/ghostAnimation';

import { dispatchHeavyAnimationEvent } from '../../hooks/useHeavyAnimationCheck';
import useLang from '../../hooks/useLang';
import useLastCallback from '../../hooks/useLastCallback';
import usePrevious from '../../hooks/usePrevious';

import ShowTransition from '../ui/ShowTransition';
import Transition from '../ui/Transition';
import Actions from './Actions';
import MediaInfo from './MediaInfo';
import Slides from './Slides';

import styles from './MediaViewer.module.scss';

interface StateProps {
  mediaId?: string;
  txId?: string;
  hiddenNfts?: 'user' | 'scam';
  noGhostAnimation?: boolean;
  mediaIds: string[];
  mediaUrl?: string;
  mediaType: MediaType;
  mediaByIds?: Record<string, ApiNft>;
  blacklistedIds: string[];
  withAnimation: boolean;
}

function MediaViewer({
  mediaId, mediaIds, mediaType, mediaUrl, withAnimation, mediaByIds, blacklistedIds, txId, hiddenNfts, noGhostAnimation,
}: StateProps) {
  const { closeMediaViewer, openMediaViewer } = getActions();

  const isOpen = Boolean(mediaId);
  const lang = useLang();
  const prevMediaId = usePrevious(mediaId);
  const prevTxId = usePrevious(txId);
  const prevHiddenNfts = usePrevious(hiddenNfts);
  const prevNoGhostAnimation = usePrevious(noGhostAnimation);
  const headerAnimation = withAnimation ? 'slideFade' : 'none';
  const shouldAnimateOpening = withAnimation && isOpen && !prevMediaId && !noGhostAnimation;
  const shouldAnimateClosing = withAnimation && !isOpen && !!prevMediaId && !prevNoGhostAnimation;

  const handleClose = useLastCallback(() => closeMediaViewer());

  const renderedMediaIds = useMemo(() => {
    return mediaIds.filter((id) => {
      const media = mediaByIds?.[id];
      return media && !media.isHidden && !blacklistedIds.includes(id);
    });
  }, [blacklistedIds, mediaByIds, mediaIds]);
  const selectedMediaIndex = renderedMediaIds.indexOf(mediaId!);

  const getMediaId = useLastCallback((fromId?: string, direction?: number): string | undefined => {
    if (fromId === undefined) return undefined;
    const index = renderedMediaIds.indexOf(fromId);
    if ((direction === -1 && index > 0) || (direction === 1 && index < renderedMediaIds.length - 1)) {
      return renderedMediaIds[index + direction];
    }
    return undefined;
  });

  const selectMedia = useLastCallback((id: string) => {
    openMediaViewer({ mediaType, mediaId: id });
  });

  useLayoutEffect(() => {
    document.body.classList.toggle('is-media-viewer-open', isOpen);
  }, [isOpen]);

  useEffect(() => (isOpen ? captureEscKeyListener(handleClose) : undefined), [handleClose, isOpen]);

  useEffect(() => {
    if (shouldAnimateOpening) {
      dispatchHeavyAnimationEvent(ANIMATION_DURATION + ANIMATION_END_DELAY);
      animateOpening(mediaType, mediaId, mediaUrl, txId, hiddenNfts);
    }
    if (shouldAnimateClosing) {
      dispatchHeavyAnimationEvent(ANIMATION_DURATION + ANIMATION_END_DELAY);
      animateClosing(mediaType, prevMediaId, prevTxId, prevHiddenNfts);
    }
  }, [
    shouldAnimateOpening,
    shouldAnimateClosing,
    mediaId,
    mediaType,
    mediaUrl,
    prevMediaId,
    txId,
    prevTxId,
    hiddenNfts,
    prevHiddenNfts,
  ]);

  return (
    <ShowTransition
      className={buildClassName(styles.root, 'opacity-transition', 'slow')}
      isOpen={isOpen}
      shouldAnimateFirstRender
      isCustom
    >
      <div className={styles.header} dir={lang.isRtl ? 'rtl' : undefined}>
        <Transition activeKey={selectedMediaIndex} name={headerAnimation} className={styles.headerTransition}>
          <MediaInfo mediaId={mediaId} />
        </Transition>
        <Actions mediaId={mediaId} onClose={handleClose} />
      </div>
      <Slides
        isOpen={isOpen}
        mediaId={mediaId}
        getMediaId={getMediaId}
        selectMedia={selectMedia}
        withAnimation={withAnimation}
        onClose={handleClose}
      />
    </ShowTransition>
  );
}

export default memo(withGlobal((global): StateProps => {
  const {
    mediaId, mediaType = MediaType.Nft, txId, hiddenNfts, noGhostAnimation,
  } = global.mediaViewer || {};
  const animationLevel = global.settings?.animationLevel;
  const accountState = selectCurrentAccountState(global);

  let mediaIds: string[] = MEMO_EMPTY_ARRAY;
  let mediaUrl: string | undefined;
  let mediaByIds: Record<string, ApiNft> | undefined;
  let blacklistedIds: string[] = MEMO_EMPTY_ARRAY;

  if (mediaType === MediaType.Nft) {
    const { orderedAddresses, byAddress } = accountState?.nfts || {};
    const { blacklistedNftAddresses } = accountState || {};
    const nft = byAddress?.[mediaId!];
    mediaUrl = nft?.image || nft?.thumbnail;
    mediaIds = orderedAddresses || MEMO_EMPTY_ARRAY;
    mediaByIds = byAddress;
    if (blacklistedNftAddresses?.length) {
      blacklistedIds = blacklistedNftAddresses;
    }
  }

  return {
    mediaId,
    txId,
    hiddenNfts,
    noGhostAnimation,
    mediaIds,
    mediaType,
    mediaUrl,
    mediaByIds,
    blacklistedIds,
    withAnimation: animationLevel > 0,
  };
})(MediaViewer));
