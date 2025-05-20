package br.com.mrocigno.bigbrother.common.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.view.KeyCharacterMap
import android.view.KeyEvent
import android.view.ViewConfiguration
import android.widget.FrameLayout
import br.com.mrocigno.bigbrother.common.R

val Activity.rootView: FrameLayout get() = window.decorView as FrameLayout

val Context.rootView: FrameLayout? get() = (this as? Activity)?.rootView

val Activity.statusBarHeight: Int
    @SuppressLint("DiscouragedApi", "InternalInsetResource")
    get() {
        val resId = resources.getIdentifier("status_bar_height", "dimen", "android")
        return if (resId > 0) resources.getDimensionPixelSize(resId)
        else resources.getDimensionPixelSize(R.dimen.bb_spacing_l)
    }

fun Activity.getNavigationBarHeight(force: Boolean = false): Int {
    val hasMenuKey = ViewConfiguration.get(this).hasPermanentMenuKey()
    val hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK)

    if ((!hasMenuKey && !hasBackKey) || force) {
        val orientation =
            if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) "navigation_bar_height"
            else "navigation_bar_width"
        val resId = resources.getIdentifier(orientation, "dimen", "android")
        if (resId > 0) return resources.getDimensionPixelSize(resId)
    }
    return 0
}