package br.com.mrocigno.bigbrother.ui_automator.finder

import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import br.com.mrocigno.bigbrother.ui_automator.UiAutomatorView

interface ViewFinder {

    val rect: Rect

    val name: String?

    fun click()

    companion object {

        fun fromCoordinates(x: Float, y: Float, root: View): ViewFinder? {
            if (root is UiAutomatorView) return null
            val rect = Rect()
            if (!root.getGlobalVisibleRect(rect) || !rect.contains(x.toInt(), y.toInt())) return null
            when (root) {
                is ComposeView -> return ComposableFinder_133(x, y, root)
                is ViewGroup -> {
                    for (i in 0 until root.childCount) {
                        val child = root.getChildAt(i)
                        val found = fromCoordinates(x, y, child)
                        if (found != null) return found
                    }
                }
            }

            return AndroidViewFinder(root)
        }
    }
}