package com.husseinrasti.app.feature.auth.ui.navigation

import com.husseinrasti.app.core.navigation.NavigationEvent

sealed interface AuthRouter : NavigationEvent {
    data object Passcode : AuthRouter
}