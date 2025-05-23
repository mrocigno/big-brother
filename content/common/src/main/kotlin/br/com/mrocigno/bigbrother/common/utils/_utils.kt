package br.com.mrocigno.bigbrother.common.utils

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
import br.com.mrocigno.bigbrother.common.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.ObjectOutputStream
import java.io.Serializable
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.reflect.KClass
import kotlin.reflect.cast

fun ViewGroup.inflate(@LayoutRes resId: Int, attachToRoot: Boolean = false) =
    LayoutInflater.from(context).inflate(resId, this, attachToRoot)

fun <T : Any> Any.cast(clazz: KClass<T>): T {
    return clazz.cast(this)
}

fun Context.copyToClipboard(text: String, toastFeedback: String? = "Copied to clipboard") {
    val clipboard = getSystemService(ClipboardManager::class.java)
    clipboard.setPrimaryClip(ClipData.newPlainText("value", text))
    toastFeedback?.run {
        Toast.makeText(this@copyToClipboard, toastFeedback, Toast.LENGTH_SHORT).show()
    }
}

inline fun <reified T> Intent.getParcelableExtraCompat(key: String): T? = when {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> getParcelableExtra(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelableExtra(key) as? T?
}

inline fun <reified T> Bundle.getParcelableCompat(key: String): T? = when {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> getParcelable(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelable(key) as? T?
}

inline fun <reified T : Serializable> Intent.getSerializableExtraCompat(key: String): T? = when {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> getSerializableExtra(key, T::class.java)
    else -> @Suppress("DEPRECATION") getSerializableExtra(key) as? T?
}

inline fun <reified T : Serializable> Bundle.getSerializableCompat(key: String): T? = when {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> getSerializable(key, T::class.java)
    else -> @Suppress("DEPRECATION") getSerializable(key) as? T?
}

fun CharSequence.highlightQuery(query: String, @ColorInt color: Int = Color.YELLOW): CharSequence {
    val index = indexOf(query, ignoreCase = true)
    return if (index < 0) this
    else SpannableStringBuilder(this).apply {
        if (index + query.length > 0) setSpan(
            ForegroundColorSpan(color),
            index, index + query.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }
}

fun String.highlightStacktrace(context: Context): CharSequence {
    val result = SpannableStringBuilder(this)
    val highlightColor = context.getColor(R.color.bb_text_highlight)
    val linkColor = context.getColor(R.color.bb_text_hyperlink)

    findCause { start, end ->
        result.setSpan(
            ForegroundColorSpan(highlightColor),
            start, end,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }

    findAppClass(context) { start, end ->
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

private fun String.findAppClass(context: Context, each: (Int, Int) -> Unit) {
    val packageId = context.packageName
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

fun RecyclerView.disableChangeAnimation() {
    (itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations = false
}

fun View.afterMeasure(action: (View) -> Unit) {
    this.viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            viewTreeObserver.removeOnGlobalLayoutListener(this)
            action(this@afterMeasure)
        }
    })
}

fun <T> MutableList<T>.update(model: T) {
    val index = indexOf(model).takeIf { it != -1 } ?: return
    removeAt(index)
    add(index, model)
}

fun Any?.canBeSerialized(): Boolean = runCatching {
    if (this !is Serializable) return false
    "".apply {  }
    ObjectOutputStream(ByteArrayOutputStream())
        .use { it.writeObject(this) }
        .let { true }
}.getOrElse { false }

inline fun <T> T.applyScoped(scope: CoroutineScope, crossinline block: suspend T.() -> Unit): T {
    scope.launch {
        block()
    }
    return this
}

@OptIn(ExperimentalContracts::class)
inline fun <T> T.applyIf(condition: Boolean, block: T.() -> Unit): T {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    if (condition) block()
    return this
}