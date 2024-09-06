package com.husseinrasti.app.feature.wallet.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.husseinrasti.app.component.navigation.NavigateUp
import com.husseinrasti.app.component.navigation.NavigationEvent
import com.husseinrasti.app.feature.wallet.ui.MainRoute
import kotlinx.serialization.Serializable

@Serializable
data object MainScreen

@Composable
fun MainGraph(
    navController: NavHostController,
    onClickNavigation: (NavigationEvent) -> Unit
) {
    NavHost(
        startDestination = MainScreen,
        navController = navController,
        modifier = Modifier
    ) {
        composable<MainScreen> {
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
