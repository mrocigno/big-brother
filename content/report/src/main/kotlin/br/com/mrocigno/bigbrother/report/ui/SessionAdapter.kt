package br.com.mrocigno.bigbrother.report.ui

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.RecyclerView.Adapter
import br.com.mrocigno.bigbrother.report.model.SessionEntry

internal class SessionAdapter(
    private val onViewClick: (SessionEntry, View) -> Unit
) : Adapter<SessionItemViewHolder>() {

    var list: List<SessionEntry>
        set(value) = differ.submitList(value)
        get() = differ.currentList

    private val differ = AsyncListDiffer(this, object : ItemCallback<SessionEntry>() {
        override fun areItemsTheSame(oldItem: SessionEntry, newItem: SessionEntry) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: SessionEntry, newItem: SessionEntry) =
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