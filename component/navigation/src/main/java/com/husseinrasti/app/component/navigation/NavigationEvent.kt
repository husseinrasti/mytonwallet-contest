package com.husseinrasti.app.component.navigation

interface NavigationEvent {
    data object Idle : NavigationEvent
}

data object NavigateUp : NavigationEvent
data object NavigateToMain : NavigationEvent
data object NavigateToCreateWallet : NavigationEvent
data object NavigateToAuth : NavigationEvent
