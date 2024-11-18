package com.style.text.lib.glitich

import android.content.Context
import android.graphics.Typeface
import androidx.core.content.res.ResourcesCompat
import com.style.text.lib.R

object FontCache {

    private val fontCache = mutableMapOf<String, Typeface>()

    fun get(name: String, context: Context): Typeface? {
        return fontCache[name] ?: try {
            ResourcesCompat.getFont(context, R.font.poppins_black)
        } catch (e: Exception) {
            null
        }
    }
}