package br.com.mrocigno.sandman.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.res.Configuration
import android.view.KeyCharacterMap
import android.view.KeyEvent
import android.view.ViewConfiguration
import android.widget.FrameLayout
import br.com.mrocigno.sandman.R

internal val Activity.rootView: FrameLayout get() = window.decorView as FrameLayout

internal val Activity.statusBarHeight: Int
    @SuppressLint("DiscouragedApi", "InternalInsetResource")
    get() {
        val resId = resources.getIdentifier("status_bar_height", "dimen", "android")
        return if (resId > 0) resources.getDimensionPixelSize(resId)
        else resources.getDimensionPixelSize(R.dimen.spacing_l)
    }

internal fun Activity.getNavigationBarHeight(force: Boolean = false): Int {
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