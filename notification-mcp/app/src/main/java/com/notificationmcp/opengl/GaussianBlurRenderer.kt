package com.notificationmcp.opengl

import android.graphics.Bitmap
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.GLUtils
import android.util.Log
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * GaussianBlurRenderer - OpenGL ES 2.0 实时高斯模糊渲染器
 *
 * 核心特性：
 * 1. 分离式高斯模糊（Two-pass）：水平 + 垂直
 * 2. FBO 离屏渲染：两个 FBO 分别存储中间结果和最终结果
 * 3. 降采样优化：可配置 downsampleFactor（默认 1/4）
 * 4. 可调节模糊半径：setBlurRadius(float radius)
 * 5. 着色器代码：9-tap 高斯核（权重：0.05, 0.09, 0.12, 0.15, 0.16, 0.15, 0.12, 0.09, 0.05）
 */
class GaussianBlurRenderer : GLSurfaceView.Renderer {
    companion object {
        private const val TAG = "GaussianBlurRenderer"
        
        // 顶点着色器
        private const val VERTEX_SHADER = """
            attribute vec4 aPosition;
            attribute vec4 aTextureCoord;
            varying vec2 vTexCoord;
            void main() {
                gl_Position = aPosition;
                vTexCoord = aTextureCoord.xy;
            }
        """
        
        // 片段着色器（水平/垂直模糊逻辑）
        private const val FRAGMENT_SHADER = """
            precision mediump float;
            varying vec2 vTexCoord;
            uniform sampler2D uTexture;
            uniform float uTexelWidthOffset;
            uniform float uTexelHeightOffset;
            
            void main() {
                vec4 sum = vec4(0.0);
                // 9-tap 高斯核
                sum += texture2D(uTexture, vTexCoord + vec2(-4.0 * uTexelWidthOffset, -4.0 * uTexelHeightOffset)) * 0.05;
                sum += texture2D(uTexture, vTexCoord + vec2(-3.0 * uTexelWidthOffset, -3.0 * uTexelHeightOffset)) * 0.09;
                sum += texture2D(uTexture, vTexCoord + vec2(-2.0 * uTexelWidthOffset, -2.0 * uTexelHeightOffset)) * 0.12;
                sum += texture2D(uTexture, vTexCoord + vec2(-1.0 * uTexelWidthOffset, -1.0 * uTexelHeightOffset)) * 0.15;
                sum += texture2D(uTexture, vTexCoord) * 0.16;
                sum += texture2D(uTexture, vTexCoord + vec2(1.0 * uTexelWidthOffset, 1.0 * uTexelHeightOffset)) * 0.15;
                sum += texture2D(uTexture, vTexCoord + vec2(2.0 * uTexelWidthOffset, 2.0 * uTexelHeightOffset)) * 0.12;
                sum += texture2D(uTexture, vTexCoord + vec2(3.0 * uTexelWidthOffset, 3.0 * uTexelHeightOffset)) * 0.09;
                sum += texture2D(uTexture, vTexCoord + vec2(4.0 * uTexelWidthOffset, 4.0 * uTexelHeightOffset)) * 0.05;
                gl_FragColor = sum;
            }
        """
        
        // 全屏四边形顶点坐标
        private val QUAD_VERTICES = floatArrayOf(
            -1.0f, -1.0f,  // 左下
             1.0f, -1.0f,  // 右下
            -1.0f,  1.0f,  // 左上
             1.0f,  1.0f   // 右上
        )
        
        // 纹理坐标
        private val QUAD_TEX_COORDS = floatArrayOf(
            0.0f, 0.0f,  // 左下
            1.0f, 0.0f,  // 右下
            0.0f, 1.0f,  // 左上
            1.0f, 1.0f   // 右上
        )
    }
    
    // 渲染状态
    private var programId = 0
    private var positionHandle = 0
    private var texCoordHandle = 0
    private var textureHandle = 0
    private var texelWidthOffsetHandle = 0
    private var texelHeightOffsetHandle = 0
    
    // FBO 和纹理
    private var fboIds = IntArray(2)
    private var fboTextureIds = IntArray(2)
    private var inputTextureId = 0
    
    // 降采样后的尺寸
    private var scaledWidth = 0
    private var scaledHeight = 0
    
    // 输入 Bitmap
    private var inputBitmap: Bitmap? = null
    private var bitmapUploaded = false
    
    // 模糊参数
    private var blurRadius = 10.0f
    private var downsampleFactor = 4  // 默认 1/4 降采样
    
    // 顶点和纹理坐标缓冲区
    private lateinit var vertexBuffer: FloatBuffer
    private lateinit var texCoordBuffer: FloatBuffer
    
    // 渲染就绪标志
    private var isReady = false
    
    // 回调接口
    interface OnBlurRenderedListener {
        fun onBlurRendered()
    }
    
    private var listener: OnBlurRenderedListener? = null
    
    /**
     * 设置模糊半径（0.0 - 25.0）
     */
    fun setBlurRadius(radius: Float) {
        blurRadius = radius.coerceIn(0.0f, 25.0f)
        Log.d(TAG, "Blur radius set to: $blurRadius")
    }
    
    /**
     * 获取当前模糊半径
     */
    fun getBlurRadius(): Float = blurRadius
    
    /**
     * 设置降采样因子（1, 2, 4, 8）
     */
    fun setDownsampleFactor(factor: Int) {
        downsampleFactor = factor.coerceIn(1, 8)
        Log.d(TAG, "Downsample factor set to: $downsampleFactor")
    }
    
    /**
     * 获取当前降采样因子
     */
    fun getDownsampleFactor(): Int = downsampleFactor
    
    /**
     * 设置渲染完成回调
     */
    fun setOnBlurRenderedListener(listener: OnBlurRenderedListener?) {
        this.listener = listener
    }
    
    /**
     * 更新输入 Bitmap
     */
    fun updateBitmap(bitmap: Bitmap?) {
        inputBitmap?.recycle()
        inputBitmap = bitmap
        bitmapUploaded = false
        Log.d(TAG, "Bitmap updated: ${bitmap?.width}x${bitmap?.height}")
    }
    
    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        Log.d(TAG, "Surface created")
        
        // 设置背景色（透明）
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f)
        
        // 启用混合（支持透明度）
        GLES20.glEnable(GLES20.GL_BLEND)
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA)
        
        // 编译着色器
        val vertexShader = compileShader(GLES20.GL_VERTEX_SHADER, VERTEX_SHADER)
        val fragmentShader = compileShader(GLES20.GL_FRAGMENT_SHADER, FRAGMENT_SHADER)
        
        // 创建着色器程序
        programId = GLES20.glCreateProgram()
        GLES20.glAttachShader(programId, vertexShader)
        GLES20.glAttachShader(programId, fragmentShader)
        GLES20.glLinkProgram(programId)
        
        // 检查链接状态
        val linkStatus = IntArray(1)
        GLES20.glGetProgramiv(programId, GLES20.GL_LINK_STATUS, linkStatus, 0)
        if (linkStatus[0] == 0) {
            val error = GLES20.glGetProgramInfoLog(programId)
            GLES20.glDeleteProgram(programId)
            throw RuntimeException("Program link failed: $error")
        }
        
        // 获取属性和 uniform 位置
        positionHandle = GLES20.glGetAttribLocation(programId, "aPosition")
        texCoordHandle = GLES20.glGetAttribLocation(programId, "aTextureCoord")
        textureHandle = GLES20.glGetUniformLocation(programId, "uTexture")
        texelWidthOffsetHandle = GLES20.glGetUniformLocation(programId, "uTexelWidthOffset")
        texelHeightOffsetHandle = GLES20.glGetUniformLocation(programId, "uTexelHeightOffset")
        
        // 准备顶点和纹理坐标缓冲区
        vertexBuffer = createFloatBuffer(QUAD_VERTICES)
        texCoordBuffer = createFloatBuffer(QUAD_TEX_COORDS)
        
        // 生成纹理
        val textures = IntArray(3)
        GLES20.glGenTextures(3, textures, 0)
        inputTextureId = textures[0]
        fboTextureIds[0] = textures[1]
        fboTextureIds[1] = textures[2]
        
        // 生成 FBO
        GLES20.glGenFramebuffers(2, fboIds, 0)
        
        Log.d(TAG, "OpenGL initialized: program=$programId, textures=${textures.toList()}, fbo=${fboIds.toList()}")
    }
    
    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        Log.d(TAG, "Surface changed: ${width}x${height}")
        
        GLES20.glViewport(0, 0, width, height)
        
        // 计算降采样后的尺寸
        scaledWidth = width / downsampleFactor
        scaledHeight = height / downsampleFactor
        
        // 重新创建 FBO 纹理
        setupFBOTextures()
        
        isReady = true
    }
    
    override fun onDrawFrame(gl: GL10?) {
        if (!isReady || inputBitmap == null) {
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
            return
        }
        
        // 上传 Bitmap 到纹理（仅首次）
        if (!bitmapUploaded && inputBitmap != null) {
            uploadBitmapToTexture(inputBitmap!!)
            bitmapUploaded = true
        }
        
        // 检查纹理是否有效
        if (inputTextureId == 0) {
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
            return
        }
        
        // 计算 texel 偏移量
        val texelWidthOffset = blurRadius / scaledWidth.toFloat()
        val texelHeightOffset = blurRadius / scaledHeight.toFloat()
        
        // Pass 1: 水平模糊（输入 → FBO0）
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fboIds[0])
        GLES20.glViewport(0, 0, scaledWidth, scaledHeight)
        
        GLES20.glUseProgram(programId)
        
        // 绑定输入纹理
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, inputTextureId)
        GLES20.glUniform1i(textureHandle, 0)
        
        // 设置水平偏移（垂直偏移为 0）
        GLES20.glUniform1f(texelWidthOffsetHandle, texelWidthOffset)
        GLES20.glUniform1f(texelHeightOffsetHandle, 0.0f)
        
        // 绘制全屏四边形
        drawQuad()
        
        // Pass 2: 垂直模糊（FBO0 → FBO1）
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fboIds[1])
        GLES20.glViewport(0, 0, scaledWidth, scaledHeight)
        
        // 绑定 FBO0 的纹理
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, fboTextureIds[0])
        GLES20.glUniform1i(textureHandle, 0)
        
        // 设置垂直偏移（水平偏移为 0）
        GLES20.glUniform1f(texelWidthOffsetHandle, 0.0f)
        GLES20.glUniform1f(texelHeightOffsetHandle, texelHeightOffset)
        
        // 绘制全屏四边形
        drawQuad()
        
        // Pass 3: 最终绘制到屏幕（FBO1 → 屏幕）
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0)
        GLES20.glViewport(0, 0, scaledWidth * downsampleFactor, scaledHeight * downsampleFactor)
        
        // 绑定 FBO1 的纹理
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, fboTextureIds[1])
        GLES20.glUniform1i(textureHandle, 0)
        
        // 设置偏移为 0（直接绘制）
        GLES20.glUniform1f(texelWidthOffsetHandle, 0.0f)
        GLES20.glUniform1f(texelHeightOffsetHandle, 0.0f)
        
        // 绘制全屏四边形
        drawQuad()
        
        // 通知渲染完成
        listener?.onBlurRendered()
    }
    
    /**
     * 释放所有 OpenGL 资源
     */
    fun release() {
        Log.d(TAG, "Releasing OpenGL resources")
        
        if (programId != 0) {
            GLES20.glDeleteProgram(programId)
            programId = 0
        }
        
        if (inputTextureId != 0) {
            GLES20.glDeleteTextures(1, intArrayOf(inputTextureId), 0)
            inputTextureId = 0
        }
        
        if (fboTextureIds[0] != 0 || fboTextureIds[1] != 0) {
            GLES20.glDeleteTextures(2, fboTextureIds, 0)
            fboTextureIds = IntArray(2)
        }
        
        if (fboIds[0] != 0 || fboIds[1] != 0) {
            GLES20.glDeleteFramebuffers(2, fboIds, 0)
            fboIds = IntArray(2)
        }
        
        inputBitmap?.recycle()
        inputBitmap = null
        
        isReady = false
        Log.d(TAG, "OpenGL resources released")
    }
    
    // ── 私有方法 ──
    
    private fun compileShader(type: Int, source: String): Int {
        val shader = GLES20.glCreateShader(type)
        GLES20.glShaderSource(shader, source)
        GLES20.glCompileShader(shader)
        
        val compileStatus = IntArray(1)
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compileStatus, 0)
        if (compileStatus[0] == 0) {
            val error = GLES20.glGetShaderInfoLog(shader)
            GLES20.glDeleteShader(shader)
            throw RuntimeException("Shader compile failed: $error")
        }
        
        return shader
    }
    
    private fun createFloatBuffer(data: FloatArray): FloatBuffer {
        val buffer = ByteBuffer.allocateDirect(data.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        buffer.put(data)
        buffer.position(0)
        return buffer
    }
    
    private fun setupFBOTextures() {
        // 设置 FBO0 纹理参数
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, fboTextureIds[0])
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE)
        GLES20.glTexImage2D(
            GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA,
            scaledWidth, scaledHeight, 0,
            GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null
        )
        
        // 设置 FBO1 纹理参数
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, fboTextureIds[1])
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE)
        GLES20.glTexImage2D(
            GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA,
            scaledWidth, scaledHeight, 0,
            GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null
        )
        
        // 绑定纹理到 FBO
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fboIds[0])
        GLES20.glFramebufferTexture2D(
            GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
            GLES20.GL_TEXTURE_2D, fboTextureIds[0], 0
        )
        
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fboIds[1])
        GLES20.glFramebufferTexture2D(
            GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
            GLES20.GL_TEXTURE_2D, fboTextureIds[1], 0
        )
        
        // 解绑 FBO
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0)
        
        Log.d(TAG, "FBO textures setup: ${scaledWidth}x${scaledHeight}")
    }
    
    private fun uploadBitmapToTexture(bitmap: Bitmap) {
        // 缩放 Bitmap 到降采样尺寸
        val scaledBitmap = if (bitmap.width != scaledWidth || bitmap.height != scaledHeight) {
            Bitmap.createScaledBitmap(bitmap, scaledWidth, scaledHeight, true)
        } else {
            bitmap
        }
        
        // 绑定输入纹理
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, inputTextureId)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE)
        
        // 上传 Bitmap 数据到纹理
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, scaledBitmap, 0)
        
        // 如果缩放了，回收临时 Bitmap
        if (scaledBitmap != bitmap) {
            scaledBitmap.recycle()
        }
        
        Log.d(TAG, "Bitmap uploaded to texture: ${scaledWidth}x${scaledHeight}")
    }
    
    private fun drawQuad() {
        // 启用顶点属性
        GLES20.glEnableVertexAttribArray(positionHandle)
        GLES20.glVertexAttribPointer(
            positionHandle, 2, GLES20.GL_FLOAT,
            false, 0, vertexBuffer
        )
        
        GLES20.glEnableVertexAttribArray(texCoordHandle)
        GLES20.glVertexAttribPointer(
            texCoordHandle, 2, GLES20.GL_FLOAT,
            false, 0, texCoordBuffer
        )
        
        // 绘制三角形带（全屏四边形）
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)
        
        // 禁用顶点属性
        GLES20.glDisableVertexAttribArray(positionHandle)
        GLES20.glDisableVertexAttribArray(texCoordHandle)
    }
}