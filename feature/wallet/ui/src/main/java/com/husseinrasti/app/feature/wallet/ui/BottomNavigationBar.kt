package com.husseinrasti.app.feature.wallet.ui

import androidx.compose.material.*
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController

@Composable
fun BottomNavigationBar(bottomNavigationItems: List<BottomNavigationItem>, navController: NavController) {
    var selectedTabIndex by rememberSaveable {
        mutableStateOf(0)
    }

    NavigationBar(
        containerColor = MaterialTheme.colors.primary
    ) {
        // looping over each tab to generate the views and navigation for each item
        bottomNavigationItems.forEachIndexed { index, tabBarItem ->
            NavigationBarItem(
                selected = selectedTabIndex == index,
                onClick = {
                    selectedTabIndex = index
                    navController.navigate(tabBarItem.title)
                },
                icon = {
                    TabBarIconView(
                        isSelected = selectedTabIndex == index,
                        icon = tabBarItem.icon,
                        title = tabBarItem.title,
                        badgeAmount = tabBarItem.badgeAmount
                    )
                },
                label = {
                    Text(
                        tabBarItem.title,
                        color = if (selectedTabIndex == index) MaterialTheme.colors.secondaryVariant
                        else MaterialTheme.colors.onSurface
                    )
                })
        }
    }
}

@Composable
fun TabBarIconView(
    isSelected: Boolean,
    icon: ImageVector,
    title: String,
    badgeAmount: Int? = null
) {
    BadgedBox(badge = { TabBarBadgeView(badgeAmount) }) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = if (isSelected) MaterialTheme.colors.secondaryVariant.copy(alpha = LocalContentAlpha.current)
            else MaterialTheme.colors.onSurface.copy(alpha = LocalContentAlpha.current)
        )
    }
}

@Composable
fun TabBarBadgeView(count: Int? = null) {
    if (count != null) {
        Badge {
            Text(count.toString())
        }
    }
}
