package com.husseinrasti.app.feature.wallet.ui

import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.husseinrasti.app.component.navigation.NavigateUp
import com.husseinrasti.app.component.navigation.NavigationEvent
import com.husseinrasti.app.component.ui.MyTonWalletTopAppBar
import com.husseinrasti.app.component.theme.MyTonWalletContestTheme
import com.husseinrasti.app.feature.wallet.ui.wallet.WalletRoute

@Composable
internal fun MainRoute(
    onClickNavigation: (NavigationEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val wallet = BottomNavigationItem(route = "wallet", icon = Icons.Default.Home, title = "Wallet")
    val assets = BottomNavigationItem(route = "assets", icon = Icons.Default.Search, title = "Assets")
    val browser = BottomNavigationItem(route = "browser", icon = Icons.Default.Info, title = "Browser")
    val settings = BottomNavigationItem(route = "settings", icon = Icons.Default.Settings, title = "Settings")

    val bottomNavigationItems = listOf(wallet, assets, browser, settings)

    val navController = rememberNavController()

    Scaffold(
        topBar = {
            MyTonWalletTopAppBar(
                onClickNavigation = { onClickNavigation(NavigateUp) },
                elevation = 0.dp
            )
        },
        bottomBar = {
            BottomNavigationBar(bottomNavigationItems = bottomNavigationItems, navController = navController)
        }
    ) {
        NavHost(navController = navController, startDestination = bottomNavigationItems.first().route) {
            composable(wallet.title) {
                WalletRoute(
                    onClickNavigation = onClickNavigation,
                    modifier = modifier
                )
            }
            composable(assets.title) {
                Text(assets.title)
            }
            composable(browser.title) {
                Text(browser.title)
            }
            composable(settings.title) {
                Text(settings.title)
            }
        }
    }
}

@Preview
@Composable
private fun MainPreview() {
    MyTonWalletContestTheme {
        MainRoute(
            modifier = Modifier,
            onClickNavigation = {},
        )
    }
}