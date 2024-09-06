package com.husseinrasti.app.feature.create.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.husseinrasti.app.component.navigation.NavigateUp
import com.husseinrasti.app.component.navigation.NavigationEvent
import com.husseinrasti.app.feature.create.ui.biometrics.BiometricsRoute
import com.husseinrasti.app.feature.create.ui.creation.WalletCreatedRoute
import com.husseinrasti.app.feature.create.ui.passcode.PasscodeRoute
import com.husseinrasti.app.feature.create.ui.phrase.phrase.ShowPhraseRoute
import com.husseinrasti.app.feature.create.ui.phrase.recovery.RecoveryPhraseRoute
import com.husseinrasti.app.feature.create.ui.phrase.test.TestPhraseRoute
import com.husseinrasti.app.feature.create.ui.start.StartRoute

const val createWalletGraph = "create_wallet_graph"
private const val startScreenRoute = "start_screen_route"
private const val walletCreatedScreenRoute = "wallet_created_route"
private const val showPhraseScreenRoute = "show_phrase_screen_route"
private const val testPhraseScreenRoute = "test_phrase_screen_route"
private const val recoveryPhraseScreenRoute = "recovery_phrase_screen_route"
private const val passcodeScreenRoute = "passcode_screen_route"
private const val biometricScreenRoute = "biometric_screen_route"

fun NavController.navigateToCreateWallet(navOptions: NavOptions? = null) {
    this.navigate(CreateWalletRouter.Start, navOptions)
}

@Composable
fun CreateWalletGraph(
    navController: NavHostController,
    onClickNavigation: (NavigationEvent) -> Unit
) {
    NavHost(
        modifier = Modifier,
        navController = navController,
        startDestination = CreateWalletRouter.Start,
    ) {
        composable<CreateWalletRouter.Start> {
            StartRoute(onClickNavigation = { event ->
                onNavigateByEvent(
                    event = event,
                    onClickNavigation = onClickNavigation,
                    navController = navController
                )
            })
        }
        composable<CreateWalletRouter.WalletCreation> {
            WalletCreatedRoute(onClickNavigation = { event ->
                onNavigateByEvent(
                    event = event,
                    onClickNavigation = onClickNavigation,
                    navController = navController
                )
            })
        }
        composable<CreateWalletRouter.PhraseShowing> {
            val item = it.toRoute<CreateWalletRouter.PhraseShowing>()
            ShowPhraseRoute(
                item = item,
                onClickNavigation = { event ->
                    onNavigateByEvent(
                        event = event,
                        onClickNavigation = onClickNavigation,
                        navController = navController
                    )
                })
        }
        composable<CreateWalletRouter.PhraseTesting> {
            val item = it.toRoute<CreateWalletRouter.PhraseTesting>()
            TestPhraseRoute(
                item = item,
                onClickNavigation = { event ->
                    onNavigateByEvent(
                        event = event,
                        onClickNavigation = onClickNavigation,
                        navController = navController
                    )
                })
        }
        composable<CreateWalletRouter.PhraseRecovery> {
            RecoveryPhraseRoute(onClickNavigation = { event ->
                onNavigateByEvent(
                    event = event,
                    onClickNavigation = onClickNavigation,
                    navController = navController
                )
            })
        }
        composable<CreateWalletRouter.Passcode> {
            PasscodeRoute(onClickNavigation = { event ->
                onNavigateByEvent(
                    event = event,
                    onClickNavigation = onClickNavigation,
                    navController = navController
                )
            })
        }
        composable<CreateWalletRouter.Biometric> {
            val item = it.toRoute<CreateWalletRouter.Biometric>()
            BiometricsRoute(
                item = item,
                onClickNavigation = { event ->
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
            navController.navigate(event)

        is CreateWalletRouter.PhraseTesting ->
            navController.navigate(event)

        is CreateWalletRouter.PhraseRecovery ->
            navController.navigate(event)

        is CreateWalletRouter.Passcode ->
            navController.navigate(event)

        is CreateWalletRouter.Biometric ->
            navController.navigate(event)

        is CreateWalletRouter.WalletCreation ->
            navController.navigate(event)

        NavigateUp -> navController.navigateUp()

        else -> onClickNavigation(event)
    }
}
