package br.com.mrocigno.bigbrother.ui_automator.finder

import android.graphics.Rect
import android.view.View
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import br.com.mrocigno.bigbrother.common.utils.scrollableParent

class AndroidViewFinder(val view: View) : ViewFinder {

    override val rect: Rect =
        Rect().apply { view.getGlobalVisibleRect(this) }

    override val name: String =
        view.getEntryName()?.takeIf { view.isIdUnique(it) } ?: identifier

    override val identifier: String
        get() = view.getXpath()

    override val scrollX: Float
        get() = view.scrollX.toFloat()

    override val scrollY: Float
        get() = when (view) {
            is RecyclerView -> view.computeVerticalScrollOffset().toFloat()
            else -> view.scrollY.toFloat()
        }

    override val hasClickAction: Boolean
        get() = view.isClickable

    override val hasLongClickAction: Boolean
        get() = view.isLongClickable

    override val isTextField: Boolean
        get() = view is EditText

    override val isScrollable: Boolean
        get() = scrollableParent != null

    override val parent: ViewFinder?
        get() = (view.parent as? View)?.let(::AndroidViewFinder)

    override val scrollableParent: ViewFinder?
        get() = view.scrollableParent?.let(::AndroidViewFinder)

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
        if (exactly) view.scrollBy((x - scrollX).toInt(), (y - scrollY).toInt())
        else view.scrollBy(x.toInt(), y.toInt())
    }

    override suspend fun isReady(identifier: String, timeout: Long) = true

    override fun equals(other: Any?): Boolean =
        other is AndroidViewFinder && view == other.view

    override fun hashCode(): Int = view.hashCode()

}
