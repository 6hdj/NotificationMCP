package com.notificationmcp.opengl

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.view.View
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import android.util.Log

/**
 * BlurBackground - Compose 集成的高斯模糊背景组件
 *
 * 使用方法：
 * ```kotlin
 * BlurBackground(
 *     sourceView = yourSourceView,
 *     blurRadius = 10.0f,
 *     downsampleFactor = 4
 * ) {
 *     // 前景内容
 *     Text("Hello World")
 * }
 * ```
 */
@Composable
fun BlurBackground(
    modifier: Modifier = Modifier,
    sourceView: View? = null,
    blurRadius: Float = 10.0f,
    downsampleFactor: Int = 4,
    content: @Composable BoxScope.() -> Unit
) {
    var blurView by remember { mutableStateOf<BlurGLSurfaceView?>(null) }
    
    // 清理资源
    DisposableEffect(Unit) {
        onDispose {
            blurView?.release()
        }
    }
    
    Box(modifier = modifier) {
        // OpenGL 模糊背景层
        if (sourceView != null) {
            AndroidView(
                factory = { context ->
                    BlurGLSurfaceView(context).apply {
                        setBlurRadius(blurRadius)
                        setDownsampleFactor(downsampleFactor)
                        blurView = this
                    }
                },
                update = { view ->
                    // 捕获源 View 并更新
                    val scale = 1.0f / downsampleFactor
                    val bitmap = BlurHelper.captureView(sourceView, scale)
                    if (bitmap != null) {
                        view.updateBitmap(bitmap)
                    }
                },
                modifier = Modifier.matchParentSize()
            )
        }
        
        // 前景内容
        content()
    }
}

/**
 * BlurBackgroundWithBitmap - 使用 Bitmap 作为模糊源的版本
 *
 * 适用于没有直接 View 引用的场景
 */
@Composable
fun BlurBackgroundWithBitmap(
    modifier: Modifier = Modifier,
    bitmap: Bitmap? = null,
    blurRadius: Float = 10.0f,
    downsampleFactor: Int = 4,
    content: @Composable BoxScope.() -> Unit
) {
    var blurView by remember { mutableStateOf<BlurGLSurfaceView?>(null) }
    
    // 清理资源
    DisposableEffect(Unit) {
        onDispose {
            blurView?.release()
        }
    }
    
    Box(modifier = modifier) {
        // OpenGL 模糊背景层
        AndroidView(
            factory = { context ->
                BlurGLSurfaceView(context).apply {
                    setBlurRadius(blurRadius)
                    setDownsampleFactor(downsampleFactor)
                    blurView = this
                }
            },
            update = { view ->
                if (bitmap != null) {
                    view.updateBitmap(bitmap)
                }
            },
            modifier = Modifier.matchParentSize()
        )
        
        // 前景内容
        content()
    }
}

/**
 * DynamicBlurBackground - 动态模糊背景（实时更新）
 *
 * 适用于需要实时模糊的场景，如滚动时的模糊效果
 */
@Composable
fun DynamicBlurBackground(
    modifier: Modifier = Modifier,
    sourceView: View? = null,
    blurRadius: Float = 10.0f,
    downsampleFactor: Int = 4,
    updateIntervalMs: Long = 100L,
    content: @Composable BoxScope.() -> Unit
) {
    var blurView by remember { mutableStateOf<BlurGLSurfaceView?>(null) }
    var lastUpdateTime by remember { mutableLongStateOf(0L) }
    
    // 定时更新模糊
    LaunchedEffect(sourceView) {
        while (true) {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastUpdateTime >= updateIntervalMs) {
                sourceView?.let { view ->
                    val scale = 1.0f / downsampleFactor
                    val bitmap = BlurHelper.captureView(view, scale)
                    if (bitmap != null) {
                        blurView?.updateBitmap(bitmap)
                        lastUpdateTime = currentTime
                    }
                }
            }
            kotlinx.coroutines.delay(updateIntervalMs)
        }
    }
    
    // 清理资源
    DisposableEffect(Unit) {
        onDispose {
            blurView?.release()
        }
    }
    
    Box(modifier = modifier) {
        // OpenGL 模糊背景层
        if (sourceView != null) {
            AndroidView(
                factory = { context ->
                    BlurGLSurfaceView(context).apply {
                        setBlurRadius(blurRadius)
                        setDownsampleFactor(downsampleFactor)
                        blurView = this
                    }
                },
                modifier = Modifier.matchParentSize()
            )
        }
        
        // 前景内容
        content()
    }
}