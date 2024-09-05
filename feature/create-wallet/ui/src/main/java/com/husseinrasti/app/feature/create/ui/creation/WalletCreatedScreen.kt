package com.husseinrasti.app.feature.create.ui.creation

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.husseinrasti.app.component.theme.MyTonWalletContestTheme
import com.husseinrasti.app.component.ui.MyTonWalletLottieAnimation
import com.husseinrasti.app.component.ui.MyTonWalletSurface
import com.husseinrasti.app.component.ui.MyTonWalletTopAppBar
import com.husseinrasti.app.core.navigation.NavigateUp
import com.husseinrasti.app.core.navigation.NavigationEvent
import com.husseinrasti.app.component.ui.MyTonWalletButton
import com.husseinrasti.app.feature.create.ui.R
import com.husseinrasti.app.feature.create.ui.navigation.CreateWalletRouter


@Composable
internal fun WalletCreatedRoute(
    onClickNavigation: (NavigationEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    WalletCreatedScaffoldScreen(
        onClickNavigation = onClickNavigation,
        modifier = modifier
    )
}

@Composable
private fun WalletCreatedScaffoldScreen(
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
            WalletCreatedScreen(
                onClickNavigation = onClickNavigation,
                modifier = modifier.padding(it)
            )
        }
    )
}

@Composable
private fun WalletCreatedScreen(
    onClickNavigation: (NavigationEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    MyTonWalletSurface(
        modifier = modifier,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize(),
        ) {
            MyTonWalletLottieAnimation(
                lottieCompositionSpec = LottieCompositionSpec.Asset("anim/congratulations.json"),
                modifier = Modifier.size(128.dp),
            )
            Spacer(Modifier.height(8.dp))
            Text(
                modifier = Modifier
                    .padding(PaddingValues(horizontal = 16.dp)),
                text = stringResource(id = R.string.title_wallet_created),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colors.secondary,
                style = MaterialTheme.typography.body1.copy(fontSize = 24.sp),
            )
            Spacer(Modifier.height(8.dp))
            Text(
                modifier = Modifier
                    .padding(PaddingValues(horizontal = 16.dp)),
                text = stringResource(id = R.string.desc_wallet_created),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colors.onSurface,
                style = MaterialTheme.typography.caption,
            )
            Spacer(Modifier.height(32.dp))
            MyTonWalletButton(
                text = stringResource(id = R.string.btn_setup_passcode),
                onClick = { onClickNavigation(CreateWalletRouter.Passcode) }
            )

        }
    }
}


@Preview(heightDp = 800, widthDp = 480)
@Composable
private fun CongratsScreenPreview() {
    MyTonWalletContestTheme {
        WalletCreatedScaffoldScreen(onClickNavigation = {})
    }
}