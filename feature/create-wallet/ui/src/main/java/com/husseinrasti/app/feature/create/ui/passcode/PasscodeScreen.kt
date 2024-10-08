package com.husseinrasti.app.feature.create.ui.passcode

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.husseinrasti.app.component.navigation.NavigateUp
import com.husseinrasti.app.component.navigation.NavigationEvent
import com.husseinrasti.app.component.ui.MyTonWalletLottieAnimation
import com.husseinrasti.app.component.ui.MyTonWalletSurface
import com.husseinrasti.app.component.ui.MyTonWalletTopAppBar
import com.husseinrasti.app.component.ui.PasscodeInput
import com.husseinrasti.app.feature.create.ui.R
import com.husseinrasti.app.feature.create.ui.navigation.CreateWalletRouter
import kotlinx.coroutines.delay


@Composable
internal fun PasscodeRoute(
    onClickNavigation: (NavigationEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    PasscodeScaffoldScreen(
        onClickNavigation = onClickNavigation,
        modifier = modifier,
        onNavigateToBiometric = { is6digits, passcode ->
            onClickNavigation(
                CreateWalletRouter.Biometric(
                    passcode = passcode,
                    isUse6Digits = is6digits
                )
            )
        }
    )
}

@Composable
private fun PasscodeScaffoldScreen(
    onClickNavigation: (NavigationEvent) -> Unit,
    onNavigateToBiometric: (Boolean, String) -> Unit,
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
            PasscodeScreen(
                onNavigateToBiometric = onNavigateToBiometric,
                modifier = modifier.padding(it)
            )
        }
    )
}

@Composable
fun PasscodeScreen(
    onNavigateToBiometric: (Boolean, String) -> Unit,
    modifier: Modifier
) {
    MyTonWalletSurface(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            var isUse6Digits by remember { mutableStateOf(false) }
            var passcode by remember { mutableStateOf("") }

            val focusRequester = remember { FocusRequester() }
            val keyboard = LocalSoftwareKeyboardController.current

            LaunchedEffect(focusRequester) {
                focusRequester.requestFocus()
                delay(100)
                keyboard?.show()
            }

            MyTonWalletLottieAnimation(
                lottieCompositionSpec = LottieCompositionSpec.Asset("anim/password.json"),
                modifier = Modifier.size(128.dp),
            )
            Spacer(Modifier.height(8.dp))
            Text(
                modifier = Modifier
                    .padding(PaddingValues(horizontal = 16.dp)),
                text = stringResource(id = R.string.title_set_passcode),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colors.secondary,
                style = MaterialTheme.typography.body1.copy(fontSize = 24.sp),
            )
            Spacer(Modifier.height(8.dp))
            Text(
                modifier = Modifier
                    .padding(PaddingValues(horizontal = 16.dp)),
                text = stringResource(
                    id = if (isUse6Digits) R.string.desc_set_passcode_6_digits
                    else R.string.desc_set_passcode_4_digits
                ),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colors.onSurface,
                style = MaterialTheme.typography.caption,
            )
            Spacer(Modifier.height(32.dp))
            PasscodeInput(
                passcode = passcode,
                passcodeCount = if (isUse6Digits) 6 else 4,
                onPasscodeTextChange = { code, isFill ->
                    passcode = code
                    if (isFill) {
                        onNavigateToBiometric.invoke(
                            isUse6Digits,
                            passcode
                        )
                    }
                },
                focusRequester = focusRequester
            )
            ClickableText(
                modifier = Modifier.padding(
                    horizontal = 24.dp,
                    vertical = 32.dp
                ),
                text = AnnotatedString(
                    stringResource(
                        id =
                        if (isUse6Digits) R.string.btn_use_4_digits
                        else R.string.btn_use_6_digits
                    )
                ),
                onClick = { isUse6Digits = isUse6Digits.not() },
                style = TextStyle(
                    color = MaterialTheme.colors.secondaryVariant,
                    fontFamily = MaterialTheme.typography.body1.fontFamily,
                    fontStyle = MaterialTheme.typography.body1.fontStyle,
                    fontSize = MaterialTheme.typography.body1.fontSize,
                    fontWeight = MaterialTheme.typography.body1.fontWeight,
                )
            )
        }
    }
}