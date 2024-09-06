package com.husseinrasti.app.feature.create.ui.biometrics

import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.husseinrasti.app.component.ui.MyTonWalletButton
import com.husseinrasti.app.component.ui.MyTonWalletLottieAnimation
import com.husseinrasti.app.component.ui.MyTonWalletSurface
import com.husseinrasti.app.component.ui.MyTonWalletTopAppBar
import com.husseinrasti.app.component.navigation.NavigateUp
import com.husseinrasti.app.component.navigation.NavigationEvent
import com.husseinrasti.app.feature.create.ui.R
import com.husseinrasti.app.feature.create.ui.navigation.CreateWalletRouter
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import com.husseinrasti.app.core.security.biometric.BiometricPromptManager
import com.husseinrasti.app.core.security.biometric.findActivity

@Composable
internal fun BiometricsRoute(
    onClickNavigation: (NavigationEvent) -> Unit,
    modifier: Modifier = Modifier,
    item: CreateWalletRouter.Biometric,
) {
    BiometricsScaffoldScreen(
        onClickNavigation = onClickNavigation,
        modifier = modifier,
        onNavigateToPhraseShowing = { biometric ->
            onClickNavigation(
                CreateWalletRouter.PhraseShowing(
                    passcode = item.passcode,
                    isUse6Digits = item.isUse6Digits,
                    biometric = biometric
                )
            )
        }
    )
}

@Composable
private fun BiometricsScaffoldScreen(
    onClickNavigation: (NavigationEvent) -> Unit,
    onNavigateToPhraseShowing: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            MyTonWalletTopAppBar(
                onClickNavigation = { onClickNavigation(NavigateUp) },
                elevation = 0.dp
            )
        },
        content = {
            BiometricsScreen(
                onNavigateToPhraseShowing = onNavigateToPhraseShowing,
                modifier = modifier.padding(it)
            )
        }
    )
}

@Composable
fun BiometricsScreen(
    onNavigateToPhraseShowing: (Boolean) -> Unit,
    modifier: Modifier
) {
    MyTonWalletSurface(
        modifier = modifier,
    ) {
        val promptManager = BiometricPromptManager(LocalContext.current.findActivity())

        val biometricResult by promptManager.promptResults.collectAsState(
            initial = null
        )
        val enrollLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult(),
            onResult = {
                println("Activity result: $it")
            }
        )
        LaunchedEffect(biometricResult) {
            if (biometricResult is BiometricPromptManager.BiometricResult.AuthenticationNotSet) {
                if (Build.VERSION.SDK_INT >= 30) {
                    val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                        putExtra(
                            Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                            BIOMETRIC_STRONG or DEVICE_CREDENTIAL
                        )
                    }
                    enrollLauncher.launch(enrollIntent)
                }
            }
        }

        biometricResult?.let { result ->
            when (result) {
                is BiometricPromptManager.BiometricResult.AuthenticationError -> {
                    // result.error
                }

                BiometricPromptManager.BiometricResult.AuthenticationFailed -> {
                    "Authentication failed"
                }

                BiometricPromptManager.BiometricResult.AuthenticationNotSet -> {
                    "Authentication not set"
                }

                BiometricPromptManager.BiometricResult.AuthenticationSuccess -> {
                    onNavigateToPhraseShowing(true)
                }

                BiometricPromptManager.BiometricResult.FeatureUnavailable -> {
                    "Feature unavailable"
                }

                BiometricPromptManager.BiometricResult.HardwareUnavailable -> {
                    "Hardware unavailable"
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            MyTonWalletLottieAnimation(
                lottieCompositionSpec = LottieCompositionSpec.Asset("anim/fingerprint.json"),
                modifier = Modifier.size(128.dp),
            )
            Spacer(Modifier.height(24.dp))
            Text(
                modifier = Modifier
                    .padding(PaddingValues(horizontal = 16.dp)),
                text = stringResource(id = R.string.title_use_biometrics),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colors.secondary,
                style = MaterialTheme.typography.body1.copy(fontSize = 24.sp),
            )
            Spacer(Modifier.height(8.dp))
            Text(
                modifier = Modifier
                    .padding(PaddingValues(horizontal = 16.dp)),
                text = stringResource(R.string.desc_use_biometrics),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colors.onSurface,
                style = MaterialTheme.typography.caption,
            )
            Spacer(Modifier.height(32.dp))
            MyTonWalletButton(
                text = stringResource(id = R.string.btn_enable),
                onClick = {
                    promptManager.showBiometricPrompt(
                        title = "Enable Biometric",
                        description = ""
                    )
                }
            )
            Spacer(Modifier.height(8.dp))
            ClickableText(
                text = AnnotatedString(stringResource(R.string.btn_skip)),
                onClick = { onNavigateToPhraseShowing(false) },
                style = TextStyle(
                    color = MaterialTheme.colors.secondaryVariant,
                    fontFamily = MaterialTheme.typography.body1.fontFamily,
                    fontStyle = MaterialTheme.typography.body1.fontStyle,
                    fontSize = MaterialTheme.typography.body1.fontSize,
                    fontWeight = MaterialTheme.typography.body1.fontWeight,
                ),
                modifier = Modifier.padding(vertical = 16.dp)
            )
        }

    }
}
