package br.com.mrocigno.sandman

import android.app.Activity
import android.content.res.Configuration
import android.view.KeyCharacterMap
import android.view.KeyEvent
import android.view.ViewConfiguration
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner

internal val Activity.statusBarHeight: Int get() {
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

internal fun Lifecycle.onStart(onStart: () -> Unit) {
    addObserver(object : DefaultLifecycleObserver {
        override fun onStart(owner: LifecycleOwner) {
            onStart()
            removeObserver(this)
        }
    })
}

internal fun Lifecycle.onStop(onStop: () -> Unit) {
    addObserver(object : DefaultLifecycleObserver {
        override fun onStop(owner: LifecycleOwner) {
            onStop()
            removeObserver(this)
        }
    })
}

internal val Fragment.decorView: FrameLayout? get() =
    if (this is AppCompatDialogFragment) {
        dialog?.window?.decorView as? FrameLayout
    } else null