package com.husseinrasti.app.feature.create.ui.start

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.flow.collectLatest
import com.husseinrasti.app.core.navigation.NavigateToImportWallet
import com.husseinrasti.app.core.navigation.NavigationEvent
import com.husseinrasti.app.component.ui.MyTonWalletButton
import com.husseinrasti.app.component.theme.MyTonWalletContestTheme
import com.husseinrasti.app.feature.create.ui.R
import com.husseinrasti.app.feature.create.ui.navigation.CreateWalletRouter


@Composable
internal fun StartRoute(
    onClickNavigation: (NavigationEvent) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: StartViewModel = hiltViewModel(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    context: Context = LocalContext.current,
) {

    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        ProgressDialog { showDialog = false }
    }

    LaunchedEffect(snackbarHostState) {
        viewModel.uiState.collectLatest { state ->
            showDialog = state is StartUiState.Loading
            when (state) {
                StartUiState.Error -> {
                    snackbarHostState.showSnackbar(
                        message = context.resources.getString(com.husseinrasti.app.component.ui.R.string.msg_error)
                    )
                }

                StartUiState.Success -> {
                    onClickNavigation(CreateWalletRouter.WalletCreation)
                }

                else -> {}
            }
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        modifier = modifier
    ) {
        StartScreen(
            onClickNavigation = onClickNavigation,
            modifier = Modifier.padding(it),
            onCreateWallet = { viewModel.generatePhrases() }
        )
    }
}

@Composable
private fun StartScreen(
    onClickNavigation: (NavigationEvent) -> Unit,
    onCreateWallet: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
    ) {
        ConstraintLayout(
            modifier = Modifier.fillMaxSize(),
        ) {
            val (appInfo) = createRefs()

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.constrainAs(appInfo) {
                    top.linkTo(parent.top)
                    end.linkTo(parent.end, margin = 16.dp)
                    start.linkTo(parent.start, margin = 16.dp)
                    bottom.linkTo(parent.bottom, margin = 16.dp)
                }
            ) {

                Image(
                    painter = painterResource(id = R.drawable.gem),
                    contentDescription = "",
                    modifier = Modifier.size(128.dp),
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    modifier = Modifier
                        .padding(
                            PaddingValues(
                                horizontal = 16.dp,
                                vertical = 8.dp
                            )
                        ),
                    text = stringResource(id = R.string.title_ton_wallet),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colors.secondary,
                    style = MaterialTheme.typography.body1.copy(fontSize = 24.sp),
                )

                Text(
                    modifier = Modifier
                        .padding(PaddingValues(horizontal = 16.dp)),
                    text = stringResource(id = R.string.desc_ton_wallet),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colors.onSurface,
                    style = MaterialTheme.typography.caption,
                )

                MyTonWalletButton(
                    text = stringResource(id = R.string.btn_create_my_wallet),
                    modifier = Modifier.padding(
                        top = 32.dp,
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 16.dp
                    ),
                    onClick = onCreateWallet
                )

                TextButton(onClick = {
                    onClickNavigation(NavigateToImportWallet)
                }) {
                    Text(
                        text = stringResource(id = R.string.btn_import_existing_wallet),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colors.secondaryVariant,
                        style = MaterialTheme.typography.button,
                    )
                }
            }

        }
    }
}


@Composable
private fun ProgressDialog(onDismissRequest: () -> Unit) {
    Dialog(
        onDismissRequest = onDismissRequest,
        DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(72.dp)
                .background(
                    color = MaterialTheme.colors.surface,
                    shape = RoundedCornerShape(8.dp)
                )
        ) {
            CircularProgressIndicator(
                color = MaterialTheme.colors.onSurface
            )
        }
    }
}

@Preview(heightDp = 800, widthDp = 480)
@Composable
private fun StartScreenPreview() {
    MyTonWalletContestTheme {
        StartScreen(
            onClickNavigation = {},
            onCreateWallet = {}
        )
    }
}