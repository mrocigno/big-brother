package br.com.mrocigno.bigbrother.report.ui

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.RecyclerView.Adapter
import br.com.mrocigno.bigbrother.report.entity.SessionEntity

internal class SessionAdapter(
    private val onViewClick: (SessionEntity, View) -> Unit
) : Adapter<SessionItemViewHolder>() {

    var list: List<SessionEntity>
        set(value) = differ.submitList(value)
        get() = differ.currentList

    private val differ = AsyncListDiffer(this, object : ItemCallback<SessionEntity>() {
        override fun areItemsTheSame(oldItem: SessionEntity, newItem: SessionEntity) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: SessionEntity, newItem: SessionEntity) =
            oldItem.status == newItem.status
                    && oldItem.dateTime == newItem.dateTime
    })

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        SessionItemViewHolder(parent)

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: SessionItemViewHolder, position: Int) {
        val model = list[position]
        holder.bind(model)
        holder.itemView.setOnClickListener {
            onViewClick(model, holder.title)
        }
    }
}