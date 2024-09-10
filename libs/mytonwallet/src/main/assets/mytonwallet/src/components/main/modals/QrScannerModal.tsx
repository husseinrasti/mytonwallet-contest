import type { StartScanOptions } from '@capacitor-mlkit/barcode-scanning';
import { BarcodeFormat, BarcodeScanner } from '@capacitor-mlkit/barcode-scanning';
import React, { memo, useRef, useState } from '../../../lib/teact/teact';
import { addExtraClass, removeExtraClass } from '../../../lib/teact/teact-dom';
import { getActions } from '../../../global';

import buildClassName from '../../../util/buildClassName';
import { vibrateOnSuccess } from '../../../util/capacitor';
import { pause } from '../../../util/schedulers';
import { DPR, IS_DELEGATING_BOTTOM_SHEET, IS_IOS } from '../../../util/windowEnvironment';

import useEffectOnce from '../../../hooks/useEffectOnce';
import useEffectWithPrevDeps from '../../../hooks/useEffectWithPrevDeps';
import useLang from '../../../hooks/useLang';
import useLastCallback from '../../../hooks/useLastCallback';

import Modal from '../../ui/Modal';

import modalStyles from '../../ui/Modal.module.scss';
import styles from './QrScannerModal.module.scss';

interface OwnProps {
  isOpen?: boolean;
  onClose: NoneToVoidFunction;
}

type Point = [number, number];
type Square = [Point, Point, Point, Point];

const START_SCAN_DELAY_MS = IS_IOS ? 160 : 360;
const MODAL_ANIMATION_DURATION_MS = IS_IOS ? 650 : 500;
const DESTROY_SCANNER_DELAY_MS = IS_IOS ? 250 : 500;

function QrScannerModal({ isOpen, onClose }: OwnProps) {
  const {
    handleQrCode,
  } = getActions();

  const [isFlashlightAvailable, setIsFlashlightAvailable] = useState(false);
  const [isFlashlightEnabled, setIsFlashlightEnabled] = useState(false);
  const [isScannerStarted, setIsScannerStarted] = useState(false);

  const lang = useLang();
  // eslint-disable-next-line no-null/no-null
  const scanSquareRef = useRef<HTMLDivElement>(null);

  useEffectOnce(() => {
    BarcodeScanner
      .isTorchAvailable()
      .then((result) => {
        setIsFlashlightAvailable(result.available);
      });
    BarcodeScanner
      .isTorchEnabled()
      .then((result) => {
        setIsFlashlightEnabled(result.enabled);
      });
  });

  const onCloseAnimationEnd = useLastCallback(async () => {
    removeExtraClass(document.documentElement, styles.documentRoot);
    await BarcodeScanner.stopScan();
  });

  const handleClose = useLastCallback(() => {
    setIsScannerStarted(false);
    onClose();
  });

  const startScan = useLastCallback(async () => {
    const options: StartScanOptions = {
      formats: [BarcodeFormat.QrCode],
    };

    const scanAreaCoordinates = getDomNodeDimensions(scanSquareRef.current);

    await BarcodeScanner.removeAllListeners();
    const listener = await BarcodeScanner.addListener(
      'barcodeScanned',
      async (event) => {
        let { cornerPoints: qrCodeCoordinates } = event.barcode;
        qrCodeCoordinates = qrCodeCoordinates ? sortCoordinatesClockwise(qrCodeCoordinates) : undefined;

        if (
          !scanAreaCoordinates
          || !qrCodeCoordinates
          || isQrCodeOutsideScanArea(scanAreaCoordinates, qrCodeCoordinates)
        ) {
          return;
        }

        await listener.remove();
        await vibrateOnSuccess();
        handleQrCode({ data: event.barcode.rawValue });
        handleClose();
        await pause(DESTROY_SCANNER_DELAY_MS);
        if (IS_IOS) {
          removeExtraClass(document.documentElement, styles.documentRoot);
        }
      },
    );

    setIsScannerStarted(true);
    await BarcodeScanner.startScan(options);
  });

  useEffectWithPrevDeps(([prevIsOpen]) => {
    if (IS_DELEGATING_BOTTOM_SHEET) return undefined;

    let startScanTimeoutId: number;
    let documentClassModifyTimeoutId: number;
    if (isOpen) {
      startScanTimeoutId = window.setTimeout(startScan, START_SCAN_DELAY_MS);
      documentClassModifyTimeoutId = window.setTimeout(() => {
        addExtraClass(document.documentElement, styles.documentRoot);
      }, MODAL_ANIMATION_DURATION_MS);
    } else if (prevIsOpen) {
      handleClose();
    }

    return () => {
      if (startScanTimeoutId) window.clearTimeout(startScanTimeoutId);
      if (documentClassModifyTimeoutId) window.clearTimeout(documentClassModifyTimeoutId);
    };
  }, [isOpen, handleClose, startScan]);

  const handleFlashlightClick = useLastCallback(async () => {
    if (isFlashlightEnabled) {
      await BarcodeScanner.disableTorch();
    } else {
      await BarcodeScanner.enableTorch();
    }
    setIsFlashlightEnabled(!isFlashlightEnabled);
  });

  const flashlightButtonClassName = buildClassName(
    styles.flashLightButton,
    isFlashlightEnabled && styles.flashLightButtonEnabled,
  );

  return (
    <Modal
      isOpen={isOpen}
      onClose={handleClose}
      nativeBottomSheetKey="qr-scanner"
      forceFullNative
      onCloseAnimationEnd={onCloseAnimationEnd}
    >
      <div className={buildClassName(styles.scanner, isScannerStarted && styles.scannerStarted)}>
        <div
          className={styles.closeButton}
          aria-label={lang('Close')}
          role="button"
          tabIndex={0}
          onClick={handleClose}
        >
          <i className={buildClassName(modalStyles.closeIcon, 'icon-close')} aria-hidden />
        </div>

        {isFlashlightAvailable && (
          <div
            className={flashlightButtonClassName}
            onClick={handleFlashlightClick}
            aria-label={lang('Toggle Flashlight')}
            role="button"
            tabIndex={0}
          >
            <i className="icon-flashlight" aria-hidden />
          </div>
        )}

        <div className={styles.title}>{lang('Scan QR Code')}</div>

        <div className={styles.square} ref={scanSquareRef}>
          <div className={styles.squareInner} />
        </div>
      </div>
    </Modal>
  );
}

export default memo(QrScannerModal);

function sortCoordinatesClockwise(coordinates: Square): Square {
  coordinates.sort((a, b) => a[1] - b[1]);

  const topHalf = coordinates.slice(0, 2) as [Point, Point];
  const bottomHalf = coordinates.slice(2) as [Point, Point];

  topHalf.sort((a, b) => b[0] - a[0]);
  bottomHalf.sort((a, b) => a[0] - b[0]);

  return [...topHalf, ...bottomHalf];
}

function getDomNodeDimensions(node: HTMLDivElement | null): Square | undefined {
  if (!node) {
    return undefined;
  }

  const scanSquareRect = node.getBoundingClientRect();
  const scaledRect = {
    left: scanSquareRect.left * DPR,
    right: scanSquareRect.right * DPR,
    top: scanSquareRect.top * DPR,
    bottom: scanSquareRect.bottom * DPR,
    width: scanSquareRect.width * DPR,
    height: scanSquareRect.height * DPR,
  };

  return [
    [scaledRect.left, scaledRect.top],
    [scaledRect.left + scaledRect.width, scaledRect.top],
    [scaledRect.left + scaledRect.width, scaledRect.top + scaledRect.height],
    [scaledRect.left, scaledRect.top + scaledRect.height],
  ];
}

function isQrCodeOutsideScanArea(scanArea: Square, qrCodeCoordinates: Square): boolean {
  return (
    scanArea[0][0] > qrCodeCoordinates[0][0]
    || scanArea[0][1] > qrCodeCoordinates[0][1]
    || scanArea[1][0] < qrCodeCoordinates[1][0]
    || scanArea[1][1] > qrCodeCoordinates[1][1]
    || scanArea[2][0] < qrCodeCoordinates[2][0]
    || scanArea[2][1] < qrCodeCoordinates[2][1]
    || scanArea[3][0] > qrCodeCoordinates[3][0]
    || scanArea[3][1] < qrCodeCoordinates[3][1]
  );
}
