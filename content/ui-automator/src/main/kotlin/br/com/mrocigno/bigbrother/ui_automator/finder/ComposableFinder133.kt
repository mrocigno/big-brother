package br.com.mrocigno.bigbrother.ui_automator.finder

import android.graphics.Rect
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.runtime.Recomposer
import androidx.compose.ui.CombinedModifier
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
import br.com.mrocigno.bigbrother.common.utils.property
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.yield

class ComposableFinder133(
    val view: ComposeView,
    x: Float? = null,
    y: Float? = null,
) : ViewFinder {

    override val rect: Rect =
        Rect()

    override val identifier: String
        get() = getXpath()

    override val name: String get() =
        selectedNode[TestTag]?.takeIf { isTestTagUnique(it) } ?: getXpath()

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
        get() = scrollableParent != null

    override val scrollableParent: ViewFinder?
        get() = selectedNode?.scrollableParent?.let { ComposableFinder133(view, it) }
            ?: AndroidViewFinder(view).scrollableParent

    override val parent: ViewFinder
        get() = selectedNode?.parent?.let { ComposableFinder133(view, it) }
            ?: AndroidViewFinder(view)

    private val owner: SemanticsOwner? = view
        .field<Any>("composition")
        .field<Any>("owner")
        .field("semanticsOwner")

    private val recomposer: Recomposer? = view
        .field<Any>("composition")
        .field<Any>("original")
        .field("parent")

    internal var selectedNode: SemanticsNode? = null

    constructor(view: ComposeView, node: SemanticsNode) : this(view, null, null) {
        initialize(node)
    }

    init {
        if (x != null && y != null)
            initialize(findNodeByCoordinates(x, y, owner?.rootSemanticsNode))
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
        // Google will pay me for this, I promise
        val state = selectedNode?.layoutInfo
            .field<CombinedModifier>("modifier")
            .field<Any>("inner")
            .property<ScrollState>("scrollerState")

        runBlocking {
            if (exactly) state?.scrollTo(y.toInt())
            else state?.scrollBy(y)
        }
    }

    override suspend fun isReady(identifier: String, timeout: Long): Boolean {
        initialize(findNodeByXpath(identifier, timeout))
        return selectedNode != null
    }

    private fun initialize(node: SemanticsNode?) {
        selectedNode = node
        selectedNode?.boundsInWindow?.toAndroidRect()?.run(rect::set)
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

    private suspend fun findNodeByXpath(xpath: String, timeout: Long = 10000): SemanticsNode? {
        val pathSegments = xpath.split("/").trimTill("ComposeView")
        var currentNode: SemanticsNode? = owner?.rootSemanticsNode

        recomposer?.currentState?.first { it == Recomposer.State.Idle }

        for (segment in pathSegments) {
            val condition = segment.substringAfter("[").substringBefore("]")

            currentNode = if (condition.startsWith("@id='")) {
                val idString = condition.substringAfter("@id='").substringBeforeLast("'")

                awaitNode(timeout) {
                    findNodeByTestTag(idString, currentNode)
                }
            } else {
                val index = condition.toIntOrNull() ?: 0

                awaitNode(timeout) {
                    currentNode?.children?.get(index)
                }
            }
        }
        return currentNode
    }

    private suspend fun awaitNode(timeout: Long, runner: () -> SemanticsNode?): SemanticsNode {
        val safeRunner = {
            runCatching {
                runner.invoke()
            }.getOrNull()
        }

        safeRunner.invoke()?.run { return this }

        return withTimeout(timeout) {
            // Brute force
            var node: SemanticsNode? = safeRunner.invoke()
            while (node == null) {
                node = runCatching { safeRunner.invoke() }.getOrNull()
                yield()
                delay(100)
            }
            node
        }
    }

    private operator fun <T> SemanticsNode?.get(key: SemanticsPropertyKey<T>): T? =
        this?.config?.getOrNull(key)

    private fun isTestTagUnique(id: String): Boolean {
        val nodes = owner?.getAllSemanticsNodes(true).orEmpty()
            .filter { it[TestTag] == id }
        return nodes.size == 1
    }

    private fun getXpath() = buildString {
        var current: SemanticsNode? = selectedNode

        while (current != null) {
            val parent = current.parent
            val index = parent?.let { p ->
                (0 until p.children.size).indexOfFirst { i ->
                    p.children[i].id == current?.id
                }
            } ?: 0

            val currentId = current[TestTag]
            val isUniqueId = isTestTagUnique(currentId ?: "")

            insert(0, "/${current[Role] ?: "Node"}${if (currentId != null) "[@id='$currentId']" else "[$index]"}")
            current = if (isUniqueId) null else parent

            if (current?.isRoot == true) break
        }

        insert(0, view.getXpath())
    }

    private val SemanticsNode.scrollableParent: SemanticsNode?
        get() = run {
            takeIf { it[ScrollBy] != null } ?: parent?.scrollableParent
        }

    override fun equals(other: Any?): Boolean =
        other is ComposableFinder133 && selectedNode?.id == other.selectedNode?.id

    override fun hashCode(): Int = selectedNode.hashCode()
}
