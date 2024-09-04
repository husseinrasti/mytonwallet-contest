package com.husseinrasti.app.component.ui

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.*
import com.husseinrasti.app.component.theme.MyTonWalletContestTheme

@Composable
fun TonLottieAnimation(
    lottieCompositionSpec: LottieCompositionSpec,
    modifier: Modifier = Modifier,
    isPlaying: Boolean = true,
    restartOnPlay: Boolean = false,
    reverseOnRepeat: Boolean = false,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    iterations: Int = 1,
) {
    val composition by rememberLottieComposition(lottieCompositionSpec)
    val progress by animateLottieCompositionAsState(
        composition = composition,
        isPlaying = isPlaying,
        restartOnPlay = restartOnPlay,
        reverseOnRepeat = reverseOnRepeat,
        iterations = iterations,
    )
    LottieAnimation(
        modifier = modifier,
        composition = composition,
        progress = { progress },
        alignment = alignment,
        contentScale = contentScale
    )
}


@Preview
@Composable
fun TonLottieAnimationPreview() {
    MyTonWalletContestTheme {
        TonLottieAnimation(
            lottieCompositionSpec = LottieCompositionSpec.Asset("anim/start.json"),
            modifier = Modifier.size(256.dp)
        )
    }
}