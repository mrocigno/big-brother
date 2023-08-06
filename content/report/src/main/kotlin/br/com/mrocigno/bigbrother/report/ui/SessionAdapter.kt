package br.com.mrocigno.bigbrother.report.ui

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import br.com.mrocigno.bigbrother.report.entity.SessionEntity

internal class SessionAdapter(
    private val list: List<SessionEntity>,
    private val onViewClick: (SessionEntity) -> Unit
) : Adapter<SessionItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        SessionItemViewHolder(parent)

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: SessionItemViewHolder, position: Int) {
        val model = list[position]
        holder.bind(model)
        holder.itemView.setOnClickListener {
            onViewClick(model)
        }
    }
}