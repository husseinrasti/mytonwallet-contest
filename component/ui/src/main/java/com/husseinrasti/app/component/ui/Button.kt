package com.husseinrasti.app.component.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.height
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.State
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.husseinrasti.app.component.theme.MyTonWalletContestTheme


@Composable
fun TonButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    enabled: Boolean = true,
    border: BorderStroke? = null,
    shape: Shape = MaterialTheme.shapes.medium,
    colors: ButtonColors = TonButtonColors(
        backgroundColor = MaterialTheme.colors.secondaryVariant,
        contentColor = MaterialTheme.colors.primary,
        disabledBackgroundColor = MaterialTheme.colors.secondary
            .copy(alpha = ContentAlpha.disabled),
        disabledContentColor = MaterialTheme.colors.onSecondary
            .copy(alpha = ContentAlpha.disabled)
    ),
    contentPadding: PaddingValues = PaddingValues(
        horizontal = 64.dp,
        vertical = 8.dp
    ),
    content: @Composable RowScope.() -> Unit,
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        enabled = enabled,
        border = border,
        colors = colors,
        shape = shape,
        contentPadding = contentPadding,
        content = content
    )
}

@Composable
fun TonButton(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    enabled: Boolean = true,
    border: BorderStroke? = null,
    shape: Shape = MaterialTheme.shapes.medium,
    colors: ButtonColors = TonButtonColors(
        backgroundColor = MaterialTheme.colors.secondaryVariant,
        contentColor = MaterialTheme.colors.primary,
        disabledBackgroundColor = MaterialTheme.colors.secondary
            .copy(alpha = ContentAlpha.disabled),
        disabledContentColor = MaterialTheme.colors.onSecondary
            .copy(alpha = ContentAlpha.disabled)
    ),
    contentPadding: PaddingValues = PaddingValues(
        horizontal = 64.dp,
        vertical = 8.dp
    ),
) {
    TonButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        border = border,
        colors = colors,
        shape = shape,
        contentPadding = contentPadding,
    ) {
        Text(
            text = text,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.button,
        )
    }
}

@Preview
@Composable
fun TonButtonPreview() {
    MyTonWalletContestTheme {
        TonButton(text = "My Ton Wallet", onClick = {})
    }
}

@Preview
@Composable
fun TonButtonPreviewDisable() {
    MyTonWalletContestTheme {
        TonButton(
            text = "My Ton Wallet", onClick = {},
            enabled = false
        )
    }
}

@Immutable
private class TonButtonColors(
    private val backgroundColor: Color,
    private val contentColor: Color,
    private val disabledBackgroundColor: Color,
    private val disabledContentColor: Color
) : ButtonColors {
    @Composable
    override fun backgroundColor(enabled: Boolean): State<Color> {
        return rememberUpdatedState(if (enabled) backgroundColor else disabledBackgroundColor)
    }

    @Composable
    override fun contentColor(enabled: Boolean): State<Color> {
        return rememberUpdatedState(if (enabled) contentColor else disabledContentColor)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as TonButtonColors

        if (backgroundColor != other.backgroundColor) return false
        if (contentColor != other.contentColor) return false
        if (disabledBackgroundColor != other.disabledBackgroundColor) return false
        if (disabledContentColor != other.disabledContentColor) return false

        return true
    }

    override fun hashCode(): Int {
        var result = backgroundColor.hashCode()
        result = 31 * result + contentColor.hashCode()
        result = 31 * result + disabledBackgroundColor.hashCode()
        result = 31 * result + disabledContentColor.hashCode()
        return result
    }
}
