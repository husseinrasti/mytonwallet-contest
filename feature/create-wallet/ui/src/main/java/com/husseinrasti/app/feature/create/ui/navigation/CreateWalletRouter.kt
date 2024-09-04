package com.husseinrasti.app.feature.create.ui.navigation

import com.husseinrasti.app.core.navigation.NavigationEvent

sealed interface CreateWalletRouter : NavigationEvent {
    data object Start : CreateWalletRouter
    data object Congratulations : CreateWalletRouter
    data object GeneratePhrase : CreateWalletRouter
    data object TestPhrase : CreateWalletRouter
}
