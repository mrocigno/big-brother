package br.com.mrocigno.bigbrother.ui_automator.finder

import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import br.com.mrocigno.bigbrother.ui_automator.R
import br.com.mrocigno.bigbrother.core.R as CoreR
import com.google.android.material.R as MaterialR

interface ViewFinder {

    val rect: Rect
    val identifier: String
    val name: String?
    val scrollX: Float
    val scrollY: Float
    val hasClickAction: Boolean
    val hasLongClickAction: Boolean
    val isTextField: Boolean
    val isScrollable: Boolean
    val parent: ViewFinder?
    fun click()
    fun longClick()
    fun setText(text: String)
    fun scroll(x: Float, y: Float, exactly: Boolean)

    companion object {

        private fun blockListBecauseGoogleHateAllOfUs(view: View): Boolean = when (view.id) {
            CoreR.id.bigbrother_click_observer,
            R.id.bigbrother_ui_automator,
            MaterialR.id.textinput_placeholder -> true
            else -> false
        }

        fun fromCoordinates(x: Float, y: Float, root: View): ViewFinder? {
            if (blockListBecauseGoogleHateAllOfUs(root)) return null
            val rect = Rect()
            if (!root.getGlobalVisibleRect(rect) || !rect.contains(x.toInt(), y.toInt())) return null
            when (root) {
                is ComposeView -> return ComposableFinder_133(x, y, root)
                is ViewGroup -> {
                    var found: ViewFinder = AndroidViewFinder(root)
                    for (i in 0 until root.childCount) {
                        val child = root.getChildAt(i)
                        found = fromCoordinates(x, y, child) ?: found
                    }
                    return found
                }
            }

            return AndroidViewFinder(root)
        }

        fun fromXPath(xPath: String, root: View): ViewFinder = when (root) {
            is ComposeView -> ComposableFinder_133(root, xPath)
            else -> AndroidViewFinder(root)
        }
    }
}