package br.com.mrocigno.bigbrother.proxy.model

import android.annotation.SuppressLint
import android.content.Context
import android.os.Parcelable
import androidx.recyclerview.widget.DiffUtil
import br.com.mrocigno.bigbrother.common.R
import br.com.mrocigno.bigbrother.common.entity.ProxyActionEntity
import br.com.mrocigno.bigbrother.common.utils.highlightQuery
import kotlinx.parcelize.Parcelize

@Parcelize
internal data class ProxyActionModel(
    val id: Long = 0,
    val action: ProxyActions,
    val name: String? = null,
    val value: String? = null,
    val body: String? = null
) : Parcelable {

    constructor(entity: ProxyActionEntity) : this(
        id = entity.id,
        action = ProxyActions.valueOf(entity.label),
        name = entity.name,
        value = entity.value,
        body = entity.body
    )

    @SuppressLint("StringFormatMatches")
    fun getDescription(context: Context): CharSequence =
        context.getString(action.description, name.orEmpty(), value.orEmpty(), body.orEmpty())
            .highlightQuery(""""${name.orEmpty()}"""", context.getColor(R.color.bb_net_entry_put))
            .highlightQuery(""""${value.orEmpty()}"""", context.getColor(R.color.bb_net_entry_get))

    fun toEntity(ruleId: Long) =
        ProxyActionEntity(
            id = id,
            proxyId = ruleId,
            label = action.name,
            name = name,
            value = value,
            body = body
        )

    class Differ : DiffUtil.ItemCallback<ProxyActionModel>() {

        override fun areContentsTheSame(oldItem: ProxyActionModel, newItem: ProxyActionModel) =
            oldItem == newItem

        override fun areItemsTheSame(oldItem: ProxyActionModel, newItem: ProxyActionModel) =
            oldItem == newItem
    }
}
