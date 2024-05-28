package br.com.mrocigno.bigbrother.network.ui

import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView.Adapter
import br.com.mrocigno.bigbrother.network.model.NetworkEntryModel

class NetworkEntryAdapter(
    private val onEntryClick: (NetworkEntryModel) -> Unit
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
        holder.bind(items[position], query, onEntryClick)
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