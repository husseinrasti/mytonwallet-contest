package com.husseinrasti.app.feature.create.ui.navigation

import com.husseinrasti.app.core.navigation.NavigationEvent

sealed interface CreateWalletRouter : NavigationEvent {
    data object WalletCreation : CreateWalletRouter
    data object PhraseShowing : CreateWalletRouter
    data object PhraseTesting : CreateWalletRouter
    data object Passcode : CreateWalletRouter
}
