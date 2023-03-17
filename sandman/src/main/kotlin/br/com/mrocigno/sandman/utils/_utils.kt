package br.com.mrocigno.sandman.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import br.com.mrocigno.sandman.BuildConfig
import br.com.mrocigno.sandman.R
import java.io.Serializable

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

internal inline fun <reified T : Serializable> Intent.getSerializableExtraCompat(key: String): T? = when {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> getSerializableExtra(key, T::class.java)
    else -> @Suppress("DEPRECATION") getSerializableExtra(key) as? T?
}

internal inline fun <reified T : Serializable> Bundle.getSerializableExtraCompat(key: String): T? = when {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> getSerializable(key, T::class.java)
    else -> @Suppress("DEPRECATION") getSerializable(key) as? T?
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

internal fun String.highlightStacktrace(context: Context): CharSequence {
    val result = SpannableStringBuilder(this)
    val highlightColor = context.getColor(R.color.text_highlight)
    val linkColor = context.getColor(R.color.text_hyperlink)

    findCause { start, end ->
        result.setSpan(
            ForegroundColorSpan(highlightColor),
            start, end,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }

    findAppClass { start, end ->
        result.setSpan(
            ForegroundColorSpan(linkColor),
            start, end,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }
    return result
}

private fun String.findCause(each: (Int, Int) -> Unit) {
    val matcher = "Caused by:(.*)".toPattern().matcher(this)
    while (matcher.find()) {
        each.invoke(matcher.start(), matcher.end())
    }
}

private fun String.findAppClass(each: (Int, Int) -> Unit) {
    val packageId = BuildConfig.LIBRARY_PACKAGE_NAME
    val matcher = "(at $packageId)(.*)".toPattern().matcher(this)
    while (matcher.find()) {
        val text = matcher.group()
        val classMatcher = "\\((.*?)\\)".toPattern().matcher(text)

        val matcherStart = matcher.start()
        while (classMatcher.find()) {
            val classStart = matcherStart + 1 + classMatcher.start()
            val classEnd = matcherStart - 1 + classMatcher.end()
            each.invoke(classStart, classEnd)
        }
    }
}

internal fun RecyclerView.disableChangeAnimation() {
    (itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations = false
}

internal fun View.afterMeasure(action: (View) -> Unit) {
    this.viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            viewTreeObserver.removeOnGlobalLayoutListener(this)
            action(this@afterMeasure)
        }
    })
}