package br.com.mrocigno.bigbrother.network.ui

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView.Adapter
import br.com.mrocigno.bigbrother.network.R
import br.com.mrocigno.bigbrother.network.model.NetworkEntryModel

internal class NetworkEntryAdapter(
    private val onEntryClick: (NetworkEntryModel) -> Unit,
    private val onEntryLongClick: (View, NetworkEntryModel) -> Unit
) : Adapter<NetworkEntryView>() {

    private val differ = AsyncListDiffer(this, NetworkEntryModel.Differ())
    private val items: List<NetworkEntryModel> get() = differ.currentList
    private var query: String = ""
    private var allItemsHolder: List<NetworkEntryModel> = emptyList()

    fun setList(list: List<NetworkEntryModel>) {
        allItemsHolder = list
        filter(query)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = NetworkEntryView(parent)

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: NetworkEntryView, position: Int) {
        val item = items[position]
        holder.bind(item, query, onEntryClick)
        holder.itemView.setOnLongClickListener {
            val title = it.findViewById(R.id.net_entry_url) ?: it
            onEntryLongClick.invoke(title, item)
            true
        }
    }

    fun filter(query: String) {
        this.query = query
        val filtered = if (query.isNotBlank()) runCatching {
            allItemsHolder.filter {
                it.toString().contains(query, true)
            }
        }.getOrElse { allItemsHolder } else allItemsHolder
        differ.submitList(filtered)
    }
}