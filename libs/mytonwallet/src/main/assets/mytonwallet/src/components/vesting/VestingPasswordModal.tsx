import React, { memo, useMemo } from '../../lib/teact/teact';
import { getActions, withGlobal } from '../../global';

import type { ApiToken, ApiVestingInfo } from '../../api/types';
import type { HardwareConnectState, UserToken } from '../../global/types';
import { VestingUnfreezeState } from '../../global/types';

import {
  CLAIM_AMOUNT,
  IS_CAPACITOR,
  TON_SYMBOL,
  TONCOIN_SLUG,
} from '../../config';
import renderText from '../../global/helpers/renderText';
import {
  selectAccount,
  selectCurrentAccountState,
  selectCurrentAccountTokens, selectIsHardwareAccount,
  selectMycoin,
} from '../../global/selectors';
import buildClassName from '../../util/buildClassName';
import { toBig } from '../../util/decimals';
import { formatCurrency } from '../../util/formatNumber';
import resolveModalTransitionName from '../../util/resolveModalTransitionName';
import { shortenAddress } from '../../util/shortenAddress';
import { calcVestingAmountByStatus } from '../main/helpers/calcVestingAmountByStatus';

import useLang from '../../hooks/useLang';
import useLastCallback from '../../hooks/useLastCallback';
import useModalTransitionKeys from '../../hooks/useModalTransitionKeys';

import LedgerConfirmOperation from '../ledger/LedgerConfirmOperation';
import LedgerConnect from '../ledger/LedgerConnect';
import Modal from '../ui/Modal';
import PasswordForm from '../ui/PasswordForm';
import Transition from '../ui/Transition';

import modalStyles from '../ui/Modal.module.scss';
import styles from './VestingModal.module.scss';

interface StateProps {
  isOpen?: boolean;
  tokens?: UserToken[];
  vesting?: ApiVestingInfo[];
  isLoading?: boolean;
  address?: string;
  error?: string;
  state?: VestingUnfreezeState;
  mycoin?: ApiToken;
  hardwareState?: HardwareConnectState;
  isLedgerConnected?: boolean;
  isTonAppConnected?: boolean;
  isHardwareAccount?: boolean;
}
function VestingPasswordModal({
  isOpen,
  vesting,
  tokens,
  isLoading,
  address,
  error,
  mycoin,
  state = VestingUnfreezeState.Password,
  hardwareState,
  isLedgerConnected,
  isTonAppConnected,
  isHardwareAccount,
}: StateProps) {
  const {
    submitClaimingVesting,
    submitClaimingVestingHardware,
    cancelClaimingVesting,
    clearVestingError,
  } = getActions();

  const lang = useLang();
  const {
    amount: balance,
  } = useMemo(() => tokens?.find(({ slug }) => slug === TONCOIN_SLUG), [tokens]) || {};
  const claimAmount = toBig(CLAIM_AMOUNT);
  const hasAmountError = !balance || balance < CLAIM_AMOUNT;
  const { renderingKey, nextKey, updateNextKey } = useModalTransitionKeys(state, Boolean(isOpen));
  const withModalHeader = !isHardwareAccount && !IS_CAPACITOR;

  const currentlyReadyToUnfreezeAmount = useMemo(() => {
    if (!vesting) return '0';

    return calcVestingAmountByStatus(vesting, ['ready']);
  }, [vesting]);

  const handleSubmit = useLastCallback((password: string) => {
    if (hasAmountError) return;
    submitClaimingVesting({ password });
  });

  const handleHardwareSubmit = useLastCallback(() => {
    if (hasAmountError) return;
    submitClaimingVestingHardware();
  });

  if (!mycoin) {
    return undefined;
  }

  function renderInfo() {
    return (
      <>
        <div className={buildClassName(styles.operationInfo, !IS_CAPACITOR && styles.operationInfoWithGap)}>
          <img src={mycoin!.image} alt="" className={styles.tokenIcon} />
          <span className={styles.operationInfoText}>
            {lang('%amount% to %address%', {
              amount: (
                <span className={styles.bold}>
                  {formatCurrency(currentlyReadyToUnfreezeAmount, mycoin!.symbol, mycoin!.decimals)}
                </span>
              ),
              address: <span className={styles.bold}>{shortenAddress(address!)}</span>,
            })}
          </span>
        </div>
        <div className={buildClassName(styles.operationInfoFee, !IS_CAPACITOR && styles.operationInfoFeeWithGap)}>
          {renderText(lang('$fee_value_bold', { fee: formatCurrency(claimAmount, TON_SYMBOL) }))}
        </div>
      </>
    );
  }

  // eslint-disable-next-line consistent-return
  function renderContent(isActive: boolean, isFrom: boolean, currentKey: number) {
    switch (currentKey) {
      case VestingUnfreezeState.ConnectHardware:
        return (
          <LedgerConnect
            isActive={isActive}
            state={hardwareState}
            isLedgerConnected={isLedgerConnected}
            isTonAppConnected={isTonAppConnected}
            onConnected={handleHardwareSubmit}
            onClose={cancelClaimingVesting}
          />
        );

      case VestingUnfreezeState.ConfirmHardware:
        return (
          <LedgerConfirmOperation
            text={lang('Please confirm transaction on your Ledger')}
            error={error}
            onClose={cancelClaimingVesting}
            onTryAgain={handleHardwareSubmit}
          />
        );

      case VestingUnfreezeState.Password:
        return (
          <PasswordForm
            isActive={Boolean(isOpen)}
            isLoading={isLoading}
            withCloseButton
            operationType="unfreeze"
            error={hasAmountError ? lang('Insufficient Balance for Fee') : error}
            submitLabel={lang('Confirm')}
            onSubmit={handleSubmit}
            onCancel={cancelClaimingVesting}
            onUpdate={clearVestingError}
          >
            {renderInfo()}
          </PasswordForm>
        );
    }
  }

  return (
    <Modal
      isOpen={isOpen}
      title={withModalHeader ? lang('Confirm Unfreezing') : undefined}
      hasCloseButton={withModalHeader}
      forceFullNative
      nativeBottomSheetKey="vesting-confirm"
      contentClassName={styles.passwordModalDialog}
      onClose={cancelClaimingVesting}
    >
      <Transition
        name={resolveModalTransitionName()}
        className={buildClassName(modalStyles.transition, 'custom-scroll')}
        slideClassName={modalStyles.transitionSlide}
        activeKey={renderingKey}
        nextKey={nextKey}
        onStop={updateNextKey}
      >
        {renderContent}
      </Transition>
    </Modal>
  );
}

export default memo(withGlobal((global): StateProps => {
  const { address } = selectAccount(global, global.currentAccountId!) || {};
  const accountState = selectCurrentAccountState(global);
  const isHardwareAccount = selectIsHardwareAccount(global);

  const {
    isConfirmRequested: isOpen,
    info: vesting,
    isLoading,
    error,
    unfreezeState,
  } = accountState?.vesting || {};
  const tokens = selectCurrentAccountTokens(global);

  const {
    hardwareState,
    isLedgerConnected,
    isTonAppConnected,
  } = global.hardware;

  return {
    isOpen,
    vesting,
    tokens,
    isLoading,
    error,
    address,
    state: unfreezeState,
    mycoin: selectMycoin(global),
    hardwareState,
    isLedgerConnected,
    isTonAppConnected,
    isHardwareAccount,
  };
})(VestingPasswordModal));
