package com.husseinrasti.app.feature.create.ui.biometrics

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
import com.husseinrasti.app.core.navigation.NavigateUp
import com.husseinrasti.app.core.navigation.NavigationEvent
import com.husseinrasti.app.feature.create.ui.R
import com.husseinrasti.app.feature.create.ui.navigation.CreateWalletRouter


@Composable
internal fun BiometricsRoute(
    onClickNavigation: (NavigationEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    BiometricsScaffoldScreen(
        onClickNavigation = onClickNavigation,
        modifier = modifier
    )
}

@Composable
private fun BiometricsScaffoldScreen(
    onClickNavigation: (NavigationEvent) -> Unit,
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
                onClickNavigation = onClickNavigation,
                modifier = modifier.padding(it)
            )
        }
    )
}

@Composable
fun BiometricsScreen(onClickNavigation: (NavigationEvent) -> Unit, modifier: Modifier) {
    MyTonWalletSurface(
        modifier = modifier,
    ) {
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

                }
            )
            Spacer(Modifier.height(8.dp))
            ClickableText(
                text = AnnotatedString(stringResource(R.string.btn_skip)),
                onClick = { onClickNavigation(CreateWalletRouter.PhraseShowing) },
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
