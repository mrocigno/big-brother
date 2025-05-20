package br.com.mrocigno.bigbrother.ui_automator

import android.view.View
import android.view.ViewGroup
import br.com.mrocigno.bigbrother.core.BigBrother
import br.com.mrocigno.bigbrother.core.BigBrotherProvider
import br.com.mrocigno.bigbrother.ui_automator.ui.UiAutomatorFragment

fun BigBrotherProvider.addUiAutomatorPage(customName: String = "UiAutomator") {
    addPage(customName) { UiAutomatorFragment() }
}

fun BigBrother.addUiAutomatorPage(customName: String = "UiAutomator") {
    addPage(customName) { UiAutomatorFragment() }
}

internal fun View.getEntryName() = runCatching { resources.getResourceEntryName(id) }
    .getOrNull()

internal fun View.getXpath() = buildString {
    var current: View? = this@getXpath

    while (current != null) {
        val parent = current.parent as? ViewGroup
        val index = parent?.let { p ->
            (0 until p.childCount).indexOfFirst { i -> p.getChildAt(i) == current }
        } ?: 0

        val currentId = current.getEntryName()
        val isUniqueId = isIdUnique(currentId ?: "")

        insert(0, "/${current.javaClass.simpleName}${if (currentId != null) "[@id='$currentId']" else "[$index]"}")
        current = if (isUniqueId) null else parent

        if (current?.id == android.R.id.content) break
    }
}

internal fun View.isIdUnique(id: String): Boolean {
    val rootView = rootView as ViewGroup
    return findViewsWithId(rootView, id).size == 1
}

private fun findViewsWithId(root: ViewGroup, id: String): List<View> {
    val views = mutableListOf<View>()
    for (i in 0 until root.childCount) {
        val child = root.getChildAt(i)
        if (runCatching { child.resources.getResourceEntryName(child.id) }.getOrNull() == id) views.add(child)
        if (child is ViewGroup) views.addAll(findViewsWithId(child, id))
    }
    return views
}