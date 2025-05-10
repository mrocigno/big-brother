package br.com.mrocigno.bigbrother.proxy.model

import androidx.recyclerview.widget.DiffUtil

internal data class ProxyActionModel(
    val action: ProxyActions,
    val name: String? = null,
    val value: String? = null,
    val body: String? = null
) {

    class Differ : DiffUtil.ItemCallback<ProxyActionModel>() {

        override fun areContentsTheSame(oldItem: ProxyActionModel, newItem: ProxyActionModel) =
            oldItem == newItem

        override fun areItemsTheSame(oldItem: ProxyActionModel, newItem: ProxyActionModel) =
            oldItem == newItem
    }
}
