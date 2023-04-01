package br.com.mrocigno.sandman.corinthian

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import br.com.mrocigno.sandman.common.utils.highlightQuery
import br.com.mrocigno.sandman.common.utils.inflate
import br.com.mrocigno.sandman.common.R as CommonR

class NetworkEntryView(parent: ViewGroup) : ViewHolder(parent.inflate(R.layout.corinthian_item_network_entry)) {

    private val dot: View by lazy { itemView.findViewById(R.id.net_entry_dot) }
    private val hour: AppCompatTextView by lazy { itemView.findViewById(R.id.net_entry_hour) }
    private val method: AppCompatTextView by lazy { itemView.findViewById(R.id.net_entry_method) }
    private val elapsedTime: AppCompatTextView by lazy { itemView.findViewById(R.id.net_entry_elapsed_time) }
    private val container: ViewGroup by lazy { itemView.findViewById(R.id.net_entry_container) }
    private val url: AppCompatTextView by lazy { itemView.findViewById(R.id.net_entry_url) }
    private val loading: View by lazy { itemView.findViewById(R.id.net_entry_loading) }
    private val arrow: View by lazy { itemView.findViewById(R.id.net_entry_arrow) }

    private val context get() = itemView.context

    fun bind(
        model: NetworkEntryModel,
        query: String,
        onEntryClick: (model: NetworkEntryModel) -> Unit
    ) {
        val highlightColor = context.getColor(CommonR.color.text_highlight)
        hour.text = model.hour.highlightQuery(query, highlightColor)
        url.text = model.url.highlightQuery(query, highlightColor)
        method.byMethod(model.method)

        if (model.statusCode != null) {
            setupCompleteView(model, query, onEntryClick)
        } else {
            setupLoadingView(model, query)
        }
    }

    private fun setupLoadingView(
        model: NetworkEntryModel,
        query: String
    ) {
        val highlightColor = context.getColor(CommonR.color.text_highlight)
        dot.isInvisible = true
        loading.isVisible = true
        arrow.isInvisible = true
        method.text = model.method.highlightQuery(query, highlightColor)
        elapsedTime.text = null
        container.setOnClickListener(null)
    }

    @SuppressLint("SetTextI18n")
    private fun setupCompleteView(
        model: NetworkEntryModel,
        query: String,
        onEntryClick: (model: NetworkEntryModel) -> Unit
    ) {
        val highlightColor = context.getColor(CommonR.color.text_highlight)
        dot.isVisible = true
        loading.isGone = true
        arrow.isVisible = true
        dot.byStatusCode(model.statusCode)
        method.text = "${model.method} - ${model.statusCode}".highlightQuery(query, highlightColor)
        elapsedTime.text = model.elapsedTime?.highlightQuery(query, highlightColor)
        container.setOnClickListener {
            onEntryClick.invoke(model)
        }
    }

    private fun AppCompatTextView.byMethod(method: String) {
        val color = when (method) {
            "POST" -> CommonR.color.net_entry_post
            "PUT" -> CommonR.color.net_entry_put
            "GET" -> CommonR.color.net_entry_get
            "DELETE" -> CommonR.color.net_entry_delete
            else -> CommonR.color.text_title
        }
        val colorList = ColorStateList.valueOf(context.getColor(color))
        backgroundTintList = colorList
        setTextColor(colorList)
    }
}
