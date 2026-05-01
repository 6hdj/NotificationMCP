package com.notificationmcp.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.notificationmcp.ui.theme.glassNavBar

@Composable
fun MainScreen() {
    var currentTab by remember { mutableIntStateOf(0) }
    val isDark = isSystemInDarkTheme()

    val tabs = listOf(
        TabItem("通知", Icons.Filled.Notifications, Icons.Outlined.Notifications),
        TabItem("历史", Icons.Filled.History, Icons.Outlined.History),
        TabItem("自动化", Icons.Filled.AutoAwesome, Icons.Outlined.AutoAwesome),
        TabItem("MCP", Icons.Filled.Dns, Icons.Outlined.Dns)
    )

    Scaffold(
        bottomBar = {
            NavigationBar(
                modifier = Modifier.glassNavBar(isDark),
                containerColor = androidx.compose.ui.graphics.Color.Transparent,
                tonalElevation = 0.dp
            ) {
                tabs.forEachIndexed { index, tab ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                if (currentTab == index) tab.selectedIcon else tab.unselectedIcon,
                                contentDescription = tab.title
                            )
                        },
                        label = { Text(tab.title, fontSize = 11.sp) },
                        selected = currentTab == index,
                        onClick = { currentTab = index },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            AnimatedContent(
                targetState = currentTab,
                transitionSpec = {
                    fadeIn(tween(200)) + slideInVertically(tween(200)) { it / 8 } togetherWith
                            fadeOut(tween(150)) + slideOutVertically(tween(150)) { -it / 8 }
                },
                label = "tabTransition"
            ) { tab ->
                when (tab) {
                    0 -> DashboardScreen()
                    1 -> HistoryScreen()
                    2 -> AutomationScreen()
                    3 -> MCPStatusScreen()
                }
            }
        }
    }
}

data class TabItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)