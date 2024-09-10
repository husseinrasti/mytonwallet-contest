import React, { memo, useEffect, useMemo } from '../../lib/teact/teact';
import { getActions, withGlobal } from '../../global';

import type { ApiToken, ApiVestingInfo } from '../../api/types';

import { selectCurrentAccountState, selectMycoin } from '../../global/selectors';
import buildClassName from '../../util/buildClassName';
import { formatFullDay, formatTime } from '../../util/dateFormat';
import { formatCurrency } from '../../util/formatNumber';
import { calcVestingAmountByStatus } from '../main/helpers/calcVestingAmountByStatus';

import useForceUpdate from '../../hooks/useForceUpdate';
import useInterval from '../../hooks/useInterval';
import useLang from '../../hooks/useLang';
import useLastCallback from '../../hooks/useLastCallback';

import { STAKING_DECIMAL } from '../staking/StakingInitial';
import Button from '../ui/Button';
import Modal from '../ui/Modal';
import ModalHeader from '../ui/ModalHeader';
import RichNumberField from '../ui/RichNumberField';

import styles from './VestingModal.module.scss';

interface StateProps {
  isOpen?: boolean;
  isUnfreezeRequested?: boolean;
  vesting?: ApiVestingInfo[];
  mycoin?: ApiToken;
}

const UPDATE_UNSTAKE_DATE_INTERVAL_MS = 30000; // 30 sec
const MY_TOKEN_SYMBOL = 'MY';

function VestingModal({
  isOpen,
  isUnfreezeRequested,
  vesting,
  mycoin,
}: StateProps) {
  const { fetchStakingHistory, closeVestingModal, startClaimingVesting } = getActions();

  const lang = useLang();
  const forceUpdate = useForceUpdate();
  const currentlyFrozenAmount = useMemo(() => {
    if (!vesting) return '0';

    return calcVestingAmountByStatus(vesting, ['frozen', 'ready']);
  }, [vesting]);

  const currentlyReadyToUnfreezeAmount = useMemo(() => {
    if (!vesting) return '0';

    return calcVestingAmountByStatus(vesting, ['ready']);
  }, [vesting]);

  const isDisabledUnfreeze = currentlyReadyToUnfreezeAmount === '0';

  useInterval(forceUpdate, isUnfreezeRequested ? UPDATE_UNSTAKE_DATE_INTERVAL_MS : undefined);

  useEffect(() => {
    if (isOpen) {
      fetchStakingHistory();
    }
  }, [fetchStakingHistory, isOpen]);

  const handleStartClaimingVesting = useLastCallback(() => {
    startClaimingVesting();
  });

  if (!mycoin) {
    return undefined;
  }

  function renderUnfrozenDescription() {
    return (
      <div className={styles.unfreezeTime}>
        <i className={buildClassName(styles.unfreezeTimeIcon, 'icon-clock')} aria-hidden />
        <div>
          {lang('You will receive your unfrozen coins in a few minutes.')}
        </div>
      </div>
    );
  }

  function renderVestingInfo(info: ApiVestingInfo) {
    return (
      <div key={info.id} className={styles.block}>
        <div className={styles.vestingInfo}>
          <div className={styles.blockTitle}>{info.title}</div>
          <div className={styles.blockSubtitle}>
            {lang('%volume% in %amount% parts', {
              volume: <b>{formatCurrency(info.initialAmount, mycoin!.symbol, mycoin!.decimals)}</b>,
              amount: info.parts.length.toString(),
            })}
          </div>
        </div>
        {info.parts.map((part) => {
          const icon = part.status === 'frozen'
            ? 'icon-snow'
            : (part.status === 'missed' ? 'icon-missed' : part.status === 'unfrozen' ? 'icon-check' : 'icon-fire');
          const title = part.status === 'frozen'
            ? 'Frozen'
            : (part.status === 'missed' ? 'Missed' : part.status === 'unfrozen' ? 'Unfrozen' : 'Ready to Unfreeze');
          const isInteractive = !isUnfreezeRequested && part.status === 'ready';
          const endsAt = part.status === 'frozen'
            ? part.time
            : part.status === 'ready'
              ? part.timeEnd
              : undefined;

          return (
            <div
              key={part.id}
              role={isInteractive ? 'button' : undefined}
              className={buildClassName(styles.part, part.status, isInteractive && styles.partInteractive)}
              onClick={isInteractive ? handleStartClaimingVesting : undefined}
            >
              <i
                className={buildClassName(styles.partIcon, icon, part.status)}
                aria-hidden
              />
              <div className={styles.partName}>
                {lang(title)}
                {Boolean(endsAt) && (
                  <div className={styles.date}>
                    {lang('until %date%', {
                      date: (
                        <>
                          <span className={styles.bold}>{formatFullDay(lang.code!, endsAt)}</span>,
                          {' '}{formatTime(endsAt)}
                        </>),
                    })}
                  </div>
                )}
              </div>
              <div className={buildClassName(styles.partValue, part.status)}>
                {formatCurrency(part.amount, mycoin!.symbol, mycoin!.decimals)}
              </div>
            </div>
          );
        })}
      </div>
    );
  }

  function renderVestingSchedule() {
    return (
      <div className={styles.actions}>
        <div className={styles.actionsTitle}>{lang('Vesting Schedule')}</div>
        <div className={buildClassName(styles.actionsList, 'custom-scroll')}>
          {vesting?.map(renderVestingInfo)}
        </div>
      </div>
    );
  }

  function renderContent() {
    return (
      <>
        <div className={styles.amountWrapper}>
          <ModalHeader
            title={lang('Vesting')}
            className={styles.modalHeader}
            closeClassName={styles.close}
            onClose={closeVestingModal}
          />
          <RichNumberField
            labelText={lang('Vesting Balance')}
            zeroValue="..."
            value={currentlyFrozenAmount || '0'}
            decimals={STAKING_DECIMAL}
            suffix={MY_TOKEN_SYMBOL}
            className={styles.inputWrapper}
            labelClassName={styles.inputLabel}
            valueClassName={styles.inputValue}
          />
          {isUnfreezeRequested
            ? renderUnfrozenDescription()
            : (
              <>
                <RichNumberField
                  labelText={lang('Ready to Unfreeze')}
                  zeroValue="..."
                  value={currentlyReadyToUnfreezeAmount}
                  decimals={STAKING_DECIMAL}
                  suffix={MY_TOKEN_SYMBOL}
                  className={styles.inputWrapper}
                  inputClassName={styles.unfreezeInput}
                  labelClassName={styles.inputLabel}
                  valueClassName={styles.unfreezeResult}
                />
                <div className={styles.buttons}>
                  <Button
                    className={styles.button}
                    isPrimary
                    isDisabled={isDisabledUnfreeze}
                    onClick={!isDisabledUnfreeze ? handleStartClaimingVesting : undefined}
                  >
                    {lang('Unfreeze')}
                  </Button>
                </div>
              </>
            )}
        </div>
        {renderVestingSchedule()}
      </>
    );
  }

  return (
    <Modal
      isOpen={isOpen}
      contentClassName={styles.modalDialog}
      nativeBottomSheetKey="vesting-info"
      forceFullNative
      onClose={closeVestingModal}
    >
      {renderContent()}
    </Modal>
  );
}

export default memo(withGlobal((global): StateProps => {
  const accountState = selectCurrentAccountState(global);
  const { info: vesting, unfreezeRequestedIds } = accountState?.vesting || {};

  return {
    isOpen: global.isVestingModalOpen,
    vesting,
    isUnfreezeRequested: Boolean(unfreezeRequestedIds?.length),
    mycoin: selectMycoin(global),
  };
})(VestingModal));
