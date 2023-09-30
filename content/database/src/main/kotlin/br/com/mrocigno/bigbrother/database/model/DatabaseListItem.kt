package br.com.mrocigno.bigbrother.database.model

import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.RecyclerView

class DatabaseListItem(
    val nodeLvl: Int,
    val type: Int,
    @DrawableRes val icon: Int,
    val title: String,
    val children: List<DatabaseListItem>? = null
) {

    class Differ(
        adapter: RecyclerView.Adapter<*>
    ) : AsyncListDiffer<DatabaseListItem>(adapter, object : ItemCallback<DatabaseListItem>() {
        override fun areItemsTheSame(
            oldItem: DatabaseListItem,
            newItem: DatabaseListItem
        ) = oldItem == newItem

        override fun areContentsTheSame(
            oldItem: DatabaseListItem,
            newItem: DatabaseListItem
        ) =
            oldItem.type == newItem.type
                && oldItem.title == newItem.title
    })

    companion object {
        const val DATABASE = 1
        const val TABLE = 2
    }
}