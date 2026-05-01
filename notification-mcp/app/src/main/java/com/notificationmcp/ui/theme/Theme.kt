package com.notificationmcp.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// ── HyperOS Color Palette ──
// 小米 HyperOS 风格配色

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF0A84FF),
    onPrimary = Color.White,
    primaryContainer = Color(0xFF00497D),
    onPrimaryContainer = Color(0xFFD1E4FF),
    secondary = Color(0xFF5E5CE6),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF3A3A8F),
    onSecondaryContainer = Color(0xFFE0DEFF),
    tertiary = Color(0xFF30D158),
    onTertiary = Color.Black,
    background = Color(0xFF0A0A14),
    onBackground = Color(0xFFE5E5E5),
    surface = Color(0xFF1C1C2E),
    onSurface = Color(0xFFE5E5E5),
    surfaceVariant = Color(0xFF2A2A3E),
    onSurfaceVariant = Color(0xFFCAC4D0),
    error = Color(0xFFFF453A),
    onError = Color.White,
    outline = Color(0xFF8E8E93)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF007AFF),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD1E4FF),
    onPrimaryContainer = Color(0xFF001D36),
    secondary = Color(0xFF5856D6),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE0DEFF),
    onSecondaryContainer = Color(0xFF151336),
    tertiary = Color(0xFF34C759),
    onTertiary = Color.Black,
    background = Color(0xFFF0F0F5),
    onBackground = Color(0xFF1C1C1E),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1C1C1E),
    surfaceVariant = Color(0xFFF2F2F7),
    onSurfaceVariant = Color(0xFF3A3A3C),
    error = Color(0xFFFF3B30),
    onError = Color.White,
    outline = Color(0xFFC6C6C8)
)

@Composable
fun NotificationMCPTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window

            // 透明状态栏和导航栏
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = Color.Transparent.toArgb()

            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = content
    )
}
