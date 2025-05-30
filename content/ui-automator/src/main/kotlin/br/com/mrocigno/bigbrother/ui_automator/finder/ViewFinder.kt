package br.com.mrocigno.bigbrother.ui_automator.finder

import android.graphics.Rect
import android.view.View
import androidx.compose.ui.platform.ComposeView

interface ViewFinder {
    val rect: Rect
    val identifier: String
    val name: String?
    val scrollX: Float
    val scrollY: Float
    val hasClickAction: Boolean
    val hasLongClickAction: Boolean
    val isTextField: Boolean
    val hasScrollToAction: Boolean get() = false
    val isScrollable: Boolean
    val scrollableParent: ViewFinder?
    val parent: ViewFinder?
    fun click()
    fun longClick()
    fun setText(text: String)
    fun scroll(x: Float, y: Float, exactly: Boolean)
    suspend fun isReady(identifier: String, timeout: Long): Boolean

    companion object {

        fun fromCoordinates(x: Float, y: Float, root: View): ViewFinder? {
            val view = root.findViewByCoordinates(x, y) ?: return null

            return when (view) {
                is ComposeView -> ComposableFinder133(view, x, y)
                else -> AndroidViewFinder(view)
            }
        }

        fun fromView(root: View): ViewFinder = when (root) {
            is ComposeView -> ComposableFinder133(root)
            else -> AndroidViewFinder(root)
        }
    }
}
