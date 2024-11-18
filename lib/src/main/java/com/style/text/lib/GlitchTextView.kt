package com.style.text.lib
import android.content.Context
import android.graphics.*
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View

class GlitchTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var text = "17"
    private var textSize = 130f
    private val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        style = Paint.Style.FILL
        typeface = Typeface.create("sans-serif", Typeface.BOLD)
        textSize = this@GlitchTextView.textSize
    }

    private val glitchPaintCyan = TextPaint(textPaint).apply {
        color = Color.CYAN
    }

    private val glitchPaintMagenta = TextPaint(textPaint).apply {
        color = Color.MAGENTA
    }

    private var glitchOffsetCyan = PointF(0f, 0f)
    private var glitchOffsetMagenta = PointF(0f, 0f)

    init {
        // Set default text size
        textPaint.textSize = textSize
        glitchPaintCyan.textSize = textSize
        glitchPaintMagenta.textSize = textSize

        setText("17")
        setTextSize(130F)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Calculate the center of the view
        val centerX = width / 2f
        val centerY = height / 2f

        // Measure text bounds
        val textBounds = Rect()
        textPaint.getTextBounds(text, 0, text.length, textBounds)

        // Calculate baseline
        val textWidth = textPaint.measureText(text)
        val textHeight = textBounds.height()
        val baselineX = centerX - textWidth / 2
        val baselineY = centerY + textHeight / 2

        // Draw glitch layers (cyan and magenta offsets)
        canvas.drawText(
            text,
            baselineX + glitchOffsetCyan.x,
            baselineY + glitchOffsetCyan.y,
            glitchPaintCyan
        )
        canvas.drawText(
            text,
            baselineX + glitchOffsetMagenta.x,
            baselineY + glitchOffsetMagenta.y,
            glitchPaintMagenta
        )

        // Draw main text (white)
        canvas.drawText(text, baselineX, baselineY, textPaint)

        // Update glitch effect offsets
        updateGlitchOffsets()

        // Redraw view for animation
        postInvalidateOnAnimation()
    }

    private fun updateGlitchOffsets() {
        glitchOffsetCyan.x = (-15..15).random().toFloat()
        glitchOffsetCyan.y = (-15..15).random().toFloat()

        glitchOffsetMagenta.x = (-15..15).random().toFloat()
        glitchOffsetMagenta.y = (-15..15).random().toFloat()
    }

    fun setText(newText: String) {
        text = newText
        invalidate()
    }

    fun setTextSize(size: Float) {
        textSize = size
        textPaint.textSize = size
        glitchPaintCyan.textSize = size
        glitchPaintMagenta.textSize = size
        invalidate()
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = (textPaint.measureText(text) + paddingLeft + paddingRight).toInt()
        val height = (textPaint.descent() - textPaint.ascent() + paddingTop + paddingBottom).toInt()
        setMeasuredDimension(width, height)
    }
}
