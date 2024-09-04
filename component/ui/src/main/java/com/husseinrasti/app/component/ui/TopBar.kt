package com.husseinrasti.app.component.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun TonTopAppBar(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    backgroundColor: Color = MaterialTheme.colors.primarySurface,
    contentColor: Color = contentColorFor(backgroundColor),
    elevation: Dp = AppBarDefaults.TopAppBarElevation
) {
    TopAppBar(
        navigationIcon = navigationIcon,
        title = title,
        modifier = modifier,
        actions = actions,
        contentColor = contentColor,
        elevation = elevation
    )
}

@Composable
fun TonTopAppBar(
    modifier: Modifier = Modifier,
    title: String = "",
    icon: ImageVector = Icons.Default.ArrowBack,
    onClickNavigation: () -> Unit,
    actions: @Composable RowScope.() -> Unit = {},
    backgroundColor: Color = MaterialTheme.colors.primarySurface,
    contentColor: Color = contentColorFor(backgroundColor),
    elevation: Dp = AppBarDefaults.TopAppBarElevation
) {
    TonTopAppBar(
        navigationIcon = {
            IconToolbar(
                onClick = onClickNavigation,
                icon = icon
            )
        },
        title = {
            if (title.isNotEmpty()) {
                TextToolbar(title = title)
            }
        },
        modifier = modifier,
        actions = actions,
        contentColor = contentColor,
        elevation = elevation
    )
}

@Composable
fun TonTopAppBar(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colors.primarySurface,
    contentColor: Color = contentColorFor(backgroundColor),
    elevation: Dp = AppBarDefaults.TopAppBarElevation,
    contentPadding: PaddingValues = AppBarDefaults.ContentPadding,
    content: @Composable RowScope.() -> Unit,
) {
    TopAppBar(
        modifier = modifier,
        backgroundColor = backgroundColor,
        contentColor = contentColor,
        elevation = elevation,
        contentPadding = contentPadding,
        content = content
    )
}

@Composable
fun TonTopAppBar(
    modifier: Modifier = Modifier,
    title: String = "",
    icon: ImageVector = Icons.Default.ArrowBack,
    onClickBack: () -> Unit = {},
    backgroundColor: Color = MaterialTheme.colors.primarySurface,
    contentColor: Color = contentColorFor(backgroundColor),
    elevation: Dp = AppBarDefaults.TopAppBarElevation,
    contentPadding: PaddingValues = AppBarDefaults.ContentPadding,
) {
    TonTopAppBar(
        modifier = modifier,
        backgroundColor = backgroundColor,
        contentColor = contentColor,
        elevation = elevation,
        contentPadding = contentPadding,
    ) {
        IconToolbar(
            onClick = onClickBack,
            icon = icon
        )

        if (title.isNotEmpty()) {
            TextToolbar(title = title)
        }
    }
}


@Composable
private fun IconToolbar(
    onClick: () -> Unit = {},
    icon: ImageVector
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .statusBarsPadding()
            .padding(16.dp)
            .size(24.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colors.onBackground
        )
    }
}

@Composable
private fun TextToolbar(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.body2,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colors.onSurface,
    )
}