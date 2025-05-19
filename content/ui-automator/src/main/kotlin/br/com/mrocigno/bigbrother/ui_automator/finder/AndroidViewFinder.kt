package br.com.mrocigno.bigbrother.ui_automator.finder

import android.graphics.Rect
import android.view.View

class AndroidViewFinder(val view: View) : ViewFinder {

    override val rect: Rect = Rect()

    override val name: String? = runCatching { view.resources.getResourceEntryName(view.id) }.getOrNull()

    override fun click() {
        view.performClick()
    }

    override fun equals(other: Any?): Boolean =
        other is AndroidViewFinder && view == other.view

    override fun hashCode(): Int = view.hashCode()

    init {
        view.getGlobalVisibleRect(rect)
    }
}