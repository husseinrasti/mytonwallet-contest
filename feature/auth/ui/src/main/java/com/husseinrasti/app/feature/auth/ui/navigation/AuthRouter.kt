package com.husseinrasti.app.feature.auth.ui.navigation

import com.husseinrasti.app.component.navigation.NavigationEvent
import kotlinx.serialization.Serializable

sealed interface AuthRouter : NavigationEvent {
    @Serializable
    data object Start : AuthRouter
}
