package br.com.mrocigno.bigbrother.log

import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView.Adapter

class LogEntryAdapter : Adapter<LogEntryView>() {

    private val differ = AsyncListDiffer(this, LogEntryModel.Differ())
    private val items: List<LogEntryModel> get() = differ.currentList
    private var query: String = ""
    private var typeFilter: LogEntryType? = null
    private var allItemsHolder: List<LogEntryModel> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = LogEntryView(parent)

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: LogEntryView, position: Int) {
        holder.bind(items[position], query) { /*TODO*/ }
    }

    fun setList(list: List<LogEntryModel>) {
        allItemsHolder = list
        filter(query, typeFilter)
    }

    fun filter(query: String, type: LogEntryType?) {
        this.query = query
        this.typeFilter = type
        val filtered = if (query.isNotBlank() || typeFilter != null) runCatching {
            allItemsHolder.filter {
                it.toString().contains(query, true)
                    && typeFilter?.let { type -> it.lvl == type } ?: true
            }
        }.getOrElse { allItemsHolder } else allItemsHolder
        differ.submitList(filtered)
    }
}