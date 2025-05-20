package br.com.mrocigno.bigbrother.ui_automator.finder

import android.graphics.Rect
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.toAndroidRect
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsNode
import androidx.compose.ui.semantics.SemanticsOwner
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getAllSemanticsNodes
import androidx.compose.ui.semantics.getOrNull
import br.com.mrocigno.bigbrother.common.utils.field
import br.com.mrocigno.bigbrother.ui_automator.getXpath

class ComposableFinder_133(
    x: Float,
    y: Float,
    val view: ComposeView
) : ViewFinder {

    override val rect: Rect = Rect()

    override val name: String get() = getXpath()

    override val identifier: String
        get() = selectedNode.getTestTag()?.takeIf { isIdUnique(it) } ?: getXpath()

    override val hasClickAction: Boolean
        get() = selectedNode?.config?.getOrNull(SemanticsActions.OnClick) != null

    override val hasLongClickAction: Boolean
        get() = selectedNode?.config?.getOrNull(SemanticsActions.OnLongClick) != null

    private val owner: SemanticsOwner? = view
        .field<Any>("composition")
        .field<Any>("owner")
        .field("semanticsOwner")

    internal var selectedNode: SemanticsNode? = null

    init {
        selectedNode = findViewAtCoordinates(x, y, owner?.rootSemanticsNode)
        selectedNode?.boundsInWindow?.toAndroidRect()?.let { rect.set(it) }
    }

    override fun click() {
        val clickAction = selectedNode
            ?.config
            ?.getOrNull(SemanticsActions.OnClick)

        clickAction?.action?.invoke()
    }

    override fun longClick() {
        val clickAction = selectedNode
            ?.config
            ?.getOrNull(SemanticsActions.OnLongClick)

        clickAction?.action?.invoke()
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

    private fun SemanticsNode?.getTestTag() =
        this?.config?.getOrNull(SemanticsProperties.TestTag)

    private fun SemanticsNode?.getRole() =
        this?.config?.getOrNull(SemanticsProperties.Role)?.toString() ?: "node"

    private fun isIdUnique(id: String): Boolean {
        val nodes = owner?.getAllSemanticsNodes(true).orEmpty()
            .filter { it.getTestTag() == id }
        return nodes.size == 1
    }

    private fun getXpath() = buildString {
        var current: SemanticsNode? = selectedNode

        while (current != null) {
            val parent = current.parent
            val index = parent?.let { p ->
                (0 until p.children.size).indexOfFirst { i -> p.children[i].id == current?.id }
            } ?: 0

            val currentId = current.getTestTag()
            val isUniqueId = isIdUnique(currentId ?: "")

            insert(0, "/${current.getRole()}${if (currentId != null) "[@id='$currentId']" else "[$index]"}")
            current = if (isUniqueId) null else parent

            if (current?.id == 1) break
        }

        insert(0, view.getXpath())
    }

    override fun equals(other: Any?): Boolean =
        other is ComposableFinder_133 && selectedNode?.id == other.selectedNode?.id

    override fun hashCode(): Int = selectedNode.hashCode()
}