package com.notificationmcp.opengl

import android.content.Context
import android.graphics.Bitmap
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.util.Log

/**
 * BlurGLSurfaceView - 自定义 GLSurfaceView 用于高斯模糊渲染
 *
 * 特性：
 * 1. 使用独立的 GLThread 渲染，不阻塞 UI 线程
 * 2. 自动管理 GaussianBlurRenderer 的生命周期
 * 3. 提供简洁的 API 接口
 */
class BlurGLSurfaceView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : GLSurfaceView(context, attrs) {
    
    companion object {
        private const val TAG = "BlurGLSurfaceView"
    }
    
    private val renderer: GaussianBlurRenderer
    
    init {
        // 设置 OpenGL ES 2.0
        setEGLContextClientVersion(2)
        
        // 设置透明背景
        setZOrderOnTop(false)
        holder.setFormat(android.graphics.PixelFormat.TRANSLUCENT)
        
        // 创建渲染器
        renderer = GaussianBlurRenderer()
        
        // 设置渲染器
        setRenderer(renderer)
        
        // 设置渲染模式为按需渲染
        renderMode = RENDERMODE_WHEN_DIRTY
        
        Log.d(TAG, "BlurGLSurfaceView initialized")
    }
    
    /**
     * 设置模糊半径（0.0 - 25.0）
     */
    fun setBlurRadius(radius: Float) {
        renderer.setBlurRadius(radius)
        requestRender()
    }
    
    /**
     * 获取当前模糊半径
     */
    fun getBlurRadius(): Float = renderer.getBlurRadius()
    
    /**
     * 设置降采样因子（1, 2, 4, 8）
     */
    fun setDownsampleFactor(factor: Int) {
        renderer.setDownsampleFactor(factor)
        requestRender()
    }
    
    /**
     * 获取当前降采样因子
     */
    fun getDownsampleFactor(): Int = renderer.getDownsampleFactor()
    
    /**
     * 更新输入 Bitmap
     */
    fun updateBitmap(bitmap: Bitmap?) {
        renderer.updateBitmap(bitmap)
        requestRender()
    }
    
    /**
     * 设置渲染完成回调
     */
    fun setOnBlurRenderedListener(listener: GaussianBlurRenderer.OnBlurRenderedListener?) {
        renderer.setOnBlurRenderedListener(listener)
    }
    
    /**
     * 暂停渲染
     */
    fun pauseBlur() {
        onPause()
    }
    
    /**
     * 恢复渲染
     */
    fun resumeBlur() {
        onResume()
    }
    
    /**
     * 释放所有资源
     */
    fun release() {
        renderer.release()
        Log.d(TAG, "BlurGLSurfaceView released")
    }
}