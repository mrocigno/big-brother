package br.com.mrocigno.bigbrother.proxy.ui

import android.content.Context
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import br.com.mrocigno.bigbrother.common.provider.id
import br.com.mrocigno.bigbrother.common.utils.gone
import br.com.mrocigno.bigbrother.common.utils.highlightQuery
import br.com.mrocigno.bigbrother.common.utils.inflate
import br.com.mrocigno.bigbrother.common.utils.visible
import br.com.mrocigno.bigbrother.proxy.R
import br.com.mrocigno.bigbrother.proxy.model.ProxyActionModel
import br.com.mrocigno.bigbrother.common.R as CR

internal class ProxyActionsAdapter : Adapter<ActionViewHolder>() {

    var list: List<ProxyActionModel>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    private val differ = AsyncListDiffer(this, ProxyActionModel.Differ())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActionViewHolder =
        ActionViewHolder(parent)

    override fun onBindViewHolder(holder: ActionViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int = list.size
}

internal class ActionViewHolder(
    parent: ViewGroup
) : ViewHolder(parent.inflate(R.layout.bigbrother_item_action)) {

    private val description: AppCompatTextView by id(R.id.proxy_item_action_description)
    private val body: AppCompatTextView by id(R.id.proxy_item_action_body)
    private val context: Context get() = itemView.context

    fun bind(model: ProxyActionModel) {
        description.text = model.getDescription(context)
            .highlightQuery(""""${model.name.orEmpty()}"""", context.getColor(CR.color.bb_net_entry_put))
            .highlightQuery(""""${model.value.orEmpty()}"""", context.getColor(CR.color.bb_net_entry_get))

        model.body?.let {
            body.text = it
            body.visible()
        } ?: body.gone()
    }
}