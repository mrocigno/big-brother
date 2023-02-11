package br.com.mrocigno.sandman.network

import android.content.res.ColorStateList
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import br.com.mrocigno.sandman.R
import br.com.mrocigno.sandman.inflate

class NetworkEntryView(parent: ViewGroup) : ViewHolder(parent.inflate(R.layout.item_network_entry)) {

    private val dot: View by lazy { itemView.findViewById(R.id.net_entry_dot) }
    private val hour: AppCompatTextView by lazy { itemView.findViewById(R.id.net_entry_hour) }
    private val method: AppCompatTextView by lazy { itemView.findViewById(R.id.net_entry_method) }
    private val elapsedTime: AppCompatTextView by lazy { itemView.findViewById(R.id.net_entry_elapsed_time) }
    private val container: ViewGroup by lazy { itemView.findViewById(R.id.net_entry_container) }
    private val url: AppCompatTextView by lazy { itemView.findViewById(R.id.net_entry_url) }

    fun bind(
        model: NetworkEntryModel,
        query: String,
        onEntryClick: (model: NetworkEntryModel) -> Unit
    ) {
        hour.text = model.hour.highlightQuery(query)
        method.text = model.method.highlightQuery(query)
        elapsedTime.text = model.elapsedTime.highlightQuery(query)

        url.text = model.url.highlightQuery(query)

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

    private fun String.highlightQuery(query: String): CharSequence {
        val index = indexOf(query, ignoreCase = true)
        return if (index < 0) this
            else SpannableStringBuilder(this).apply {
                setSpan(
                    ForegroundColorSpan(Color.YELLOW),
                    index, index + query.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
    }

}
