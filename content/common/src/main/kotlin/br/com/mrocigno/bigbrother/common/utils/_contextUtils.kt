package br.com.mrocigno.bigbrother.common.utils

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.core.content.ContextCompat

fun Context.getDrawableFromAttr(@AttrRes attrResId: Int): Drawable? {
    val typedValue = TypedValue()
    if (theme.resolveAttribute(attrResId, typedValue, true)) {
        return ContextCompat.getDrawable(this, typedValue.resourceId)
    }
    return null
}