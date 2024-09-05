package com.husseinrasti.app.feature.wallet.ui.navigation

import androidx.navigation.*
import androidx.navigation.compose.composable
import com.husseinrasti.app.component.navigation.NavigateUp
import com.husseinrasti.app.component.navigation.NavigationEvent
import com.husseinrasti.app.feature.wallet.ui.MainRoute

const val mainGraph = "main_graph"
private const val walletScreenRoute = "main_screen_route"

fun NavController.navigateToMain(navOptions: NavOptions? = null) {
    this.navigate(mainGraph, navOptions)
}

fun NavGraphBuilder.mainGraph(
    navController: NavController,
    onClickNavigation: (NavigationEvent) -> Unit
) {
    navigation(startDestination = walletScreenRoute, route = mainGraph) {
        composable(route = walletScreenRoute) {
            MainRoute(
                onClickNavigation = { event ->
                    onNavigateByEvent(
                        event = event,
                        onClickNavigation = onClickNavigation,
                        navController = navController
                    )
                }
            )
        }
    }
}

private fun onNavigateByEvent(
    event: NavigationEvent,
    onClickNavigation: (NavigationEvent) -> Unit,
    navController: NavController
) {
    when (event) {
        NavigateUp -> navController.navigateUp()

        else -> onClickNavigation(event)
    }
}
