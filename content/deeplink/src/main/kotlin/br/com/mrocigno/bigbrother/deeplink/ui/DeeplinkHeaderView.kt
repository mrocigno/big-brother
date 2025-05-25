package br.com.mrocigno.bigbrother.deeplink.ui

import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import br.com.mrocigno.bigbrother.common.provider.id
import br.com.mrocigno.bigbrother.common.utils.inflate
import br.com.mrocigno.bigbrother.deeplink.R
import br.com.mrocigno.bigbrother.deeplink.model.DeeplinkAdapterItem
import br.com.mrocigno.bigbrother.deeplink.model.DeeplinkHeader

internal class DeeplinkHeaderView(parent: ViewGroup) : ViewHolder(parent.inflate(R.layout.bigbrother_item_header)) {

    private val icon : AppCompatImageView by id(R.id.deeplink_header_icon)
    private val title : AppCompatTextView by id(R.id.deeplink_header_title)
    private val clear : AppCompatImageView by id(R.id.deeplink_header_clear)

    fun bind(item: DeeplinkAdapterItem, onClear: () -> Unit) {
        val model = item as DeeplinkHeader
        icon.setImageResource(model.icon)
        title.setText(model.title)
        clear.isVisible = model.showClear
        clear.setOnClickListener { onClear.invoke() }
    }
}
