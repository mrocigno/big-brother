package br.com.mrocigno.sandman.network

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import br.com.mrocigno.sandman.R
import br.com.mrocigno.sandman.highlightQuery
import br.com.mrocigno.sandman.inflate

class NetworkEntryView(parent: ViewGroup) : ViewHolder(parent.inflate(R.layout.item_network_entry)) {

    private val dot: View by lazy { itemView.findViewById(R.id.net_entry_dot) }
    private val hour: AppCompatTextView by lazy { itemView.findViewById(R.id.net_entry_hour) }
    private val method: AppCompatTextView by lazy { itemView.findViewById(R.id.net_entry_method) }
    private val elapsedTime: AppCompatTextView by lazy { itemView.findViewById(R.id.net_entry_elapsed_time) }
    private val container: ViewGroup by lazy { itemView.findViewById(R.id.net_entry_container) }
    private val url: AppCompatTextView by lazy { itemView.findViewById(R.id.net_entry_url) }

    private val context get() = itemView.context

    @SuppressLint("SetTextI18n")
    fun bind(
        model: NetworkEntryModel,
        query: String,
        onEntryClick: (model: NetworkEntryModel) -> Unit
    ) {
        val highlightColor = context.getColor(R.color.text_highlight)
        hour.text = model.hour.highlightQuery(query, highlightColor)
        method.text = "${model.method} - ${model.statusCode}".highlightQuery(query, highlightColor)
        elapsedTime.text = model.elapsedTime.highlightQuery(query, highlightColor)
        url.text = model.url.highlightQuery(query, highlightColor)

        dot.byStatusCode(model.statusCode)
        method.byMethod(model.method)

        container.setOnClickListener {
            onEntryClick.invoke(model)
        }
    }

    private fun AppCompatTextView.byMethod(method: String) {
        val color = when (method) {
            "POST" -> R.color.net_entry_post
            "PUT" -> R.color.net_entry_put
            "GET" -> R.color.net_entry_get
            "DELETE" -> R.color.net_entry_delete
            else -> R.color.text_title
        }
        val colorList = ColorStateList.valueOf(context.getColor(color))
        backgroundTintList = colorList
        setTextColor(colorList)
    }
}
