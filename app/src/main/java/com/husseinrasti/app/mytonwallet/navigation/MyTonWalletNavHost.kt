package com.husseinrasti.app.mytonwallet.navigation

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.husseinrasti.app.component.navigation.NavigateToAuth
import com.husseinrasti.app.component.navigation.NavigateToMain
import com.husseinrasti.app.component.navigation.NavigateUp
import com.husseinrasti.app.component.navigation.NavigationEvent
import com.husseinrasti.app.feature.auth.ui.navigation.authGraph
import com.husseinrasti.app.feature.auth.ui.navigation.navigateToAuth
import com.husseinrasti.app.feature.create.ui.navigation.CreateWalletGraph
import com.husseinrasti.app.feature.wallet.ui.navigation.mainGraph
import com.husseinrasti.app.feature.wallet.ui.navigation.navigateToMain

@Composable
fun MyTonWalletNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    isAuth: Boolean,
) {

    var navigationEvent: NavigationEvent by remember { mutableStateOf(NavigationEvent.Idle) }

    if (isAuth || navigationEvent == NavigateToMain || navigationEvent == NavigateToAuth) {

        val dispatcher = LocalOnBackPressedDispatcherOwner.current!!.onBackPressedDispatcher

        when (navigationEvent) {
            is NavigateUp -> dispatcher.onBackPressed()
            is NavigateToAuth -> navController.navigateToAuth()
            is NavigateToMain -> navController.navigateToMain()
        }

        NavHost(
            navController = navController,
            startDestination = authGraph,
            modifier = modifier,
        ) {

            authGraph(
                navController = navController,
                onClickNavigation = { navigationEvent = it }
            )

            mainGraph(
                navController = navController,
                onClickNavigation = { navigationEvent = it }
            )
        }
    } else {
        CreateWalletGraph(
            navController = navController,
            onClickNavigation = { navigationEvent = it }
        )
    }

}
