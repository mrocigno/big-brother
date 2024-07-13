package br.com.mrocigno.bigbrother.report.ui

import android.content.Context
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import br.com.mrocigno.bigbrother.common.utils.inflate
import br.com.mrocigno.bigbrother.report.R
import br.com.mrocigno.bigbrother.report.entity.SessionEntity
import br.com.mrocigno.bigbrother.report.model.SessionStatus
import org.threeten.bp.format.DateTimeFormatter

internal class SessionItemViewHolder(parent: ViewGroup) : ViewHolder(parent.inflate(R.layout.bigbrother_item_session)) {

    val title: AppCompatTextView by lazy { itemView.findViewById(R.id.session_item_title) }
    private val date: AppCompatTextView by lazy { itemView.findViewById(R.id.session_item_date) }
    private val status: AppCompatTextView by lazy { itemView.findViewById(R.id.session_item_status) }
    private val imgCount: AppCompatTextView by lazy { itemView.findViewById(R.id.session_item_img_count) }

    private val context: Context get() = itemView.context

    fun bind(model: SessionEntity) {
        imgCount.text = if (model.status == SessionStatus.CRASHED) "1" else "0"
        title.text = context.getString(R.string.report_session_session, model.id)
        status.text = model.status.name
        status.setTextColor(context.getColor(model.status.color))
        model.dateTime
            .format(DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm"))
            .run(date::setText)
    }
}