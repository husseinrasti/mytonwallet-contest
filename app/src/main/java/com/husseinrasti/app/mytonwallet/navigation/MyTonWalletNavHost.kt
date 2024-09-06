package com.husseinrasti.app.mytonwallet.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.husseinrasti.app.component.navigation.NavigateToAuth
import com.husseinrasti.app.component.navigation.NavigateToCreateWallet
import com.husseinrasti.app.component.navigation.NavigateToMain
import com.husseinrasti.app.component.navigation.NavigationEvent
import com.husseinrasti.app.feature.auth.ui.navigation.AuthGraph
import com.husseinrasti.app.feature.create.ui.navigation.CreateWalletGraph
import com.husseinrasti.app.feature.wallet.ui.navigation.MainGraph

@Composable
fun MyTonWalletNavHost(
    navController: NavHostController = rememberNavController(),
    isAuth: Boolean,
) {
    var navigationEvent: NavigationEvent by remember { mutableStateOf(NavigationEvent.Idle) }
    Log.i("TAG", "MyTonWalletNavHost: $navigationEvent")
    when {
        navigationEvent == NavigateToMain -> {
            MainGraph(
                navController = navController,
                onClickNavigation = { navigationEvent = it }
            )
        }

        isAuth || navigationEvent == NavigateToAuth -> {
            AuthGraph(
                navController = navController,
                onClickNavigation = { navigationEvent = it }
            )
        }

        isAuth.not() || navigationEvent == NavigateToCreateWallet -> {
            CreateWalletGraph(
                navController = navController,
                onClickNavigation = { navigationEvent = it }
            )
        }
    }

}
