package br.com.mrocigno.bigbrother.ui_automator.finder

import android.graphics.Rect
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.toAndroidRect
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsNode
import androidx.compose.ui.semantics.SemanticsOwner
import androidx.compose.ui.semantics.getOrNull
import br.com.mrocigno.bigbrother.common.utils.field

class ComposableFinder_133(
    x: Float,
    y: Float,
    val view: ComposeView
) : ViewFinder {

    override val rect: Rect = Rect()

    override val name: String get() = "TODO()"

    private val owner: SemanticsOwner? = view
        .field<Any>("composition")
        .field<Any>("owner")
        .field("semanticsOwner")

    internal var selectedNode: SemanticsNode? = null

    override fun click() {
        val clickAction = selectedNode
            ?.config
            ?.getOrNull(SemanticsActions.OnClick)

        clickAction?.action?.invoke()
    }

    init {
        selectedNode = findViewAtCoordinates(x, y, owner?.rootSemanticsNode)
        selectedNode?.boundsInWindow?.toAndroidRect()?.let { rect.set(it) }
    }

    private fun findViewAtCoordinates(x: Float, y: Float, root: SemanticsNode?): SemanticsNode? {
        root ?: return null
        val rect = root.boundsInWindow
        if (!rect.contains(Offset(x, y))) return null

        val children = root.children

        children.forEach { child ->
            val found = findViewAtCoordinates(x, y, child)
            if (found != null) return found
        }

        return root
    }

    override fun equals(other: Any?): Boolean =
        other is ComposableFinder_133 && selectedNode?.id == other.selectedNode?.id

    override fun hashCode(): Int = selectedNode.hashCode()
}