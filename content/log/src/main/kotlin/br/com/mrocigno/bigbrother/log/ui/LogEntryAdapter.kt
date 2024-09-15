package br.com.mrocigno.bigbrother.log.ui

import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView.Adapter
import br.com.mrocigno.bigbrother.log.model.LogEntry
import br.com.mrocigno.bigbrother.log.model.LogEntryType

class LogEntryAdapter : Adapter<LogEntryView>() {

    private val differ = AsyncListDiffer(this, LogEntry.Differ())
    private val items: List<LogEntry> get() = differ.currentList
    private var query: String = ""
    private var typeFilter: LogEntryType? = null
    private var allItemsHolder: List<LogEntry> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = LogEntryView(parent)

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: LogEntryView, position: Int) {
        holder.bind(items[position], query) { /*TODO*/ }
    }

    fun setList(list: List<LogEntry>) {
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