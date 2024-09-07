package br.com.mrocigno.bigbrother.database.model

import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.RecyclerView
import br.com.mrocigno.bigbrother.database.DatabaseHelper

internal class FileListItem(
    val type: Int,
    val title: String,
    val nodeLvl: Int = 0,
    @DrawableRes val icon: Int = -1,
    val children: List<FileListItem>? = null,
    val databaseHelper: DatabaseHelper? = null
) {

    class Differ(
        adapter: RecyclerView.Adapter<*>
    ) : AsyncListDiffer<FileListItem>(adapter, object : ItemCallback<FileListItem>() {
        override fun areItemsTheSame(
            oldItem: FileListItem,
            newItem: FileListItem
        ) = oldItem == newItem

        override fun areContentsTheSame(
            oldItem: FileListItem,
            newItem: FileListItem
        ) =
            oldItem.type == newItem.type
                && oldItem.title == newItem.title
    })

    companion object {
        const val LABEL = 0
        const val DATABASE = 1
        const val TABLE = 2
        const val SHARED_PREFERENCES = 3
    }
}