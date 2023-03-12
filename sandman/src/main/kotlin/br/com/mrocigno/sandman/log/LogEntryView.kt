package br.com.mrocigno.sandman.log

import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import br.com.mrocigno.sandman.R
import br.com.mrocigno.sandman.utils.getColor
import br.com.mrocigno.sandman.utils.highlightQuery
import br.com.mrocigno.sandman.utils.inflate

class LogEntryView(parent: ViewGroup) : ViewHolder(parent.inflate(R.layout.item_log_entry)) {

    private val icon: AppCompatTextView by lazy { itemView.findViewById(R.id.log_entry_icon) }
    private val message: AppCompatTextView by lazy { itemView.findViewById(R.id.log_entry_message) }

    fun bind(model: LogEntryModel, query: String, onClick: () -> Unit) {
        val highlightColor = getColor(R.color.text_highlight)

        val type = model.lvl
        icon.text = type.initial
        icon.setBackgroundColor(getColor(type.bgColor))
        icon.setTextColor(getColor(type.fgColor))

        message.setTextColor(getColor(type.textColor))
        message.text = itemView.context.getString(R.string.generic_separation, model.tag, model.message)
            .highlightQuery(query, highlightColor)

        if (message.lineCount > 4) itemView.setOnClickListener {
            onClick()
        }
    }
}