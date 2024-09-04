package com.husseinrasti.app.component.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ColorPalette = lightColors(
    primary = Color.White,
    onPrimary = Color.Black,
    primaryVariant = Color.Gray,
    secondary = Color(0xFF222222),
    onSecondary = Color.White,
    secondaryVariant = Color(0xFF339CEC),
    background = Color.White,
    onBackground = Color.Black,
    surface = Color.White,
    onSurface = Color.Black,
    error = Color(0xFFFE3C30),
    onError = Color.White,
)

@Composable
fun MyTonWalletContestTheme(content: @Composable () -> Unit) {
//    val colorScheme = when {
//        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
//            val context = LocalContext.current
//            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
//        }
//
//        darkTheme -> DarkColorScheme
//        else -> LightColorScheme
//    }

    MaterialTheme(
        colors = ColorPalette,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}