package br.com.mrocigno.bigbrother.proxy.model

import android.annotation.SuppressLint
import android.content.Context
import androidx.recyclerview.widget.DiffUtil
import br.com.mrocigno.bigbrother.common.entity.ProxyActionEntity

internal data class ProxyActionModel(
    val action: ProxyActions,
    val name: String? = null,
    val value: String? = null,
    val body: String? = null
) {

    constructor(entity: ProxyActionEntity) : this(
        action = ProxyActions.valueOf(entity.label),
        name = entity.name,
        value = entity.value,
        body = entity.body
    )

    @SuppressLint("StringFormatMatches")
    fun getDescription(context: Context) =
        context.getString(action.description, name.orEmpty(), value.orEmpty(), body.orEmpty())

    class Differ : DiffUtil.ItemCallback<ProxyActionModel>() {

        override fun areContentsTheSame(oldItem: ProxyActionModel, newItem: ProxyActionModel) =
            oldItem == newItem

        override fun areItemsTheSame(oldItem: ProxyActionModel, newItem: ProxyActionModel) =
            oldItem == newItem
    }
}
