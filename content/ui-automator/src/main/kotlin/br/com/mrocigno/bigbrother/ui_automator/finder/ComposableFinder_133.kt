package br.com.mrocigno.bigbrother.ui_automator.finder

import android.graphics.Rect
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.toAndroidRect
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.semantics.SemanticsActions.OnClick
import androidx.compose.ui.semantics.SemanticsActions.OnLongClick
import androidx.compose.ui.semantics.SemanticsActions.ScrollBy
import androidx.compose.ui.semantics.SemanticsActions.SetText
import androidx.compose.ui.semantics.SemanticsNode
import androidx.compose.ui.semantics.SemanticsOwner
import androidx.compose.ui.semantics.SemanticsProperties.HorizontalScrollAxisRange
import androidx.compose.ui.semantics.SemanticsProperties.Role
import androidx.compose.ui.semantics.SemanticsProperties.TestTag
import androidx.compose.ui.semantics.SemanticsProperties.VerticalScrollAxisRange
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.getAllSemanticsNodes
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.text.AnnotatedString
import br.com.mrocigno.bigbrother.common.utils.field
import br.com.mrocigno.bigbrother.ui_automator.getXpath

class ComposableFinder_133(
    x: Float? = null,
    y: Float? = null,
    val view: ComposeView
) : ViewFinder {

    override val rect: Rect =
        Rect()

    override val identifier: String
        get() = getXpath()

    override val name: String get() =
        selectedNode[TestTag]?.takeIf { isIdUnique(it) } ?: getXpath()

    override val scrollX: Float
        get() = selectedNode[HorizontalScrollAxisRange]?.value?.invoke() ?: 0f

    override val scrollY: Float
        get() = selectedNode[VerticalScrollAxisRange]?.value?.invoke() ?: 0f

    override val hasClickAction: Boolean
        get() = selectedNode[OnClick] != null

    override val hasLongClickAction: Boolean
        get() = selectedNode[OnLongClick] != null

    override val isTextField: Boolean
        get() = selectedNode[SetText] != null

    override val isScrollable: Boolean
        get() = selectedNode[ScrollBy] != null

    override val parent: ViewFinder
        get() = selectedNode?.parent?.let { ComposableFinder_133(view, it) }
            ?: AndroidViewFinder(view)

    private val owner: SemanticsOwner? = view
        .field<Any>("composition")
        .field<Any>("owner")
        .field("semanticsOwner")

    internal var selectedNode: SemanticsNode? = null

    constructor(view: ComposeView, xpath: String) : this(null, null, view) {
        selectedNode = findNodeByXpath(xpath)
    }

    constructor(view: ComposeView, node: SemanticsNode) : this(null, null, view) {
        selectedNode = node
    }

    init {
        if (x != null && y != null) {
            selectedNode = findNodeByCoordinates(x, y, owner?.rootSemanticsNode)
            selectedNode?.boundsInWindow?.toAndroidRect()?.run(rect::set)
        }
    }

    override fun click() {
        selectedNode[OnClick]?.action?.invoke()
    }

    override fun longClick() {
        selectedNode[OnLongClick]?.action?.invoke()
    }

    override fun setText(text: String) {
        selectedNode[SetText]?.action?.invoke(AnnotatedString(text))
    }

    override fun scroll(x: Float, y: Float, exactly: Boolean) {
        if (exactly) selectedNode[ScrollBy]?.action?.invoke(x, y)
        else selectedNode[ScrollBy]?.action?.invoke(x, scrollY - y)
    }

    private fun findNodeByTestTag(testTag: String, root: SemanticsNode?): SemanticsNode? {
        root ?: return null
        if (root[TestTag] == testTag) return root
        val children = root.children
        var found: SemanticsNode? = null
        children.forEach { child ->
            found = findNodeByTestTag(testTag, child) ?: found
        }
        return found
    }

    private fun findNodeByCoordinates(x: Float, y: Float, root: SemanticsNode?): SemanticsNode? {
        root ?: return null
        val rect = root.boundsInWindow
        if (!rect.contains(Offset(x, y))) return null

        val children = root.children

        children.forEach { child ->
            val found = findNodeByCoordinates(x, y, child)
            if (found != null) return found
        }

        return root
    }

    private fun findNodeByXpath(xpath: String): SemanticsNode? {
        val pathSegments = xpath.split("/").filter { it.isNotEmpty() }
        var currentNode: SemanticsNode? = owner?.rootSemanticsNode

        for (segment in pathSegments) {
            val role = segment.substringBefore("[")
            if (role == ComposeView::class.simpleName) continue
            val condition = segment.substringAfter("[").substringBefore("]")

            if (condition.startsWith("@id='")) {
                val idString = condition.substringAfter("@id='").substringBeforeLast("'")
                currentNode = findNodeByTestTag(idString, currentNode)
            } else {
                val index = condition.toIntOrNull() ?: 0
                currentNode = currentNode?.children?.get(index)
            }
        }
        return currentNode
    }

    private operator fun <T> SemanticsNode?.get(key: SemanticsPropertyKey<T>): T? =
        this?.config?.getOrNull(key)

    private fun isIdUnique(id: String): Boolean {
        val nodes = owner?.getAllSemanticsNodes(true).orEmpty()
            .filter { it[TestTag] == id }
        return nodes.size == 1
    }

    private fun getXpath() = buildString {
        var current: SemanticsNode? = selectedNode

        while (current != null) {
            val parent = current.parent
            val index = parent?.let { p ->
                (0 until p.children.size).indexOfFirst { i -> p.children[i].id == current?.id }
            } ?: 0

            val currentId = current[TestTag]
            val isUniqueId = isIdUnique(currentId ?: "")

            insert(0, "/${current[Role]}${if (currentId != null) "[@id='$currentId']" else "[$index]"}")
            current = if (isUniqueId) null else parent

            if (current?.id == 1) break
        }

        insert(0, view.getXpath())
    }

    override fun equals(other: Any?): Boolean =
        other is ComposableFinder_133 && selectedNode?.id == other.selectedNode?.id

    override fun hashCode(): Int = selectedNode.hashCode()
}