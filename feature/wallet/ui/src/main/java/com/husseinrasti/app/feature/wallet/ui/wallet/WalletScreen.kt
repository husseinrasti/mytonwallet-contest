package com.husseinrasti.app.feature.wallet.ui.wallet

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.husseinrasti.app.component.navigation.NavigationEvent
import com.husseinrasti.app.component.ui.MyTonWalletTopAppBar
import com.husseinrasti.app.component.theme.MyTonWalletContestTheme
import com.husseinrasti.app.component.ui.MyTonWalletLottieAnimation
import com.husseinrasti.app.feature.wallet.domain.entity.TokenEntity
import com.husseinrasti.app.feature.wallet.domain.entity.TransactionEntity
import com.husseinrasti.app.feature.wallet.domain.entity.WalletEntity
import com.husseinrasti.app.feature.wallet.ui.R

@Composable
internal fun WalletRoute(
    onClickNavigation: (NavigationEvent) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: WalletViewModel = hiltViewModel(),
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle(
        minActiveState = Lifecycle.State.RESUMED
    )

    when (uiState) {
        is WalletUiState.Success -> {
            WalletScaffoldScreen(
                onClickNavigation = onClickNavigation,
                modifier = modifier,
                wallet = (uiState as WalletUiState.Success).wallet,
            )
        }

        WalletUiState.Loading -> {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colors.secondaryVariant
                )
            }
        }

        WalletUiState.Error -> {}
    }
}

@Composable
private fun WalletScaffoldScreen(
    onClickNavigation: (NavigationEvent) -> Unit,
    modifier: Modifier = Modifier,
    wallet: WalletEntity,
) {
    Scaffold(
        topBar = {
            MyTonWalletTopAppBar(
                elevation = 0.dp,
                icon = Icons.Default.Menu
            )
        },
        content = {
            WalletScreen(
                onClickNavigation = onClickNavigation,
                modifier = modifier.padding(it),
                wallet = wallet
            )
        }
    )
}

@Composable
private fun WalletScreen(
    wallet: WalletEntity,
    onClickNavigation: (NavigationEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.primary),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        item {
            Header(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                title = wallet.name,
                symbol = wallet.symbol,
                balance = wallet.balance
            )
        }
        if (wallet.tokens.isEmpty() && wallet.transactions.isEmpty()) {
            item {
                Divider()
                MyTonWalletLottieAnimation(
                    lottieCompositionSpec = LottieCompositionSpec.Asset("anim/created.json"),
                    modifier = Modifier.size(128.dp),
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(id = R.string.msg_no_transaction),
                    style = MaterialTheme.typography.body1,
                    color = MaterialTheme.colors.secondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
            }
        } else {
            wallet.tokens.takeIf { it.isNotEmpty() }?.let { tokens ->
                item {
                    Divider()
                }
                items(tokens.size) {
                    TokenItem(item = tokens[it])
                }
            }
            wallet.transactions.takeIf { it.isNotEmpty() }?.let { trx ->
                item {
                    Divider()
                }
                items(trx.size) {
                    TransactionItem(item = trx[it])
                }
            }
        }
    }
}

@Composable
private fun TransactionItem(item: TransactionEntity) {
    Text(text = item.amount)
}

@Composable
private fun TokenItem(item: TokenEntity) {
    Text(text = item.amount)
}

@Composable
private fun Divider() {
    Spacer(
        modifier = Modifier
            .height(8.dp)
            .fillMaxWidth()
            .background(Color.LightGray)
    )
}

@Composable
private fun Header(
    modifier: Modifier = Modifier,
    title: String,
    symbol: String,
    balance: String,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.h4,
            color = MaterialTheme.colors.primaryVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Text(
            text = "$symbol$balance",
            style = MaterialTheme.typography.h4,
            color = MaterialTheme.colors.onSurface,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ItemActionButton(
                onClick = {},
                title = stringResource(id = R.string.title_add),
                icon = painterResource(id = R.drawable.ic_add_32)
            )
            ItemActionButton(
                onClick = {},
                title = stringResource(id = R.string.title_send),
                icon = painterResource(id = R.drawable.ic_send_32)
            )
            ItemActionButton(
                onClick = {},
                title = stringResource(id = R.string.title_earn),
                icon = painterResource(id = R.drawable.ic_earn_32)
            )
            ItemActionButton(
                onClick = {},
                title = stringResource(id = R.string.title_swap),
                icon = painterResource(id = R.drawable.ic_swap_32)
            )
        }
    }
}

@Composable
fun ItemActionButton(onClick: () -> Unit, title: String, icon: Painter) {
    IconButton(onClick = onClick) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = icon,
                contentDescription = title,
                tint = MaterialTheme.colors.secondaryVariant
            )
            Text(
                text = title,
                style = MaterialTheme.typography.body1,
                color = MaterialTheme.colors.secondaryVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }
    }
}

@Preview
@Composable
private fun WalletPreview() {
    MyTonWalletContestTheme {
        WalletScaffoldScreen(
            wallet = WalletEntity(
                name = "MyWallet",
                balance = "10000",
                symbol = "$",
                tokens = listOf(),
                transactions = listOf(),
            ),
            onClickNavigation = {},
        )
    }
}
