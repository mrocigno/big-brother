package br.com.mrocigno.bigbrother.proxy.ui

import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import br.com.mrocigno.bigbrother.proxy.model.ProxyActionModel

internal class ProxyActionsAdapter : Adapter<ViewHolder>() {

    var list: List<ProxyActionModel>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    private val differ = AsyncListDiffer(this, ProxyActionModel.Differ())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        object : ViewHolder(AppCompatTextView(parent.context)) {}

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        (holder.itemView as AppCompatTextView).text = list[position].toString()
    }

    override fun getItemCount(): Int = list.size
}