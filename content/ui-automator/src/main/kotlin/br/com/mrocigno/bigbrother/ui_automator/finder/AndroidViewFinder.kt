package br.com.mrocigno.bigbrother.ui_automator.finder

import android.graphics.Rect
import android.view.View
import br.com.mrocigno.bigbrother.ui_automator.getEntryName
import br.com.mrocigno.bigbrother.ui_automator.getXpath
import br.com.mrocigno.bigbrother.ui_automator.isIdUnique

class AndroidViewFinder(val view: View) : ViewFinder {

    override val rect: Rect = Rect().apply {
        view.getGlobalVisibleRect(this)
    }

    override val name: String = identifier

    override val hasClickAction: Boolean
        get() = view.isClickable

    override val hasLongClickAction: Boolean
        get() = view.isLongClickable

    override fun click() {
        view.performClick()
    }

    override fun longClick() {
        view.performLongClick()
    }

    override val identifier: String get() =
        view.getEntryName()?.takeIf { view.isIdUnique(it) } ?: view.getXpath()

    override fun equals(other: Any?): Boolean =
        other is AndroidViewFinder && view == other.view

    override fun hashCode(): Int = view.hashCode()

}