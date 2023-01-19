package br.com.mrocigno.sandman.network

import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView.Adapter

class NetworkEntryAdapter : Adapter<NetworkEntryView>() {

    private val differ = AsyncListDiffer(this, NetworkEntryModel.Differ())
    private val items: List<NetworkEntryModel> get() = differ.currentList

    fun setList(list: List<NetworkEntryModel>) = differ.submitList(list)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = NetworkEntryView(parent)

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: NetworkEntryView, position: Int) {
        holder.bind(items[position])
    }
}