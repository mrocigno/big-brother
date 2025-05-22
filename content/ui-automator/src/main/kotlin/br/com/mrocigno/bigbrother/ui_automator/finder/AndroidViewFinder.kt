package br.com.mrocigno.bigbrother.ui_automator.finder

import android.graphics.Rect
import android.view.View
import android.widget.EditText
import br.com.mrocigno.bigbrother.ui_automator.getEntryName
import br.com.mrocigno.bigbrother.ui_automator.getXpath
import br.com.mrocigno.bigbrother.ui_automator.isIdUnique

class AndroidViewFinder(val view: View) : ViewFinder {

    override val rect: Rect = Rect().apply {
        view.getGlobalVisibleRect(this)
    }

    override val name: String =
        view.getEntryName()?.takeIf { view.isIdUnique(it) } ?: identifier

    override val hasClickAction: Boolean
        get() = view.isClickable

    override val hasLongClickAction: Boolean
        get() = view.isLongClickable

    override val isTextField: Boolean
        get() = view is EditText

    override fun click() {
        view.performClick()
    }

    override fun longClick() {
        view.performLongClick()
    }

    override fun setText(text: String) = when (view) {
        is EditText -> view.setText(text)
        else -> throw IllegalStateException("View is not editable")
    }

    override val identifier: String get() = view.getXpath()

    override fun equals(other: Any?): Boolean =
        other is AndroidViewFinder && view == other.view

    override fun hashCode(): Int = view.hashCode()

}