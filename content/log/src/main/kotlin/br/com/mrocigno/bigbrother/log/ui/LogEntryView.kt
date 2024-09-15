package br.com.mrocigno.bigbrother.log.ui

import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import br.com.mrocigno.bigbrother.common.utils.applyIf
import br.com.mrocigno.bigbrother.common.utils.getColor
import br.com.mrocigno.bigbrother.common.utils.highlightQuery
import br.com.mrocigno.bigbrother.common.utils.inflate
import br.com.mrocigno.bigbrother.log.R
import br.com.mrocigno.bigbrother.log.model.LogEntry
import br.com.mrocigno.bigbrother.common.R as CommonR

class LogEntryView(parent: ViewGroup) : ViewHolder(parent.inflate(R.layout.bigbrother_item_log_entry)) {

    private val icon: AppCompatTextView by lazy { itemView.findViewById(R.id.log_entry_icon) }
    private val message: AppCompatTextView by lazy { itemView.findViewById(R.id.log_entry_message) }

    fun bind(model: LogEntry, query: String, onClick: () -> Unit) {
        val highlightColor = getColor(CommonR.color.text_highlight)

        val type = model.lvl
        icon.text = type.initial
        icon.setBackgroundColor(getColor(type.bgColor))
        icon.setTextColor(getColor(type.fgColor))

        message.setTextColor(getColor(type.textColor))
        message.text = StringBuilder()
            .append(model.tag)
            .append(" - ")
            .append(model.message)
            .applyIf(model.errorStacktrace != null) {
                appendLine()
                append(model.errorStacktrace)
            }
            .toString()
            .highlightQuery(query, highlightColor)

        if (message.lineCount > 4) itemView.setOnClickListener {
            onClick()
        }
    }
}