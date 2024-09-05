package com.husseinrasti.app.feature.create.ui.navigation

import androidx.navigation.*
import androidx.navigation.compose.composable
import com.husseinrasti.app.core.navigation.NavigationEvent
import com.husseinrasti.app.feature.create.ui.creation.WalletCreatedRoute
import com.husseinrasti.app.feature.create.ui.passcode.PasscodeRoute
import com.husseinrasti.app.feature.create.ui.phrase.recovery.RecoveryPhraseRoute
import com.husseinrasti.app.feature.create.ui.phrase.test.TestPhraseRoute
import com.husseinrasti.app.feature.create.ui.start.StartRoute

const val createWalletRoute = "create_wallet_route"
private const val startScreenRoute = "start_screen_route"
private const val walletCreatedScreenRoute = "wallet_created_route"
private const val generatePhraseScreenRoute = "generate_phrase_screen_route"
private const val testPhraseScreenRoute = "test_phrase_screen_route"
private const val passcodeScreenRoute = "passcode_route"

fun NavController.navigateToCreateWallet(navOptions: NavOptions? = null) {
    this.navigate(createWalletRoute, navOptions)
}

fun NavGraphBuilder.createWalletGraph(
    navController: NavController,
    onClickNavigation: (NavigationEvent) -> Unit
) {
    navigation(startDestination = startScreenRoute, route = createWalletRoute) {
        composable(route = startScreenRoute) {
            StartRoute(onClickNavigation = { event ->
                onNavigateByEvent(
                    event = event,
                    onClickNavigation = onClickNavigation,
                    navController = navController
                )
            })
        }
        composable(route = walletCreatedScreenRoute) {
            WalletCreatedRoute(onClickNavigation = { event ->
                onNavigateByEvent(
                    event = event,
                    onClickNavigation = onClickNavigation,
                    navController = navController
                )
            })
        }
        composable(route = generatePhraseScreenRoute) {
            RecoveryPhraseRoute(onClickNavigation = { event ->
                onNavigateByEvent(
                    event = event,
                    onClickNavigation = onClickNavigation,
                    navController = navController
                )
            })
        }
        composable(route = testPhraseScreenRoute) {
            TestPhraseRoute(onClickNavigation = { event ->
                onNavigateByEvent(
                    event = event,
                    onClickNavigation = onClickNavigation,
                    navController = navController
                )
            })
        }
        composable(passcodeScreenRoute) {
            PasscodeRoute(onClickNavigation = { event ->
                onNavigateByEvent(
                    event = event,
                    onClickNavigation = onClickNavigation,
                    navController = navController
                )
            })
        }
    }
}

private fun onNavigateByEvent(
    event: NavigationEvent,
    onClickNavigation: (NavigationEvent) -> Unit,
    navController: NavController
) {
    when (event) {
        is CreateWalletRouter.PhraseShowing ->
            navController.navigate(generatePhraseScreenRoute)

        is CreateWalletRouter.PhraseTesting ->
            navController.navigate(testPhraseScreenRoute)

        is CreateWalletRouter.Passcode ->
            navController.navigate(passcodeScreenRoute)

        is CreateWalletRouter.WalletCreation ->
            navController.navigate(walletCreatedScreenRoute)

        else -> onClickNavigation(event)
    }
}
