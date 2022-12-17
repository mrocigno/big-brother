package br.com.mrocigno.sandman.json

import android.graphics.Color
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.graphics.ColorUtils
import androidx.core.text.color
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import br.com.mrocigno.sandman.R
import br.com.mrocigno.sandman.copyToClipboard
import br.com.mrocigno.sandman.inflate

class JsonViewerAdapter(
    private val list: MutableList<JsonViewerModel>
) : Adapter<JsonViewerViewHolder>() {

    private val searchList = mutableListOf(*list.toTypedArray())
    private var search: String? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        JsonViewerViewHolder(parent)

    override fun getItemCount() = searchList.size

    override fun onBindViewHolder(holder: JsonViewerViewHolder, position: Int) {
        holder.bind(searchList[position]) {
            if (!it.expanded) expand(it)
            else collapse(it)
        }
    }

    fun setSearch(search: String) {
        if (search.length < 3) {
            this.search = null
            collapseAll()
            return
        }
        this.search = search

        list.applySearch(search, null)
    }

    private fun notifyItemChanged(model: JsonViewerModel) =
        notifyItemChanged(searchList.indexOf(model), Unit)

    private fun expand(model: JsonViewerModel) {
        model.expanded = true
        val nodes = model.children.orEmpty()
        val index = searchList.indexOf(model) + 1

        searchList.addAll(index, nodes)
        notifyItemRangeInserted(index, nodes.size)
        notifyItemChanged(model)
    }

    private fun collapseAll() {
        list.forEach(::collapse)
    }

    private fun collapse(model: JsonViewerModel) {
        if (!model.expanded) return
        model.expanded = false
        val nodes = getNodesToCollapse(model)
        val index = searchList.indexOf(model) + 1

        searchList.removeAll(nodes)
        notifyItemRangeRemoved(index, nodes.size)
        notifyItemChanged(model)
    }

    private fun getNodesToCollapse(model: JsonViewerModel): List<JsonViewerModel> {
        val result = mutableListOf<JsonViewerModel>()

        result.addAll(model.children.orEmpty())
        result.forEach {
            if (it.expanded) {
                it.expanded = false
                result.addAll(getNodesToCollapse(it))
            }
        }

        return result
    }

    private fun List<JsonViewerModel>.applySearch(search: String, parent: JsonViewerModel?) {
        for (node in this) {
            if (node.toString().contains(search, true)) {
                if (parent?.expanded == true) continue
                parent?.run {
                    expand(this)
                }
            } else {
                if (parent?.expanded == false) continue
                parent?.run {
                    collapse(this)
                }
            }
            node.children?.applySearch(search, node)
        }
    }
}

class JsonViewerViewHolder(parent: ViewGroup) : ViewHolder(parent.inflate(R.layout.item_json_viewer)) {

    private val key: AppCompatTextView by lazy { itemView.findViewById(R.id.json_viewer_item_key) }
    private val value: AppCompatTextView by lazy { itemView.findViewById(R.id.json_viewer_item_value) }
    private val icon: AppCompatImageView by lazy { itemView.findViewById(R.id.json_viewer_item_ic_down) }

    private val context get() = itemView.context

    fun bind(model: JsonViewerModel, onExpand: (model: JsonViewerModel) -> Unit) {
        adjustLevel(model)
        adjustContent(model, onExpand)
    }

    private fun adjustLevel(model: JsonViewerModel) {
        itemView.setBackgroundColor(ColorUtils.setAlphaComponent(Color.GRAY, model.lvl * 10))
        itemView.updateLayoutParams<RecyclerView.LayoutParams> {
            val spacing = context.resources.getDimensionPixelOffset(R.dimen.spacing_s)
            leftMargin = spacing * model.lvl
        }
    }

    private fun adjustContent(model: JsonViewerModel, onExpand: (model: JsonViewerModel) -> Unit) {
        key.text = model.key.plus(":")
        if (model.children != null) {
            icon.isVisible = true
            if (model.expanded) {
                icon.rotation = 180f
            } else {
                icon.rotation = 0f
            }
            value.text = context.getString(R.string.json_viewer_expand_click)
            value.setTextColor(context.getColor(R.color.text_hyperlink))
            itemView.setOnClickListener { onExpand(model) }
        } else {
            icon.isVisible = false
            value.text = model.value.toString()
            itemView.setOnClickListener(null)
            value.setTextColor(context.getColor(R.color.text_paragraph))
        }
        itemView.setOnLongClickListener {
            context.copyToClipboard(
                model.value.toString(),
                context.getString(R.string.json_viewer_copy_feedback, model.key)
            )
            true
        }
    }

    private fun applySearchHighlight(search: String?) {
        search ?: return

        if (key.text.toString().contains(search, true)) {
            key.text = SpannableStringBuilder(key.text).apply {
                val index = indexOf(search, 0, true)
                setSpan(ForegroundColorSpan(Color.YELLOW), index, (index + search.length), SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }
    }
}