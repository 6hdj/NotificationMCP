package com.notificationmcp.ui.components

import android.content.Context
import android.graphics.*
import android.os.Build
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.util.AttributeSet
import android.view.View
import android.view.ViewTreeObserver
import kotlin.math.min

/**
 * Real-time blur view for Android 8.0 - 11 (API 26-30).
 * Captures the background behind this view and applies a Gaussian blur.
 * On API 31+ this is not needed (Compose Modifier.blur works natively).
 */
class RealtimeBlurView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var blurRadius: Float = 25f // 1-25, in pixels
    private var overlayColor: Int = Color.TRANSPARENT

    private var blurBitmap: Bitmap? = null
    private var blurCanvas: Canvas? = null
    private var renderScript: RenderScript? = null
    private var blurScript: ScriptIntrinsicBlur? = null
    private var inputAllocation: Allocation? = null
    private var outputAllocation: Allocation? = null

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val srcPaint = Paint()

    private var isBlurring = false
    private var downsampleFactor: Float = 4f

    private var preDrawListener: ViewTreeObserver.OnPreDrawListener? = null

    fun setBlurRadius(radius: Float) {
        blurRadius = radius
        blurScript?.setRadius(min(radius, 25f))
    }

    fun setOverlayColor(color: Int) {
        overlayColor = color
        invalidate()
    }

    fun setDownsampleFactor(factor: Float) {
        downsampleFactor = factor.coerceAtLeast(1f)
    }

    fun startBlur() {
        if (isBlurring) return
        isBlurring = true

        renderScript = RenderScript.create(context)
        blurScript = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript))

        preDrawListener = ViewTreeObserver.OnPreDrawListener {
            blur()
            true
        }
        viewTreeObserver.addOnPreDrawListener(preDrawListener)
    }

    fun stopBlur() {
        isBlurring = false
        preDrawListener?.let { viewTreeObserver.removeOnPreDrawListener(it) }
        preDrawListener = null

        blurBitmap?.recycle()
        blurBitmap = null
        inputAllocation?.destroy()
        outputAllocation?.destroy()
        blurScript?.destroy()
        renderScript?.destroy()
        inputAllocation = null
        outputAllocation = null
        blurScript = null
        renderScript = null
    }

    private fun blur() {
        val w = width
        val h = height
        if (w <= 0 || h <= 0) return

        val sw = (w / downsampleFactor).toInt().coerceAtLeast(1)
        val sh = (h / downsampleFactor).toInt().coerceAtLeast(1)

        if (blurBitmap == null || blurBitmap?.width != sw || blurBitmap?.height != sh) {
            blurBitmap?.recycle()
            blurBitmap = Bitmap.createBitmap(sw, sh, Bitmap.Config.ARGB_8888)
            blurCanvas = Canvas(blurBitmap!!)

            inputAllocation?.destroy()
            outputAllocation?.destroy()

            inputAllocation = Allocation.createFromBitmap(renderScript, blurBitmap)
            outputAllocation = Allocation.createTyped(renderScript, inputAllocation!!.type)
        }

        // Capture the view behind us
        val canvas = blurCanvas!!
        canvas.save()
        canvas.scale(1f / downsampleFactor, 1f / downsampleFactor)

        // Draw the parent's background
        drawParentBackground(canvas)
        canvas.restore()

        // Apply blur
        inputAllocation?.copyFrom(blurBitmap)
        blurScript?.setInput(inputAllocation)
        blurScript?.forEach(outputAllocation)
        outputAllocation?.copyTo(blurBitmap)

        invalidate()
    }

    private fun drawParentBackground(canvas: Canvas) {
        val parent = parent as? View ?: return
        // Simple approach: draw the parent's background color
        val bg = parent.background
        if (bg != null) {
            bg.draw(canvas)
        } else {
            canvas.drawColor(Color.BLACK)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        blurBitmap?.let { bmp ->
            // Draw blurred bitmap scaled to view size
            val src = Rect(0, 0, bmp.width, bmp.height)
            val dst = Rect(0, 0, width, height)
            canvas.drawBitmap(bmp, src, dst, srcPaint)

            // Draw overlay tint
            if (overlayColor != Color.TRANSPARENT) {
                canvas.drawColor(overlayColor)
            }
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (isBlurring) startBlur()
    }

    override fun onDetachedFromWindow() {
        stopBlur()
        super.onDetachedFromWindow()
    }
}
