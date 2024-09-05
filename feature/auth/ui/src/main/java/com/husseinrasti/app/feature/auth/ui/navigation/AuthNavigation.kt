package com.husseinrasti.app.feature.auth.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.husseinrasti.app.core.navigation.NavigationEvent
import com.husseinrasti.app.feature.auth.ui.StartRoute


const val authRoute = "auth_route"
private const val authStartRoute = "auth_start_route"


fun NavController.navigateToAuth(navOptions: NavOptions? = null) {
    this.navigate(authRoute, navOptions)
}

fun NavGraphBuilder.authGraph(
    navController: NavController,
    onClickNavigation: (NavigationEvent) -> Unit
) {
    navigation(startDestination = authStartRoute, route = authRoute) {
        composable(authStartRoute) {
            StartRoute(onClickNavigation = onClickNavigation)
        }
    }
}