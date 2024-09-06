package com.husseinrasti.app.feature.wallet.ui.navigation

import com.husseinrasti.app.component.navigation.NavigationEvent

sealed interface MainRouter : NavigationEvent {
    data object Wallet : MainRouter
    data object Assets : MainRouter
    data object Browser : MainRouter
    data object Settings : MainRouter
}
