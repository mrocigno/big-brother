package br.com.mrocigno.bigbrother.proxy.ui

import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import br.com.mrocigno.bigbrother.common.dao.DistinctEndpoints
import br.com.mrocigno.bigbrother.common.helpers.SimpleDiffer
import br.com.mrocigno.bigbrother.common.provider.id
import br.com.mrocigno.bigbrother.common.utils.byMethod
import br.com.mrocigno.bigbrother.common.utils.inflate
import br.com.mrocigno.bigbrother.proxy.R

internal class ProxyEndpointAdapter(
    private val onSelect: (method: String, url: String) -> Unit
) : Adapter<EndpointViewHolder>() {

    var items: List<DistinctEndpoints>
        get() = differ.currentList
        set(value) {
            allItemsHolder = value
            differ.submitList(value)
        }

    private val differ = AsyncListDiffer(this, SimpleDiffer<DistinctEndpoints>())
    private var query: String = ""
    private var allItemsHolder: List<DistinctEndpoints> = emptyList()

    fun filter(query: String) {
        this.query = query
        val filtered = if (query.isNotBlank()) runCatching {
            allItemsHolder.filter {
                it.toString().contains(query, true)
            }
        }.getOrElse { allItemsHolder } else allItemsHolder
        differ.submitList(filtered)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): EndpointViewHolder = EndpointViewHolder(parent)

    override fun onBindViewHolder(
        holder: EndpointViewHolder,
        position: Int
    ) {
        val (method, url) = items[position]
        holder.bind(method, url)
        holder.itemView.setOnClickListener {
            onSelect(method, url)
        }
    }

    override fun getItemCount(): Int = items.size

}

internal class EndpointViewHolder(parent: ViewGroup) : ViewHolder(parent.inflate(R.layout.bigbrother_item_endpoint)) {

    private val method: AppCompatTextView by id(R.id.proxy_eps_method)
    private val name: AppCompatTextView by id(R.id.proxy_eps_name)

    fun bind(method: String, url: String) {
        this.method.byMethod(method)
        this.method.text = method
        this.name.text = url
    }
}