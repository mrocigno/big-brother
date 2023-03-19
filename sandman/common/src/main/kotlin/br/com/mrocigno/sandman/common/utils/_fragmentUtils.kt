package br.com.mrocigno.sandman.common.utils

import android.content.res.ColorStateList
import android.widget.FrameLayout
import androidx.annotation.ColorRes
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner

fun Fragment.getColor(@ColorRes res: Int) =
    ContextCompat.getColor(requireContext(), res)

fun Fragment.getColorState(@ColorRes res: Int) =
    ColorStateList.valueOf(getColor(res))

fun Lifecycle.onStart(onStart: () -> Unit) {
    addObserver(object : DefaultLifecycleObserver {
        override fun onStart(owner: LifecycleOwner) {
            onStart()
            removeObserver(this)
        }
    })
}

fun Lifecycle.onStop(onStop: () -> Unit) {
    addObserver(object : DefaultLifecycleObserver {
        override fun onStop(owner: LifecycleOwner) {
            onStop()
            removeObserver(this)
        }
    })
}

val Fragment.decorView: FrameLayout? get() =
    if (this is AppCompatDialogFragment) {
        dialog?.window?.decorView as? FrameLayout
    } else null