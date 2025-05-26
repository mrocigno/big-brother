package br.com.mrocigno.bigbrother.ui_automator.finder

import android.graphics.Rect
import android.view.View
import android.widget.EditText
import android.widget.HorizontalScrollView
import android.widget.ScrollView
import androidx.core.view.ScrollingView
import br.com.mrocigno.bigbrother.ui_automator.getEntryName
import br.com.mrocigno.bigbrother.ui_automator.getXpath
import br.com.mrocigno.bigbrother.ui_automator.isIdUnique

class AndroidViewFinder(val view: View) : ViewFinder {

    override val rect: Rect =
        Rect().apply { view.getGlobalVisibleRect(this) }

    override val name: String =
        view.getEntryName()?.takeIf { view.isIdUnique(it) } ?: identifier

    override val scrollX: Float
        get() = view.scrollX.toFloat()

    override val scrollY: Float
        get() = view.scrollY.toFloat()

    override val hasClickAction: Boolean
        get() = view.isClickable

    override val hasLongClickAction: Boolean
        get() = view.isLongClickable

    override val isTextField: Boolean
        get() = view is EditText

    override val isScrollable: Boolean
        get() = when (view) {
            is ScrollView,
            is HorizontalScrollView,
            is ScrollingView -> true
            else -> false
        }

    override val parent: ViewFinder?
        get() = (view.parent as? View)?.let(::AndroidViewFinder)

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

    override fun scroll(x: Float, y: Float, exactly: Boolean) {
        if (exactly) view.scrollTo(x.toInt(), y.toInt())
        else view.scrollBy(x.toInt(), y.toInt())
    }

    override val identifier: String get() = view.getXpath()

    override fun equals(other: Any?): Boolean =
        other is AndroidViewFinder && view == other.view

    override fun hashCode(): Int = view.hashCode()

}