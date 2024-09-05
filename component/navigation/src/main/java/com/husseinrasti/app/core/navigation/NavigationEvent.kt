package com.husseinrasti.app.core.navigation

interface NavigationEvent {
    data object Idle : NavigationEvent
}

data object NavigateUp : NavigationEvent
data object NavigateToWallet : NavigationEvent
data object NavigateToCreateWallet : NavigationEvent
data object NavigateToImportWallet : NavigationEvent
data object NavigateToAuth : NavigationEvent
