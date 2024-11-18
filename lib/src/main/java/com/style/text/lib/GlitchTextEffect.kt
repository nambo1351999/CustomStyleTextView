package com.style.text.lib

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.TranslateAnimation
import android.widget.FrameLayout
import android.widget.TextView
import com.style.text.lib.glitich.FontCache

class GlitchTextEffect @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    private val colors = mutableListOf<Int>()
    private var noise = 10
    private var text: String = ""
    private val textViews = mutableListOf<TextView>()
    private var animationSet: AnimationSet? = null
    private var speed = 70
    private var reverse = false
    private var textSize = 50
    private var fontFile = "font/poppins_black"
    private var gravity = Gravity.CENTER

    init {
        if (attrs != null) {
            loadStateFromAttrs(attrs)
        }
        setLayerType(LAYER_TYPE_HARDWARE, null)
    }

    private fun loadStateFromAttrs(attributeSet: AttributeSet) {
        val a: TypedArray = context.obtainStyledAttributes(attributeSet, R.styleable.GlitchTextEffect)
        try {
            noise = a.getInteger(R.styleable.GlitchTextEffect_noise, noise)
            textSize = a.getInteger(R.styleable.GlitchTextEffect_textSize, textSize)
            speed = a.getInteger(R.styleable.GlitchTextEffect_speed, speed)
            fontFile = a.getString(R.styleable.GlitchTextEffect_fontLocation) ?: fontFile
            text = a.getString(R.styleable.GlitchTextEffect_text) ?: ""
            if (text.isEmpty()) throw IllegalArgumentException("Text not specified, please include app:text attribute")

            val colorsResId = a.getResourceId(R.styleable.GlitchTextEffect_textColors, 0)
            if (colorsResId == 0) throw IllegalArgumentException("Colors not specified, please include app:textColors")

            val ta = resources.obtainTypedArray(colorsResId)
            for (i in 0 until ta.length()) {
                val color = Color.parseColor(ta.getString(i))
                colors.add(color)
            }
            ta.recycle()

            start()
        } finally {
            a.recycle()
        }
    }

    fun start() {
        init()
    }

    fun setTextSize(textSize: Int) {
        this.textSize = textSize
    }

    fun setFontFile(fontFile: String) {
        this.fontFile = fontFile
    }

    fun setGravity(gravity: Int) {
        this.gravity = gravity
    }

    fun setNoise(noise: Int) {
        this.noise = noise
    }

    fun setSpeed(speed: Int) {
        this.speed = speed
    }

    private fun init() {
        colors.forEachIndexed { index, color ->
            val textView = createTextView(color)
            addView(textView)
            textViews.add(textView)
            if (index + 1 != colors.size) {
                animate(textView, noise + (index / 2 * 2))
            }
        }
    }

    private fun createTextView(color: Int): TextView {
        return TextView(context).apply {
            gravity = this@GlitchTextEffect.gravity
            typeface = FontCache.get(fontFile, context)
            setTextColor(color)
            text = this@GlitchTextEffect.text
            setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize.toFloat())
        }
    }

    private fun animate(textView: TextView, noise: Int) {
        animationSet = AnimationSet(false).apply {
            val animations = listOf(
                createTranslateAnimation(0, reverse, noise),
                createTranslateAnimation(speed.toLong(), !reverse, noise * 2),
                createTranslateAnimation(2 * speed.toLong(), !reverse, noise * 2),
                createTranslateAnimation(3 * speed.toLong(), reverse, noise)
            )
            animations.forEach { addAnimation(it) }

            interpolator = AccelerateDecelerateInterpolator()
            setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {}
                override fun onAnimationEnd(animation: Animation) {
                    animate(textView, noise)
                }
                override fun onAnimationRepeat(animation: Animation) {}
            })
        }
        textView.startAnimation(animationSet)
        reverse = !reverse
    }

    private fun createTranslateAnimation(
        startOffset: Long,
        reverse: Boolean,
        noise: Int
    ): TranslateAnimation {
        return TranslateAnimation(
            0, 0f,
            TranslateAnimation.ABSOLUTE, if (reverse) noise.toFloat() else -noise.toFloat(),
            0, 0f,
            TranslateAnimation.ABSOLUTE, if (reverse) -noise.toFloat() else noise.toFloat()
        ).apply {
            this.startOffset = startOffset
            duration = speed.toLong()
        }
    }
}