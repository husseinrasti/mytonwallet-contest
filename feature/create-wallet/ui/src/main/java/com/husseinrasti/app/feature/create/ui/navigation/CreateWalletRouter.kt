package com.husseinrasti.app.feature.create.ui.navigation

import com.husseinrasti.app.component.navigation.NavigationEvent

sealed interface CreateWalletRouter : NavigationEvent {
    data object WalletCreation : CreateWalletRouter
    data object PhraseShowing : CreateWalletRouter
    data object PhraseTesting : CreateWalletRouter
    data object PhraseRecovery : CreateWalletRouter
    data object Passcode : CreateWalletRouter
    data object Biometric : CreateWalletRouter
}
