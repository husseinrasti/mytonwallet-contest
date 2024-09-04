package com.husseinrasti.app.core.navigation

interface NavigationEvent {
    object Idle : NavigationEvent
}

object NavigateUp : NavigationEvent
object NavigateCreateWallet : NavigationEvent
object NavigateImportWallet : NavigationEvent
object NavigateAuth : NavigationEvent