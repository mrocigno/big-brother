package br.com.mrocigno.bigbrother.common.utils

import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.RotateDrawable
import androidx.annotation.ColorInt

fun Drawable.setColor(@ColorInt color: Int) {
    when (this) {
        is RotateDrawable -> drawable?.setColor(color)
        is GradientDrawable -> setColor(color)
    }
}