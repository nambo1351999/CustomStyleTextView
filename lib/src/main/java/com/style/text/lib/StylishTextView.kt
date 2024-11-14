package com.style.text.lib

import android.content.Context
import android.graphics.*

import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap

class StylishTextView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private val textBounds = Rect()
    private val textStylish = "0930"
    private val textPaint: TextPaint = TextPaint().apply {
        flags = Paint.ANTI_ALIAS_FLAG
        textAlign = Paint.Align.CENTER
        color = Color.RED
        style = Paint.Style.FILL
        textSize = 160F
    }

    private val strokePaint = TextPaint().apply {
        isAntiAlias = true
        textSize = 160f
        textAlign = Paint.Align.CENTER
        color = Color.RED
        style = Paint.Style.STROKE
        strokeWidth = 8f
    }

    private val text3DPaint = TextPaint().apply {
        isAntiAlias = true
        textSize = 160f
        textAlign = Paint.Align.CENTER
        color = Color.RED
        style = Paint.Style.STROKE
        strokeWidth = 8f
    }

    private val shadowPaint: Paint = Paint().apply {
        isAntiAlias = true
        color = Color.BLUE
        setShadowLayer(10f, 4f, 4f, Color.BLUE)
    }
    private var bitmap: Bitmap? = null
    private val path = Path()
    private var shader: BitmapShader? = null

    init {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.StylishTextView)
        val drawable = ta.getDrawable(
            R.styleable.StylishTextView_stylish_drawable
        )

        val fontFamilyId: Int =
            ta.getResourceId(R.styleable.StylishTextView_android_fontFamily, 0)
        if (fontFamilyId > 0) {
            textPaint.typeface = ResourcesCompat.getFont(getContext(), fontFamilyId)
            strokePaint.typeface = ResourcesCompat.getFont(getContext(), fontFamilyId)
        }

        drawable?.let {
            bitmap = it.toBitmap()
            shader = BitmapShader(it.toBitmap(), Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
            textPaint.shader = shader
        }
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        setBackgroundColor(Color.TRANSPARENT)
        canvas.save()
        canvas.translate(10f, 10f)
        canvas.drawPath(path, shadowPaint)
        canvas.restore()

        strokePaint.getTextPath(
            textStylish,
            0,
            textStylish.length,
            0F,
            height.toFloat(),
            path
        )
        canvas.drawPath(path, strokePaint)

        for (i in 1..5) {
            canvas.save()
            val offset = i * 7f
            text3DPaint.getTextPath(
                textStylish,
                0,
                textStylish.length,
                offset,
                height.toFloat() + offset,
                path
            )
            canvas.drawPath(path, text3DPaint)
            canvas.restore()
        }

        textPaint.getTextPath(
            textStylish,
            0,
            textStylish.length,
            0F,
            height.toFloat(),
            path
        )
        canvas.drawPath(path, textPaint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        textPaint.getTextBounds(textStylish, 0, textStylish.length, textBounds)
        setMeasuredDimension(textBounds.width(), textBounds.height())
    }
}