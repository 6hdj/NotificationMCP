package com.notificationmcp.opengl

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.widget.ImageView

/**
 * BlurHelper - 高斯模糊工具类
 *
 * 功能：
 * 1. 捕获 View 内容为 Bitmap
 * 2. 管理 BlurGLSurfaceView 的生命周期
 * 3. 提供简洁的 API 接口
 */
object BlurHelper {
    
    private const val TAG = "BlurHelper"
    
    /**
     * 捕获 View 内容为 Bitmap
     *
     * @param view 要捕获的 View
     * @param scale 缩放比例（0.0 - 1.0），默认 0.25（1/4 降采样）
     * @return 捕获的 Bitmap，失败返回 null
     */
    fun captureView(view: View, scale: Float = 0.25f): Bitmap? {
        return try {
            val width = view.width
            val height = view.height
            
            if (width <= 0 || height <= 0) {
                Log.w(TAG, "View has invalid dimensions: ${width}x${height}")
                return null
            }
            
            val scaledWidth = (width * scale).toInt()
            val scaledHeight = (height * scale).toInt()
            
            val bitmap = Bitmap.createBitmap(scaledWidth, scaledHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            
            // 缩放画布
            val scaleX = scaledWidth.toFloat() / width
            val scaleY = scaledHeight.toFloat() / height
            canvas.scale(scaleX, scaleY)
            
            // 绘制 View 内容
            view.draw(canvas)
            
            Log.d(TAG, "View captured: ${width}x${height} -> ${scaledWidth}x${scaledHeight}")
            bitmap
        } catch (e: Exception) {
            Log.e(TAG, "Failed to capture view", e)
            null
        }
    }
    
    /**
     * 捕获 View 内容并更新到 BlurGLSurfaceView
     *
     * @param sourceView 要捕获的源 View
     * @param blurView 模糊渲染的 GLSurfaceView
     * @param scale 缩放比例（0.0 - 1.0）
     */
    fun captureAndUpdateBlur(
        sourceView: View,
        blurView: BlurGLSurfaceView,
        scale: Float = 0.25f
    ) {
        val bitmap = captureView(sourceView, scale)
        if (bitmap != null) {
            blurView.updateBitmap(bitmap)
        }
    }
    
    /**
     * 为 ImageView 设置模糊背景
     *
     * @param imageView 目标 ImageView
     * @param sourceView 源 View
     * @param blurRadius 模糊半径（0.0 - 25.0）
     * @param downsampleFactor 降采样因子（1, 2, 4, 8）
     */
    fun setBlurBackground(
        imageView: ImageView,
        sourceView: View,
        blurRadius: Float = 10.0f,
        downsampleFactor: Int = 4
    ) {
        // 捕获源 View
        val scale = 1.0f / downsampleFactor
        val bitmap = captureView(sourceView, scale)
        
        if (bitmap != null) {
            // 设置到 ImageView
            imageView.setImageBitmap(bitmap)
            
            // 应用模糊效果（如果需要动态模糊，可以使用 BlurGLSurfaceView）
            Log.d(TAG, "Blur background set: radius=$blurRadius, downsample=$downsampleFactor")
        }
    }
    
    /**
     * 创建动态模糊渲染器（用于实时模糊）
     *
     * @param activity Activity 上下文
     * @param sourceView 源 View
     * @param blurRadius 模糊半径
     * @param downsampleFactor 降采样因子
     * @return BlurGLSurfaceView 实例
     */
    fun createDynamicBlur(
        activity: Activity,
        sourceView: View,
        blurRadius: Float = 10.0f,
        downsampleFactor: Int = 4
    ): BlurGLSurfaceView {
        val blurView = BlurGLSurfaceView(activity)
        blurView.setBlurRadius(blurRadius)
        blurView.setDownsampleFactor(downsampleFactor)
        
        // 监听源 View 变化
        sourceView.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                captureAndUpdateBlur(sourceView, blurView, 1.0f / downsampleFactor)
                return true
            }
        })
        
        return blurView
    }
    
    /**
     * 释放 BlurGLSurfaceView 资源
     */
    fun releaseBlurView(blurView: BlurGLSurfaceView?) {
        blurView?.release()
    }
}