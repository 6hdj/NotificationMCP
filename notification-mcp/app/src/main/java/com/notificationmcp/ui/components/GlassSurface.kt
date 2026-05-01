package com.notificationmcp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.notificationmcp.ui.theme.PreferencesManager

// ── HyperOS 高斯模糊色板 ──
object GlassPalette {
            // 深色模式（半透明，让背景隐约透出）
    val DarkBody = Color(0xB31A1A2E)
    val DarkBorder = Color(0x33FFFFFF)
    val DarkHighlight = Color(0x40FFFFFF)
    val DarkShadow = Color(0x20000000)

    // 浅色模式（实色，不透明，靠阴影区分层次）
    val LightBody = Color(0xFFFFFFFF)        // 纯白实色
    val LightBorder = Color.Transparent       // 不需要边框
    val LightHighlight = Color(0x18000000)    // 极淡顶部高光
    val LightShadow = Color(0x0D000000)       // 柔和阴影

    fun bodyColor(isDark: Boolean) = if (isDark) DarkBody else LightBody
    fun borderColor(isDark: Boolean) = if (isDark) DarkBorder else LightBorder
    fun highlightColor(isDark: Boolean) = if (isDark) DarkHighlight else LightHighlight
    fun shadowColor(isDark: Boolean) = if (isDark) DarkShadow else LightShadow
}

/**
 * GlassSurface — HyperOS 高斯模糊核心组件
 *
 * 浅色模式：纯白实色卡片 + 柔和阴影，无边框
 * 深色模式：半透明卡片 + 微妙边框
 */
@Composable
fun GlassSurface(
    isDark: Boolean,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 16.dp,
    elevation: Dp = 4.dp,
    blurEnabled: Boolean = true,
    shimmerEnabled: Boolean = false,
    content: @Composable () -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)
    val enabled by PreferencesManager.liquidGlassEnabled.collectAsState()

    if (!enabled) {
        val fallbackBg = if (isDark) Color(0xE01A1A2E) else Color(0xFFF8F8FA)
        Box(
            modifier = modifier
                .shadow(elevation, shape)
                .clip(shape)
                .background(fallbackBg)
        ) {
            content()
        }
        return
    }

    if (isDark) {
        // ── 深色模式：半透明 + 边框 ──
        Box(
            modifier = modifier
                .shadow(elevation, shape, ambientColor = GlassPalette.DarkShadow)
                .clip(shape)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            GlassPalette.DarkBody,
                            GlassPalette.DarkBody.copy(alpha = GlassPalette.DarkBody.alpha * 0.85f)
                        )
                    )
                )
                .drawWithContent {
                    drawContent()
                    val w = size.width
                    val stroke = 2.dp.toPx()
                    drawLine(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color.Transparent,
                                GlassPalette.DarkHighlight,
                                Color.Transparent
                            ),
                            startX = w * 0.1f,
                            endX = w * 0.9f
                        ),
                        start = Offset(w * 0.15f, stroke),
                        end = Offset(w * 0.85f, stroke),
                        strokeWidth = stroke
                    )
                }
                .border(
                    width = 0.5.dp,
                    color = GlassPalette.DarkBorder,
                    shape = shape
                )
        ) {
            content()
        }
    } else {
        // ── 浅色模式：纯白实色 + 阴影，无边框 ──
        Box(
            modifier = modifier
                .shadow(elevation, shape, ambientColor = Color(0x0D000000), spotColor = Color(0x14000000))
                .clip(shape)
                .background(Color.White)
        ) {
            content()
        }
    }
}

/**
 * GlassCard — 预设卡片样式
 */
@Composable
fun GlassCard(
    isDark: Boolean,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 16.dp,
    content: @Composable () -> Unit
) {
    GlassSurface(
        isDark = isDark,
        modifier = modifier,
        cornerRadius = cornerRadius,
        elevation = 4.dp,
        content = content
    )
}

/**
 * GlassDialogSurface — 弹窗用
 */
@Composable
fun GlassDialogSurface(
    isDark: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    GlassSurface(
        isDark = isDark,
        modifier = modifier,
        cornerRadius = 24.dp,
        elevation = 12.dp,
        content = content
    )
}

/**
 * GlassNavBar — 底部导航栏
 */
@Composable
fun GlassNavBar(
    isDark: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    val shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    val enabled by PreferencesManager.liquidGlassEnabled.collectAsState()

    if (!enabled) {
        val fallbackBg = if (isDark) Color(0xE01A1A2E) else Color(0xFFF8F8FA)
        Row(
            modifier = modifier
                .background(fallbackBg)
                .padding(horizontal = 8.dp),
            content = content
        )
        return
    }

    if (isDark) {
        Row(
            modifier = modifier
                .shadow(8.dp, shape, ambientColor = GlassPalette.DarkShadow)
                .clip(shape)
                .background(GlassPalette.DarkBody)
                .drawWithContent {
                    drawContent()
                    val w = size.width
                    drawLine(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color.Transparent,
                                GlassPalette.DarkHighlight.copy(alpha = 0.4f),
                                Color.Transparent
                            )
                        ),
                        start = Offset(w * 0.05f, 0.5f),
                        end = Offset(w * 0.95f, 0.5f),
                        strokeWidth = 1.dp.toPx()
                    )
                }
                .border(
                    width = 0.5.dp,
                    color = GlassPalette.DarkBorder.copy(alpha = 0.3f),
                    shape = shape
                )
                .padding(horizontal = 8.dp),
            content = content
        )
    } else {
        // 浅色模式：纯白实色底栏 + 阴影，无边框
        Row(
            modifier = modifier
                .shadow(8.dp, shape, ambientColor = Color(0x0D000000), spotColor = Color(0x14000000))
                .clip(shape)
                .background(Color.White)
                .padding(horizontal = 8.dp),
            content = content
        )
    }
}

/**
 * GlassOverlay — 全屏模糊遮罩
 */
@Composable
fun GlassOverlay(
    isDark: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val overlayAlpha = if (isDark) 0.65f else 0.35f
    Box(
        modifier = modifier
            .background(Color.Black.copy(alpha = overlayAlpha))
    ) {
        content()
    }
}

/**
 * GlassDivider — 玻璃风格分隔线
 */
@Composable
fun GlassDivider(
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    val dividerColor = if (isDark) {
        GlassPalette.DarkBorder.copy(alpha = 0.4f)
    } else {
        Color(0x1A000000)  // 浅色模式用 10% 黑色
    }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(0.5.dp)
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color.Transparent,
                        dividerColor,
                        Color.Transparent
                    )
                )
            )
    )
}
