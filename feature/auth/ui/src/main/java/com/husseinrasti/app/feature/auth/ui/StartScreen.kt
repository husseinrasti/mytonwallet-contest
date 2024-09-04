package com.husseinrasti.app.feature.auth.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.husseinrasti.app.component.theme.MyTonWalletContestTheme
import com.husseinrasti.app.core.navigation.NavigateUp
import com.husseinrasti.app.core.navigation.NavigationEvent
import com.husseinrasti.app.component.ui.TonButton
import com.husseinrasti.app.component.ui.TonLottieAnimation
import com.husseinrasti.app.component.ui.TonSurface
import com.husseinrasti.app.component.ui.TonTopAppBar
import com.husseinrasti.app.feature.auth.ui.navigation.AuthRouter


@Composable
internal fun StartRoute(
    onClickNavigation: (NavigationEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    StartScaffoldScreen(
        onClickNavigation = onClickNavigation,
        modifier = modifier
    )
}

@Composable
private fun StartScaffoldScreen(
    onClickNavigation: (NavigationEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            TonTopAppBar(
                onClickNavigation = { onClickNavigation(NavigateUp) },
                elevation = 0.dp
            )
        },
        content = {
            StartScreen(
                onClickNavigation = onClickNavigation,
                modifier = modifier.padding(it)
            )
        }
    )
}

@Composable
private fun StartScreen(
    onClickNavigation: (NavigationEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    var isChecked by remember { mutableStateOf(false) }

    TonSurface(
        modifier = modifier,
    ) {
        ConstraintLayout(
            modifier = Modifier.fillMaxSize(),
        ) {
            val (container, button, checkbox) = createRefs()

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.constrainAs(container) {
                    top.linkTo(parent.top, margin = 32.dp)
                    end.linkTo(parent.end, margin = 16.dp)
                    start.linkTo(parent.start, margin = 16.dp)
                    bottom.linkTo(button.top, margin = 16.dp)
                }
            ) {
                TonLottieAnimation(
                    lottieCompositionSpec = LottieCompositionSpec.Asset("anim/success.json"),
                    modifier = Modifier.size(128.dp),
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    modifier = Modifier
                        .padding(PaddingValues(horizontal = 16.dp)),
                    text = stringResource(id = R.string.title_prefect),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colors.secondary,
                    style = MaterialTheme.typography.body1.copy(fontSize = 24.sp),
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    modifier = Modifier
                        .padding(PaddingValues(horizontal = 16.dp)),
                    text = stringResource(id = R.string.msg_set_passcode),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colors.onSurface,
                    style = MaterialTheme.typography.caption,
                )
            }

            Row(
                modifier = Modifier
                    .constrainAs(checkbox) {
                        end.linkTo(parent.end, margin = 16.dp)
                        start.linkTo(parent.start, margin = 16.dp)
                        bottom.linkTo(button.top, margin = 32.dp)
                    }
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {

                Checkbox(
                    checked = isChecked,
                    onCheckedChange = {
                        isChecked = it
                    },
                )

                Text(
                    text = stringResource(id = R.string.chk_enable_biometric),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colors.secondary,
                    style = MaterialTheme.typography.body2,
                )
            }

            TonButton(
                text = stringResource(id = R.string.btn_set_passcode),
                modifier = Modifier.constrainAs(button) {
                    top.linkTo(container.bottom, margin = 32.dp)
                    end.linkTo(parent.end, margin = 16.dp)
                    start.linkTo(parent.start, margin = 16.dp)
                    bottom.linkTo(parent.bottom, margin = 32.dp)
                },
                onClick = { onClickNavigation(AuthRouter.Passcode) }
            )

        }
    }
}


@Preview
@Composable
private fun StartScreenPreview() {
    MyTonWalletContestTheme {
        StartScaffoldScreen(onClickNavigation = {})
    }
}