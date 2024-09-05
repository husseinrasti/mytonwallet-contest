package com.husseinrasti.app.feature.wallet.ui.wallet

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.*
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.husseinrasti.app.component.navigation.NavigateUp
import com.husseinrasti.app.component.navigation.NavigationEvent
import com.husseinrasti.app.component.ui.MyTonWalletLottieAnimation
import com.husseinrasti.app.component.ui.MyTonWalletTopAppBar
import com.husseinrasti.app.component.theme.MyTonWalletContestTheme


private const val DEFAULT_COLUMN_COUNT = 2
private const val TITLE_FONT_SCALE_STARE = 1f
private const val TITLE_FONT_SCALE_END = 0.5f

private val headerHeight = 250.dp
private val toolbarHeight = 56.dp

private val paddingMedium = 16.dp

private val titlePaddingStart = 0.dp
private val titlePaddingEnd = 72.dp


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
                title = (uiState as WalletUiState.Success).title,
            )
        }

        WalletUiState.Loading -> {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                CircularProgressIndicator()
            }
        }

        WalletUiState.Error -> {}
    }
}

@Composable
private fun WalletScaffoldScreen(
    onClickNavigation: (NavigationEvent) -> Unit,
    modifier: Modifier = Modifier,
    title: String,
) {
    Scaffold(
        topBar = {
            MyTonWalletTopAppBar(
                onClickNavigation = { onClickNavigation(NavigateUp) },
                elevation = 0.dp
            )
        },
        content = {
            WalletScreen(
                onClickNavigation = onClickNavigation,
                modifier = modifier.padding(it),
                title = title
            )
        },
        bottomBar = {

        }
    )
}

@Composable
private fun WalletScreen(
    title: String,
    onClickNavigation: (NavigationEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scroll: ScrollState = rememberScrollState(0)

    val headerHeightPx = with(LocalDensity.current) { headerHeight.toPx() }

    Box(modifier = modifier.fillMaxSize()) {
        Header(
            scroll = scroll,
            headerHeightPx = headerHeightPx,
            modifier = Modifier
                .fillMaxWidth()
                .height(headerHeight)
        )
        Body(
            scroll = scroll,
            onClick = { },
        )
        MyTonWalletTopAppBar(
            onClickNavigation = { onClickNavigation(NavigateUp) },
            elevation = 0.dp
        )
        Title(
            scroll = scroll,
            title = title
        )
    }
}

@Composable
private fun Header(
    scroll: ScrollState,
    headerHeightPx: Float,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .graphicsLayer {
                translationY = -scroll.value.toFloat() / 1.2f // Parallax effect
                alpha = (-1f / headerHeightPx) * scroll.value + 1
            },
        contentAlignment = Alignment.Center,
    ) {
        MyTonWalletLottieAnimation(
            lottieCompositionSpec = LottieCompositionSpec.Asset("anim/recovery_phrase.json"),
            modifier = Modifier.size(128.dp)
        )
    }
}

@Composable
private fun Body(
    scroll: ScrollState,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(scroll)
    ) {
        Spacer(Modifier.height(headerHeight))
//        Text(
//            text = stringResource(R.string.desc_your_recovery_phrase),
//            style = MaterialTheme.typography.body2,
//            color = MaterialTheme.colors.onSurface,
//            textAlign = TextAlign.Center,
//            modifier = Modifier.padding(horizontal = 16.dp)
//        )
        Spacer(Modifier.height(32.dp))

//        GridPhrases(phrases = phrases)

        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun GridPhrases(
    phrases: List<String>,
    modifier: Modifier = Modifier,
    columnCount: Int = DEFAULT_COLUMN_COUNT,
) {
    val size = phrases.size
    for (rowIndex in phrases.indices) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            for (columnIndex in 0 until columnCount) {
                val itemIndex = rowIndex * columnCount + columnIndex
                if (itemIndex < size) {
                    Box(
                        modifier = Modifier.weight(1F, fill = true),
                        propagateMinConstraints = true,
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(8.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = "${itemIndex + 1}. ",
                                style = MaterialTheme.typography.body2,
                                color = MaterialTheme.colors.primaryVariant,
                                textAlign = TextAlign.End,
                                modifier = Modifier.weight(0.2F, fill = true),
                            )
                            Text(
                                text = phrases[itemIndex],
                                style = MaterialTheme.typography.body1,
                                color = MaterialTheme.colors.secondary,
                                modifier = Modifier
                                    .weight(1F, fill = true)
                                    .padding(horizontal = 8.dp),
                                textAlign = TextAlign.Start,
                            )
                        }
                    }
                } else {
                    Spacer(Modifier.weight(1F, fill = true))
                }
            }
        }
    }
}

@Composable
private fun Title(
    title: String,
    scroll: ScrollState,
    modifier: Modifier = Modifier
) {
    var titleHeightPx by remember { mutableStateOf(0f) }
    var titleWidthPx by remember { mutableStateOf(0f) }

    Text(
        text = title,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colors.secondary,
        style = MaterialTheme.typography.body1.copy(fontSize = 24.sp),
        modifier = modifier
            .fillMaxWidth()
            .padding(end = 32.dp)
            .graphicsLayer {
                val collapseRange: Float = (headerHeight.toPx() - toolbarHeight.toPx())
                val collapseFraction: Float = (scroll.value / collapseRange).coerceIn(0f, 1f)

                val scaleXY = lerp(
                    TITLE_FONT_SCALE_STARE.dp,
                    TITLE_FONT_SCALE_END.dp,
                    collapseFraction
                )

                val titleExtraStartPadding = titleWidthPx.toDp() * (1 - scaleXY.value) / 2f

                val titleYFirstInterpolatedPoint = lerp(
                    headerHeight - titleHeightPx.toDp() - paddingMedium,
                    headerHeight / 2,
                    collapseFraction
                )

                val titleXFirstInterpolatedPoint = lerp(
                    titlePaddingStart,
                    (titlePaddingEnd - titleExtraStartPadding) * 5 / 4,
                    collapseFraction
                )

                val titleYSecondInterpolatedPoint = lerp(
                    headerHeight / 2,
                    toolbarHeight / 2 - titleHeightPx.toDp() / 2,
                    collapseFraction
                )

                val titleXSecondInterpolatedPoint = lerp(
                    (titlePaddingEnd - titleExtraStartPadding) * 5 / 4,
                    titlePaddingEnd - titleExtraStartPadding,
                    collapseFraction
                )

                val titleY = lerp(
                    titleYFirstInterpolatedPoint,
                    titleYSecondInterpolatedPoint,
                    collapseFraction
                )

                val titleX = lerp(
                    titleXFirstInterpolatedPoint,
                    titleXSecondInterpolatedPoint,
                    collapseFraction
                )

                translationY = titleY.toPx()
                translationX = titleX.toPx()
            }
            .onGloballyPositioned {
                titleHeightPx = it.size.height.toFloat()
                titleWidthPx = it.size.width.toFloat()
            }
    )
}

@Preview
@Composable
private fun WalletPreview() {
    MyTonWalletContestTheme {
        WalletScreen(
            title = "Title",
            onClickNavigation = {},
        )
    }
}
