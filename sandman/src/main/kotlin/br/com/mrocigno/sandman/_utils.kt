package br.com.mrocigno.sandman

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.KeyCharacterMap
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator

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

internal fun ViewGroup.inflate(@LayoutRes resId: Int) =
    LayoutInflater.from(context).inflate(resId, this, false)

internal fun Context.copyToClipboard(text: String, toastFeedback: String? = null) {
    val clipboard = getSystemService(ClipboardManager::class.java)
    clipboard.setPrimaryClip(ClipData.newPlainText("value", text))
    toastFeedback?.run {
        Toast.makeText(this@copyToClipboard, toastFeedback, Toast.LENGTH_SHORT).show()
    }
}

internal inline fun <reified T> Intent.getParcelableExtraCompat(key: String): T? = when {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> getParcelableExtra(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelableExtra(key) as? T?
}

internal inline fun <reified T> Bundle.getParcelableExtraCompat(key: String): T? = when {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> getParcelable(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelable(key) as? T?
}

internal fun String.highlightQuery(query: String, @ColorInt color: Int = Color.YELLOW): CharSequence {
    val index = indexOf(query, ignoreCase = true)
    return if (index < 0) this
    else SpannableStringBuilder(this).apply {
        setSpan(
            ForegroundColorSpan(color),
            index, index + query.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }
}

internal fun RecyclerView.disableChangeAnimation() {
    (itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations = false
}