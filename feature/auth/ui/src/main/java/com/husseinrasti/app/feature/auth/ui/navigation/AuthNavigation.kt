package com.husseinrasti.app.feature.auth.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.husseinrasti.app.component.navigation.NavigationEvent
import com.husseinrasti.app.feature.auth.ui.AuthScreenRoute


@Composable
fun AuthGraph(
    navController: NavHostController,
    onClickNavigation: (NavigationEvent) -> Unit
) {
    NavHost(
        modifier = Modifier,
        startDestination = AuthRouter.Start,
        navController = navController
    ) {
        composable<AuthRouter.Start> {
            AuthScreenRoute(onClickNavigation = onClickNavigation)
        }
    }
}