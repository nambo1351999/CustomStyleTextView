package com.style.text.lib

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.util.AttributeSet
import android.view.animation.LinearInterpolator
import android.widget.TextView
import androidx.core.graphics.ColorUtils
import kotlin.math.sin


enum class GradientType(val value: Int) {
    SLIDING(1),
    FADING(2),
    RAINBOW(3),
    PULSATING(4),
    WAVE(5),
    SCROLLING(6)
}

enum class TitleMode(val value: Int) {
    CLAMP(1),
    REPEAT(2),
    MIRROR(3)
}

@SuppressLint("AppCompatCustomView", "Recycle")
class GradientTextView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : TextView(context, attrs, defStyleAttr) {
    var colors = intArrayOf(
        Color.parseColor("#F97C3C"),
        Color.parseColor("#FDB54E"),
        Color.parseColor("#64B678"),
        Color.parseColor("#478AEA"),
        Color.parseColor("#8446CC")
    )
        set(value) {
            field = value
            invalidate()
        }

    var duration: Int = 3000
        set(value) {
            field = value
            invalidate()
        }

    var repeatCountValue = ValueAnimator.INFINITE
        set(value) {
            field = value
            invalidate()
        }

    private val width = paint.measureText(text.toString())

    var titleMode = Shader.TileMode.CLAMP
        set(value) {
            field = value
            invalidate()
        }

    init {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.GradientTextView)
        val isAnimation = ta.getBoolean(R.styleable.GradientTextView_isAnimation, false)
        val gradientTypeValue = ta.getInt(R.styleable.GradientTextView_type_gradient, 1)
        duration = ta.getInteger(R.styleable.GradientTextView_duration, 3000)
        val gradientType = GradientType.entries.firstOrNull { it.value == gradientTypeValue }
            ?: GradientType.SLIDING

        val tileModeValue = ta.getInt(R.styleable.GradientTextView_title_mode, 1)
        titleMode = toShaderTileMode((TitleMode.entries.firstOrNull { it.value == tileModeValue }
            ?: TitleMode.CLAMP))

        if (isAnimation) {
            applyGradientAnimation(gradientType)
        } else {
            paint.shader = LinearGradient(0F, 0F, width, textSize, colors, null, titleMode)
        }
    }

    private fun toShaderTileMode(type: TitleMode): Shader.TileMode {
        return when (type) {
            TitleMode.CLAMP -> Shader.TileMode.CLAMP
            TitleMode.REPEAT -> Shader.TileMode.REPEAT
            else -> Shader.TileMode.MIRROR
        }
    }

    private fun applyGradientAnimation(type: GradientType) {
        val animator = when (type) {
            GradientType.SLIDING -> {
                ValueAnimator.ofFloat(0f, width).apply {
                    duration = duration
                    repeatCount = repeatCountValue
                    interpolator = LinearInterpolator()
                    addUpdateListener { animation ->
                        val animatedValue = animation.animatedValue as Float
                        val textShader = LinearGradient(
                            animatedValue, 0f, animatedValue + width, textSize,
                            colors, null, titleMode
                        )
                        paint.shader = textShader
                        invalidate()
                    }
                }
            }

            GradientType.SCROLLING -> {
                ValueAnimator.ofFloat(0f, width * 2).apply {
                    duration = duration
                    repeatCount = repeatCountValue
                    interpolator = LinearInterpolator()
                    addUpdateListener { animation ->
                        val offset = animation.animatedValue as Float
                        val textShader = LinearGradient(
                            offset - width, 0f, offset, textSize,
                            colors, null, titleMode
                        )
                        paint.shader = textShader
                        invalidate()
                    }
                }
            }

            GradientType.FADING -> {
                ValueAnimator.ofFloat(0.3f, 1f).apply {
                    duration = duration
                    repeatMode = ValueAnimator.REVERSE
                    repeatCount = repeatCountValue
                    addUpdateListener { animation ->
                        val alpha = (animation.animatedValue as Float)
                        val fadedColors = colors.map { color ->
                            ColorUtils.setAlphaComponent(color, (alpha * 255).toInt())
                        }.toIntArray()

                        paint.shader = LinearGradient(
                            0f,
                            0f,
                            width,
                            textSize,
                            fadedColors,
                            null,
                            titleMode
                        )
                        invalidate()
                    }
                }
            }

            GradientType.RAINBOW -> {
                ValueAnimator.ofFloat(0f, 1f).apply {
                    duration = duration
                    repeatCount = repeatCountValue
                    interpolator = LinearInterpolator()
                    addUpdateListener { animation ->
                        val progress = animation.animatedValue as Float
                        val shiftedColors = colors.mapIndexed { index, _ ->
                            colors[(index + (progress * colors.size).toInt()) % colors.size]
                        }.toIntArray()

                        paint.shader = LinearGradient(
                            0f,
                            0f,
                            width,
                            textSize,
                            shiftedColors,
                            null,
                            titleMode
                        )
                        invalidate()
                    }
                }
            }

            GradientType.PULSATING -> {
                ValueAnimator.ofFloat(0.8f, 1.2f).apply {
                    duration = duration
                    repeatMode = ValueAnimator.REVERSE
                    repeatCount = repeatCountValue
                    addUpdateListener { animation ->
                        val scale = animation.animatedValue as Float
                        val textShader = LinearGradient(
                            0f, 0f, width * scale, textSize * scale,
                            colors, null, titleMode
                        )
                        paint.shader = textShader
                        invalidate()
                    }
                }
            }

            GradientType.WAVE -> {
                ValueAnimator.ofFloat(0f, Math.PI.toFloat() * 2).apply {
                    duration = duration
                    repeatCount = repeatCountValue
                    interpolator = LinearInterpolator()
                    addUpdateListener { animation ->
                        val angle = animation.animatedValue as Float
                        val offset = (sin(angle) * width / 4)

                        val textShader = LinearGradient(
                            offset, 0f, offset + width, textSize,
                            colors, null, titleMode
                        )
                        paint.shader = textShader
                        invalidate()
                    }
                }
            }
        }

        animator.start()
    }
}