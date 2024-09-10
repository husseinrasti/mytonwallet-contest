import {
  BURN_ADDRESS, NOTCOIN_EXCHANGERS, NOTCOIN_VOUCHERS_ADDRESS, TONCOIN_SLUG,
} from '../../../config';
import { findDifference } from '../../../util/iteratees';
import { IS_DELEGATING_BOTTOM_SHEET } from '../../../util/windowEnvironment';
import { NFT_TRANSFER_AMOUNT } from '../../../api/blockchains/ton/constants';
import { addActionHandler } from '../../index';
import { updateCurrentAccountState } from '../../reducers';
import { selectCurrentAccountState } from '../../selectors';

import { getIsPortrait } from '../../../hooks/useDeviceScreen';

const NBS_INIT_TIMEOUT = IS_DELEGATING_BOTTOM_SHEET ? 100 : 0;

addActionHandler('burnNfts', (global, actions, { nfts }) => {
  actions.startTransfer({
    isPortrait: getIsPortrait(),
    nfts,
  });

  const isNotcoinVouchers = nfts.some((n) => n.collectionAddress === NOTCOIN_VOUCHERS_ADDRESS);

  setTimeout(() => {
    actions.submitTransferInitial({
      tokenSlug: TONCOIN_SLUG,
      amount: NFT_TRANSFER_AMOUNT,
      toAddress: isNotcoinVouchers ? NOTCOIN_EXCHANGERS[0] : BURN_ADDRESS,
      nftAddresses: nfts.map(({ address }) => address),
    });
  }, NBS_INIT_TIMEOUT);
});

addActionHandler('addNftsToBlacklist', (global, actions, { addresses: nftAddresses }) => {
  // Force hide NFT - remove it from whitelist and add to blacklist
  let { blacklistedNftAddresses = [], whitelistedNftAddresses = [] } = selectCurrentAccountState(global) || {};
  blacklistedNftAddresses = findDifference(blacklistedNftAddresses, nftAddresses);
  whitelistedNftAddresses = findDifference(whitelistedNftAddresses, nftAddresses);

  return updateCurrentAccountState(global, {
    blacklistedNftAddresses: [...blacklistedNftAddresses, ...nftAddresses],
    whitelistedNftAddresses,
  });
});

addActionHandler('addNftsToWhitelist', (global, actions, { addresses: nftAddresses }) => {
  // Force show NFT - remove it from blacklist and add to whitelist
  let { blacklistedNftAddresses = [], whitelistedNftAddresses = [] } = selectCurrentAccountState(global) || {};
  blacklistedNftAddresses = findDifference(blacklistedNftAddresses, nftAddresses);
  whitelistedNftAddresses = findDifference(whitelistedNftAddresses, nftAddresses);

  return updateCurrentAccountState(global, {
    blacklistedNftAddresses,
    whitelistedNftAddresses: [...whitelistedNftAddresses, ...nftAddresses],
  });
});

addActionHandler('removeNftSpecialStatus', (global, actions, { address: nftAddress }) => {
  // Stop forcing to show/hide NFT if it was in whitelist/blacklist
  let { blacklistedNftAddresses = [], whitelistedNftAddresses = [] } = selectCurrentAccountState(global) || {};

  blacklistedNftAddresses = blacklistedNftAddresses.filter((address) => address !== nftAddress);
  whitelistedNftAddresses = whitelistedNftAddresses.filter((address) => address !== nftAddress);

  return updateCurrentAccountState(global, {
    blacklistedNftAddresses,
    whitelistedNftAddresses,
  });
});

addActionHandler('openUnhideNftModal', (global, actions, { address, name }) => {
  return updateCurrentAccountState(global, {
    isUnhideNftModalOpen: true,
    selectedNftToUnhide: { address, name },
  });
});

addActionHandler('closeUnhideNftModal', (global) => {
  return updateCurrentAccountState(global, {
    isUnhideNftModalOpen: undefined,
    selectedNftToUnhide: undefined,
  });
});

addActionHandler('openHideNftModal', (global, actions, { addresses, isCollection }) => {
  return updateCurrentAccountState(global, {
    selectedNftsToHide: { addresses, isCollection },
  });
});

addActionHandler('closeHideNftModal', (global) => {
  return updateCurrentAccountState(global, {
    selectedNftsToHide: undefined,
  });
});
