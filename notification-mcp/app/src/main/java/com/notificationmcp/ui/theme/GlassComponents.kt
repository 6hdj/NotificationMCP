package com.notificationmcp.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// ── HyperOS 高斯模糊色板 ──
val GlassTintDark  = Color(0xB31A1A2E)
val GlassBorderDark  = Color(0x33FFFFFF)

/**
 * HyperOS 高斯模糊卡片 Modifier
 *
 * 浅色模式：纯白实色 + 柔和阴影
 * 深色模式：半透明 + 微妙边框
 */
@Composable
fun Modifier.liquidGlass(
    isDark: Boolean,
    cornerRadius: Dp = 16.dp,
    blurRadius: Float = 25f,
    elevation: Dp = 4.dp
): Modifier {
    val enabled by PreferencesManager.liquidGlassEnabled.collectAsState()
    val shape = RoundedCornerShape(cornerRadius)

    if (!enabled) {
        val fallbackBg = if (isDark) Color(0xE01A1A2E) else Color(0xFFF8F8FA)
        return this.background(fallbackBg)
    }

    if (isDark) {
        return this
            .shadow(elevation, shape, ambientColor = Color(0x20000000))
            .clip(shape)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(GlassTintDark, GlassTintDark.copy(alpha = GlassTintDark.alpha * 0.9f))
                )
            )
            .border(width = 0.5.dp, color = GlassBorderDark, shape = shape)
    } else {
        // 浅色模式：纯白实色 + 阴影，无边框
        return this
            .shadow(elevation, shape, ambientColor = Color(0x0D000000), spotColor = Color(0x14000000))
            .clip(shape)
            .background(Color.White)
    }
}

/**
 * 简洁玻璃背景 — 用于设置行等平面区域
 */
@Composable
fun Modifier.glassBackground(
    isDark: Boolean,
    cornerRadius: Dp = 16.dp
): Modifier {
    val enabled by PreferencesManager.liquidGlassEnabled.collectAsState()
    if (!enabled) {
        val fallbackBg = if (isDark) Color(0xE01A1A2E) else Color(0xFFF8F8FA)
        return this.background(fallbackBg)
    }
    val shape = RoundedCornerShape(cornerRadius)

    if (isDark) {
        return this
            .shadow(2.dp, shape, ambientColor = Color(0x10000000))
            .clip(shape)
            .background(GlassTintDark)
            .border(width = 0.5.dp, color = GlassBorderDark.copy(alpha = 0.3f), shape = shape)
    } else {
        return this
            .shadow(2.dp, shape, ambientColor = Color(0x08000000))
            .clip(shape)
            .background(Color.White)
    }
}

/**
 * 全屏背景层
 */
@Composable
fun LiquidBlurBackground(
    isDark: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val bgColor = if (isDark) Color(0xFF0A0A14) else Color(0xFFF0F0F5)
    Box(modifier = modifier.background(bgColor)) {
        content()
    }
}

/**
 * 弹窗遮罩
 */
@Composable
fun BlurOverlay(
    isDark: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val overlayColor = if (isDark) Color.Black.copy(alpha = 0.6f) else Color.Black.copy(alpha = 0.3f)
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(overlayColor)
    ) {
        content()
    }
}

/**
 * 光效（HyperOS 风格下不使用）
 */
@Composable
fun Modifier.glassShimmer(): Modifier {
    return this.graphicsLayer {
        this.alpha = 1f
    }
}

/**
 * HyperOS 风格底部导航栏背景
 */
@Composable
fun Modifier.glassNavBar(isDark: Boolean): Modifier {
    val enabled by PreferencesManager.liquidGlassEnabled.collectAsState()

    if (!enabled) {
        val fallbackBg = if (isDark) Color(0xE01A1A2E) else Color(0xFFF8F8FA)
        return this.background(fallbackBg)
    }

    val shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)

    if (isDark) {
        return this
            .shadow(8.dp, shape, ambientColor = Color.Black.copy(alpha = 0.12f))
            .clip(shape)
            .background(GlassTintDark)
            .border(0.5.dp, GlassBorderDark, shape)
    } else {
        return this
            .shadow(8.dp, shape, ambientColor = Color(0x0D000000), spotColor = Color(0x14000000))
            .clip(shape)
            .background(Color.White)
    }
}
