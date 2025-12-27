package br.com.mrocigno.bigbrother.ui_automator.finder

import android.content.Context
import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.compose.ui.platform.ComposeView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import br.com.mrocigno.bigbrother.core.R
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.withTimeout

internal fun View.getEntryName() = runCatching {
    id.takeIf { it > 0 }?.run(resources::getResourceEntryName)
}.getOrNull()

internal fun View.getXpath() = buildString {
    var current: View? = this@getXpath

    while (current != null) {
        val parent = current.parent as? ViewGroup
        val index = when (parent) {
            is RecyclerView -> parent.getChildAdapterPosition(current)
            else -> parent?.indexOfChild(current)
        }

        val currentId = current.getEntryName()
        val isUniqueId = isIdUnique(currentId)

        insert(0, "/${current.javaClass.simpleName}${if (isUniqueId) "[@id='$currentId']" else "[$index]"}")
        current = if (isUniqueId) null else parent

        if (current?.id == android.R.id.content) break
    }
}

internal fun View.isIdUnique(id: String?): Boolean {
    id ?: return false
    val rootView = rootView as ViewGroup
    return findViewsWithId(rootView, id).size == 1
}

internal fun findViewsWithId(root: ViewGroup, id: String): List<View> {
    val views = mutableListOf<View>()
    for (i in 0 until root.childCount) {
        val child = root.getChildAt(i)
        if (runCatching { child.getEntryName() }.getOrNull() == id) views.add(child)
        if (child is ViewGroup) views.addAll(findViewsWithId(child, id))
    }
    return views
}

internal suspend fun View.findViewByXPath(xpath: String, timeout: Long = 10000): View? {
    val pathSegments = xpath.split("/").filter { it.isNotEmpty() }
    var currentView: View? = this

    for (segment in pathSegments) {
        val condition = segment.substringAfter("[").substringBefore("]")

        currentView = if (condition.startsWith("@id='")) {
            val idString = condition.substringAfter("@id='").substringBeforeLast("'")
            val id = context.getResIdByName(idString) ?: return null

            currentView.awaitView(timeout) { currentView?.findViewById(id) }
        } else {
            val index = condition.toIntOrNull() ?: 0

            currentView.awaitView(timeout) { currentView?.getChildAt(index) }
        }

        if (currentView is ComposeView) break
    }
    return currentView
}

private fun View.getChildAt(index: Int): View? = when (this) {
    is RecyclerView -> findViewHolderForAdapterPosition(index)?.itemView
    is ViewGroup -> getChildAt(index)
    else -> null
}

internal fun View.findViewByCoordinates(x: Float, y: Float): View? {
    if (blockListBecauseGoogleHateAllOfUs(this)) return null
    val rect = Rect()
    if (!getGlobalVisibleRect(rect) || !rect.contains(x.toInt(), y.toInt())) return null
    return when (this) {
        is ComposeView -> this
        is ViewGroup -> {
            var found = this
            for (i in 0 until childCount) {
                val child = getChildAt(i)
                found = child.findViewByCoordinates(x, y) ?: found
            }
            found
        }
        else -> this
    }
}

internal fun List<String>.trimTill(target: String) =
    takeLastWhile { !it.contains(target) }

private suspend fun View?.awaitView(timeout: Long, block: () -> View?): View? {
    this ?: return null
    block.invoke()
        ?.takeIf { it.isViewReady }
        ?.run { return this }

    val completableView = CompletableDeferred<View?>()
    var listener: ViewTreeObserver.OnGlobalLayoutListener? = null
    val observer = viewTreeObserver
    if (observer.isAlive) {
        listener = ViewTreeObserver.OnGlobalLayoutListener {
            block.invoke()
                ?.takeIf { it.isViewReady }
                ?.run(completableView::complete)
                ?.also {
                    observer.takeIf { it.isAlive }
                        ?.removeOnGlobalLayoutListener(listener)
                }
        }
        observer.addOnGlobalLayoutListener(listener)
    } else {
        completableView.complete(null)
    }

    return withTimeout(timeout) { completableView.await() }
}

private val View.isViewReady: Boolean
    get() = isVisible && width > 0 && height > 0

private fun Context.getResIdByName(name: String): Int? =
    runCatching {
        val rClass = Class.forName("${packageName}.R\$id")
        rClass.getDeclaredField(name).getInt(null)
    }.getOrNull()

private fun blockListBecauseGoogleHateAllOfUs(view: View): Boolean = when (view.id) {
    R.id.bigbrother_click_observer,
    br.com.mrocigno.bigbrother.ui_automator.R.id.bigbrother_ui_automator,
    com.google.android.material.R.id.textinput_placeholder -> true
    else -> false
}
