package br.com.mrocigno.bigbrother.deeplink.ui

import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import br.com.mrocigno.bigbrother.common.provider.id
import br.com.mrocigno.bigbrother.common.utils.getColor
import br.com.mrocigno.bigbrother.common.utils.getColorState
import br.com.mrocigno.bigbrother.common.utils.highlightQuery
import br.com.mrocigno.bigbrother.common.utils.inflate
import br.com.mrocigno.bigbrother.deeplink.R
import br.com.mrocigno.bigbrother.deeplink.model.DeeplinkAdapterItem
import br.com.mrocigno.bigbrother.deeplink.model.DeeplinkEntry
import br.com.mrocigno.bigbrother.common.R as CommonR

internal class DeeplinkEntryView(parent: ViewGroup) : ViewHolder(parent.inflate(R.layout.bigbrother_item_deeplink)) {

    private val type: AppCompatTextView by id(R.id.deeplink_item_type)
    private val path: AppCompatTextView by id(R.id.deeplink_item_path)
    private val activityName: AppCompatTextView by id(R.id.deeplink_item_activity)
    private val view: AppCompatTextView by id(R.id.deeplink_item_view)
    private val browsable: AppCompatTextView by id(R.id.deeplink_item_browsable)

    fun bind(
        item: DeeplinkAdapterItem,
        query: String,
        onClick: (DeeplinkEntry, View) -> Unit,
        onLongClick: (DeeplinkEntry, View) -> Unit,
    ) {
        val model = item as DeeplinkEntry
        val highlightColor = getColor(CommonR.color.bb_text_highlight)

        type.text = model.type.name.lowercase().replaceFirstChar { it.uppercaseChar() }
        type.backgroundTintList = getColorState(model.type.color)
        activityName.text = model.activityName.highlightQuery(query, highlightColor)
        activityName.isVisible = model.activityName.isNotBlank()
        path.text = model.path.highlightQuery(query, highlightColor)
        view.isVisible = model.hasView
        browsable.isVisible = model.hasBrowsable

        itemView.setOnClickListener {
            onClick(model, path)
        }
        itemView.setOnLongClickListener {
            onLongClick(model, path)
            true
        }
    }
}
