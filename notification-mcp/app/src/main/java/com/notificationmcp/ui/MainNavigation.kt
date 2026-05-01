package com.notificationmcp.ui

import androidx.compose.animation.*
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
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.notificationmcp.ui.components.GlassNavBar
import com.notificationmcp.ui.screens.AutomationScreen
import com.notificationmcp.ui.screens.DashboardScreen
import com.notificationmcp.ui.screens.HistoryScreen
import com.notificationmcp.ui.screens.MCPStatusScreen
import com.notificationmcp.ui.screens.SettingsScreen

sealed class Screen(val route: String, val title: String, val icon: ImageVector, val selectedIcon: ImageVector) {
    data object Dashboard : Screen("dashboard", "首页", Icons.Outlined.Dashboard, Icons.Filled.Dashboard)
    data object History : Screen("history", "历史", Icons.Outlined.History, Icons.Filled.History)
    data object Automation : Screen("automation", "自动化", Icons.Outlined.AutoAwesome, Icons.Filled.AutoAwesome)
    data object Settings : Screen("settings", "设置", Icons.Outlined.Settings, Icons.Filled.Settings)
}

private val bottomScreens = listOf(Screen.Dashboard, Screen.History, Screen.Automation, Screen.Settings)

@Composable
fun MainNavigation() {
    val navController = rememberNavController()
    val isDark = isSystemInDarkTheme()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        containerColor = androidx.compose.ui.graphics.Color.Transparent,
        bottomBar = {
            GlassNavBar(
                isDark = isDark,
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .height(64.dp)
            ) {
                                bottomScreens.forEach { screen ->
                    val selected = currentDestination?.route == screen.route
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            if (currentDestination?.route != screen.route) {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        inclusive = false
                                        saveState = false
                                    }
                                    launchSingleTop = true
                                    restoreState = false
                                }
                            }
                        },
                        icon = {
                            Icon(
                                if (selected) screen.selectedIcon else screen.icon,
                                contentDescription = screen.title
                            )
                        },
                        label = { Text(screen.title) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route,
            modifier = Modifier.padding(innerPadding)
        ) {
                        composable(Screen.Dashboard.route) {
                DashboardScreen(onNavigate = { route ->
                    // 如果目标是底部导航栏的路由，直接导航
                    // 否则先回到 dashboard，再导航到子页面（确保底部导航栏状态正确）
                    val isBottomRoute = bottomScreens.any { it.route == route }
                    if (isBottomRoute) {
                        navController.navigate(route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    } else {
                        navController.navigate(route) {
                            launchSingleTop = true
                        }
                    }
                })
            }
            composable(Screen.History.route) { HistoryScreen() }
            composable(Screen.Automation.route) { AutomationScreen() }
            composable(Screen.Settings.route) { SettingsScreen() }
            composable("mcp_status") { MCPStatusScreen() }
            composable("about") {
                com.notificationmcp.ui.screens.AboutScreen()
            }
        }
    }
}
