package com.husseinrasti.app.core.navigation

interface NavigationEvent {
    data object Idle : NavigationEvent
}

data object NavigateUp : NavigationEvent
data object NavigateCreateWallet : NavigationEvent
data object NavigateImportWallet : NavigationEvent
data object NavigateAuth : NavigationEvent