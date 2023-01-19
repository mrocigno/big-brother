package br.com.mrocigno.sandman.network

import android.content.Context
import android.content.res.ColorStateList
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import br.com.mrocigno.sandman.R
import br.com.mrocigno.sandman.inflate
import org.threeten.bp.format.DateTimeFormatter

class NetworkEntryView(parent: ViewGroup) : ViewHolder(parent.inflate(R.layout.item_network_entry)) {

    private val hour: AppCompatTextView by lazy { itemView.findViewById(R.id.net_entry_hour) }
    private val method: AppCompatTextView by lazy { itemView.findViewById(R.id.net_entry_method) }
    private val elapsedTime: AppCompatTextView by lazy { itemView.findViewById(R.id.net_entry_elapsed_time) }
    private val container: ViewGroup by lazy { itemView.findViewById(R.id.net_entry_container) }
    private val url: AppCompatTextView by lazy { itemView.findViewById(R.id.net_entry_url) }

    private val context: Context get() = itemView.context

    fun bind(model: NetworkEntryModel) {
        hour.text = model.hour.format(DateTimeFormatter.ISO_TIME)
        method.text = "${model.method}: ${model.statusCode}"
        elapsedTime.text = model.elapsedTime
        url.text = model.url

        val containerColor = when (model.statusCode) {
            in 200..399 -> R.color.icon_positive_50
            else -> R.color.icon_negative_50
        }
        container.backgroundTintList = ColorStateList.valueOf(context.getColor(containerColor))
    }
}