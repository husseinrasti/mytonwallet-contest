package com.husseinrasti.app.feature.wallet.ui

import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavigationItem(
    val title: String,
    val route: String,
    val icon: ImageVector,
    val badgeAmount: Int? = null
)