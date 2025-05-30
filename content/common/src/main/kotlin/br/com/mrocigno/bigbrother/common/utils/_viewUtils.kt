package br.com.mrocigno.bigbrother.common.utils

import android.graphics.RectF
import android.view.View
import android.view.ViewTreeObserver
import android.widget.HorizontalScrollView
import android.widget.ScrollView
import androidx.core.view.ScrollingView
import androidx.core.view.isGone
import androidx.core.view.isVisible

fun View.onMeasured(block: View.() -> Unit) {
    val callback = object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            block.invoke(this@onMeasured)
            viewTreeObserver.removeOnGlobalLayoutListener(this)
        }
    }

    viewTreeObserver.addOnGlobalLayoutListener(callback)
}

fun View.getBounds() = RectF(
    x,
    y,
    x + width,
    y + height
)

fun View.gone() {
    isGone = true
}

fun View.visible() {
    isVisible = true
}

val View.isScrollable get() = when (this) {
    is ScrollView,
    is ScrollingView,
    is HorizontalScrollView -> true
    else -> false
}

val View.scrollableParent: View?
    get() = run {
        takeIf { it.isScrollable } ?: (parent as? View)?.scrollableParent
    }
