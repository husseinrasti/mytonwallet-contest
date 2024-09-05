package com.husseinrasti.app.mytonwallet.navigation

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.husseinrasti.app.component.navigation.NavigateToAuth
import com.husseinrasti.app.component.navigation.NavigateUp
import com.husseinrasti.app.component.navigation.NavigationEvent
import com.husseinrasti.app.feature.auth.ui.navigation.authGraph
import com.husseinrasti.app.feature.auth.ui.navigation.navigateToAuth
import com.husseinrasti.app.feature.create.ui.navigation.createWalletGraph
import com.husseinrasti.app.feature.create.ui.navigation.createWalletRoute

@Composable
fun MyTonWalletNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = createWalletRoute,
) {

    var navigationEvent: NavigationEvent by remember { mutableStateOf(NavigationEvent.Idle) }

    val dispatcher = LocalOnBackPressedDispatcherOwner.current!!.onBackPressedDispatcher

    when (navigationEvent) {
        is NavigateUp -> dispatcher.onBackPressed()
        is NavigateToAuth -> navController.navigateToAuth()
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        createWalletGraph(
            navController = navController,
            onClickNavigation = { navigationEvent = it }
        )

        authGraph(
            navController = navController,
            onClickNavigation = { navigationEvent = it }
        )
    }
}
