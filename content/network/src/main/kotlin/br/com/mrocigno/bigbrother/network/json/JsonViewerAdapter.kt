package br.com.mrocigno.bigbrother.network.json

import android.graphics.Color
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.graphics.ColorUtils
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import br.com.mrocigno.bigbrother.common.utils.copyToClipboard
import br.com.mrocigno.bigbrother.common.utils.highlightQuery
import br.com.mrocigno.bigbrother.common.utils.inflate
import br.com.mrocigno.bigbrother.network.R
import br.com.mrocigno.bigbrother.common.R as CommonR

class JsonViewerAdapter(
    private val origin: List<JsonViewerModel>
) : Adapter<JsonViewerViewHolder>() {

    private val currentList: List<JsonViewerModel> get() = differ.currentList
    private val differ = AsyncListDiffer(this, JsonViewerModel.Differ()).apply {
        submitList(origin)
    }

    private var search: String? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        JsonViewerViewHolder(parent)

    override fun getItemCount() = differ.currentList.size

    override fun onBindViewHolder(holder: JsonViewerViewHolder, position: Int) {
        holder.bind(currentList[position], search.orEmpty()) {
            if (!it.expanded) expand(it)
            else collapse(it)
        }
    }

    fun setSearch(search: String) {
        if (search.length < 3) {
            this.search = null
            differ.submitList(origin)
            collapseAll()
            return
        }
        this.search = search

        differ.submitList(origin.applySearch(search))
    }

    private fun expand(model: JsonViewerModel) {
        if (model.expanded) return
        model.expanded = true

        val expandList = currentList.toMutableList()
        val nodes = model.children.orEmpty()
        val index = expandList.indexOf(model) + 1

        expandList.addAll(index, nodes)
        differ.submitList(expandList)
    }

    private fun collapseAll() {
        currentList.forEach {
            collapse(it)
        }
    }

    private fun collapse(model: JsonViewerModel) {
        if (!model.expanded) return
        model.expanded = false

        val expandList = currentList.toMutableList()
        val nodes = getNodesToCollapse(model)

        expandList.removeAll(nodes)
        differ.submitList(expandList)
    }

    private fun getNodesToCollapse(model: JsonViewerModel): List<JsonViewerModel> {
        val result = mutableListOf<JsonViewerModel>()

        val children = model.children.orEmpty()
        result.addAll(children)
        for (json in children) {
            if (json.expanded) {
                json.expanded = false
                result.addAll(getNodesToCollapse(json))
            }
        }

        return result
    }

    private fun List<JsonViewerModel>.applySearch(search: String): List<JsonViewerModel> {
        val result = this.toMutableList()

        for (node in this) {
            if (node.toString().contains(search, true)) {
                node.expanded = true
                val nodes = node.children.orEmpty().applySearch(search)
                val index = result.indexOf(node) + 1

                result.addAll(index, nodes)
            } else {
                result.remove(node)
            }
        }
        return result
    }
}

class JsonViewerViewHolder(parent: ViewGroup) : ViewHolder(parent.inflate(R.layout.bigbrother_item_json_viewer)) {

    private val key: AppCompatTextView by lazy { itemView.findViewById(R.id.json_viewer_item_key) }
    private val value: AppCompatTextView by lazy { itemView.findViewById(R.id.json_viewer_item_value) }
    private val icon: AppCompatImageView by lazy { itemView.findViewById(R.id.json_viewer_item_ic_down) }

    private val context get() = itemView.context

    fun bind(model: JsonViewerModel, query: String, onExpand: (model: JsonViewerModel) -> Unit) {
        adjustLevel(model)
        adjustContent(model, query, onExpand)
    }

    private fun adjustLevel(model: JsonViewerModel) {
        itemView.setBackgroundColor(ColorUtils.setAlphaComponent(Color.GRAY, model.lvl * 10))
        itemView.updateLayoutParams<RecyclerView.LayoutParams> {
            val spacing = context.resources.getDimensionPixelOffset(CommonR.dimen.spacing_s)
            leftMargin = spacing * model.lvl
        }
    }

    private fun adjustContent(model: JsonViewerModel, query: String, onExpand: (model: JsonViewerModel) -> Unit) {
        val highlightColor = context.getColor(CommonR.color.text_highlight)
        key.text = model.key.plus(":").highlightQuery(query, highlightColor)
        if (model.children != null) {
            icon.isVisible = true
            if (model.expanded) {
                icon.rotation = 180f
            } else {
                icon.rotation = 0f
            }
            value.text = context.getString(R.string.json_viewer_expand_click)
            value.setTextColor(context.getColor(CommonR.color.text_hyperlink))
            itemView.setOnClickListener { onExpand(model) }
        } else {
            icon.isVisible = false
            itemView.setOnClickListener(null)
            value.setTextColor(context.getColor(CommonR.color.text_paragraph))
            value.text = model.value.toString().highlightQuery(query, highlightColor)
        }
        itemView.setOnLongClickListener {
            context.copyToClipboard(
                model.value.toString(),
                context.getString(R.string.json_viewer_copy_feedback, model.key)
            )
            true
        }
    }
}