package com.husseinrasti.app.feature.auth.ui

import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.biometric.BiometricManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.husseinrasti.app.component.navigation.NavigateToMain
import com.husseinrasti.app.component.theme.MyTonWalletContestTheme
import com.husseinrasti.app.component.navigation.NavigationEvent
import com.husseinrasti.app.component.ui.ColorBox
import com.husseinrasti.app.component.ui.MyTonWalletSurface
import com.husseinrasti.app.component.ui.PasscodeInput
import com.husseinrasti.app.component.ui.number_keyboard.NumberKeyboard
import com.husseinrasti.app.component.ui.number_keyboard.NumberKeyboardAuxButton
import com.husseinrasti.app.component.ui.number_keyboard.NumberKeyboardButton
import com.husseinrasti.app.component.ui.number_keyboard.NumberKeyboardData
import com.husseinrasti.app.component.ui.number_keyboard.NumberKeyboardListener
import com.husseinrasti.app.core.security.biometric.BiometricPromptManager
import com.husseinrasti.app.core.security.biometric.findActivity


@Composable
internal fun AuthScreenRoute(
    onClickNavigation: (NavigationEvent) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle(initialValue = null)
    val isUse6Digits by viewModel.stateNumDigits.collectAsStateWithLifecycle(
        initialValue = null
    )

    when (state) {
        AuthState.Error -> {}
        AuthState.Loading -> {}
        AuthState.NavigateToMain -> onClickNavigation(NavigateToMain)
        null -> {}
    }

    AuthScaffoldScreen(
        onChangeRoute = { biometric, passcode ->
            viewModel.check(biometric = biometric, passcode = passcode)
        },
        modifier = modifier,
        isUse6Digits = isUse6Digits ?: false
    )
}

@Composable
private fun AuthScaffoldScreen(
    onChangeRoute: (Boolean?, String?) -> Unit,
    modifier: Modifier = Modifier,
    isUse6Digits: Boolean,
) {
    Scaffold(
        topBar = {},
        content = {
            AuthScreen(
                onChangeRoute = onChangeRoute,
                modifier = modifier.padding(it),
                isUse6Digits = isUse6Digits
            )
        }
    )
}

@Composable
private fun AuthScreen(
    onChangeRoute: (Boolean?, String?) -> Unit,
    modifier: Modifier = Modifier,
    isUse6Digits: Boolean,
) {
    var passcode by remember { mutableStateOf("") }

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
                        BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL
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
                //todo save biometric
                onChangeRoute(true, null)
            }

            BiometricPromptManager.BiometricResult.FeatureUnavailable -> {
                "Feature unavailable"
            }

            BiometricPromptManager.BiometricResult.HardwareUnavailable -> {
                "Hardware unavailable"
            }
        }
    }

    MyTonWalletSurface(
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.secondaryVariant)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
            ) {
                Image(
                    modifier = Modifier.size(48.dp),
                    colorFilter = ColorFilter.tint(MaterialTheme.colors.primary),
                    imageVector = Icons.Filled.Lock,
                    contentDescription = ""
                )
                Spacer(Modifier.height(24.dp))
                PasscodeInput(
                    passcode = passcode,
                    passcodeCount = if (isUse6Digits) 6 else 4,
                    onPasscodeTextChange = { code, isFill ->
                        passcode = code
                        if (isFill) {
                            onChangeRoute(null, passcode)
                        }
                    },
                    onFocusChanged = {},
                    colorBox = ColorBox(
                        fill = MaterialTheme.colors.primary,
                        empty = MaterialTheme.colors.secondaryVariant,
                        border = MaterialTheme.colors.primary
                    )
                )
                Spacer(Modifier.height(24.dp))
                Text(
                    modifier = Modifier
                        .padding(PaddingValues(horizontal = 16.dp)),
                    text = stringResource(id = R.string.title_unlock_wallet),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colors.primary,
                    style = MaterialTheme.typography.body1,
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    modifier = Modifier
                        .padding(PaddingValues(horizontal = 16.dp)),
                    text = stringResource(id = R.string.desc_unlock_wallet),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colors.primary,
                    style = MaterialTheme.typography.body1,
                )

                val buttonModifier = Modifier
                    .weight(1F)
                    .aspectRatio(1F)
                val buttonTextStyle = MaterialTheme.typography.button.copy(
                    color = MaterialTheme.colors.primary,
                    fontSize = 24.sp
                )
                Column(
                    modifier = Modifier
                        .padding(48.dp)
                ) {
                    NumberKeyboard(
                        maxAllowedAmount = if (isUse6Digits) 999_999.00 else 9_999.00,
                        maxAllowedDecimals = 0,
                        currencySymbol = "",
                        verticalArrangement = Arrangement.spacedBy(24.dp),
                        horizontalArrangement = Arrangement.spacedBy(24.dp),
                        button = { number, clickedListener ->
                            NumberKeyboardButton(
                                modifier = buttonModifier,
                                textStyle = buttonTextStyle,
                                number = number,
                                shape = CircleShape,
                                listener = clickedListener
                            )
                        },
                        leftAuxButton = { _ ->
                            NumberKeyboardAuxButton(
                                modifier = buttonModifier,
                                textStyle = buttonTextStyle,
                                imageVector = ImageVector.vectorResource(id = R.drawable.baseline_fingerprint_24),
                                tintImage = MaterialTheme.colors.primary,
                                shape = CircleShape,
                                clicked = {
                                    promptManager.showBiometricPrompt(
                                        title = "Login by Fingerprint",
                                        description = ""
                                    )
                                }
                            )
                        },
                        rightAuxButton = { clickedListener ->
                            NumberKeyboardAuxButton(
                                modifier = buttonModifier,
                                textStyle = buttonTextStyle,
                                imageVector = ImageVector.vectorResource(id = R.drawable.baseline_backspace_24),
                                tintImage = MaterialTheme.colors.primary,
                                shape = CircleShape,
                                clicked = {
                                    clickedListener.onRightAuxButtonClicked()
                                }
                            )
                        },
                        listener = object : NumberKeyboardListener {
                            override fun onUpdated(data: NumberKeyboardData) {
                                passcode = data.int.toString()
                            }
                        }
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}


@Preview
@Composable
private fun StartScreenPreview() {
    MyTonWalletContestTheme {
        AuthScaffoldScreen(onChangeRoute = { _, _ -> }, isUse6Digits = true)
    }
}