package com.husseinrasti.app.feature.auth.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.husseinrasti.app.component.navigation.NavigationEvent
import com.husseinrasti.app.feature.auth.ui.AuthScreenRoute


const val authGraph = "auth_graph"
const val authScreenRoute = "auth_screen_route"


fun NavController.navigateToAuth(navOptions: NavOptions? = null) {
    this.navigate(authGraph, navOptions)
}

fun NavGraphBuilder.authGraph(
    navController: NavController,
    onClickNavigation: (NavigationEvent) -> Unit
) {
    navigation(startDestination = authScreenRoute, route = authGraph) {
        composable(authScreenRoute) {
            AuthScreenRoute(onClickNavigation = onClickNavigation)
        }
    }
}