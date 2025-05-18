package br.com.mrocigno.bigbrother.database.ui

import android.graphics.Typeface.BOLD
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat.getDrawable
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import br.com.mrocigno.bigbrother.common.utils.gone
import br.com.mrocigno.bigbrother.common.utils.inflate
import br.com.mrocigno.bigbrother.common.utils.visible
import br.com.mrocigno.bigbrother.core.utils.getBigBrotherTask
import br.com.mrocigno.bigbrother.database.DatabaseTask
import br.com.mrocigno.bigbrother.database.R
import br.com.mrocigno.bigbrother.database.model.FileListItem
import br.com.mrocigno.bigbrother.database.model.FileListItem.Companion.DATABASE
import br.com.mrocigno.bigbrother.database.model.FileListItem.Companion.LABEL
import br.com.mrocigno.bigbrother.database.model.FileListItem.Companion.SHARED_PREFERENCES
import br.com.mrocigno.bigbrother.common.R as CR

internal class DatabaseAdapter(
    private val onClick: (FileListItem) -> Unit
) : RecyclerView.Adapter<DatabaseListItemViewHolder>() {

    private val differ = FileListItem.Differ(this)
    var list: List<FileListItem>
        get() = differ.currentList
        set(value) {
            differ.submitList(value)
        }

    init {
        val task = getBigBrotherTask(DatabaseTask::class)
        val databases = task?.databases?.values.orEmpty()
        val sharedPreferences = task?.sharedPreferences.orEmpty()

        list = listOf(
            FileListItem(LABEL, "Database")
        ) + databases.map { db ->
            FileListItem(
                nodeLvl = 1,
                type = DATABASE,
                icon = CR.drawable.bigbrother_ic_database,
                title = db.name,
                databaseHelper = db,
                children = db.tablesName.map { tableName ->
                    FileListItem(
                        nodeLvl = 2,
                        type = FileListItem.TABLE,
                        icon = CR.drawable.bigbrother_ic_table,
                        title = tableName,
                        databaseHelper = db
                    )
                }
            )
        } + listOf(
            FileListItem(LABEL, "SharedPreferences")
        ) + sharedPreferences.map { sp ->
            FileListItem(
                nodeLvl = 1,
                type = SHARED_PREFERENCES,
                title = sp,
                icon = CR.drawable.bigbrother_ic_file,
            )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        DatabaseListItemViewHolder(parent)

    override fun getItemCount() = list.size

    override fun getItemViewType(position: Int) = list[position].type

    override fun onBindViewHolder(holder: DatabaseListItemViewHolder, position: Int) {
        val model = list[position]
        holder.bind(model) {
            toggle(model)
            onClick(model)
        }
    }

    private fun toggle(model: FileListItem) {
        val children = model.children ?: return

        list = if (list.contains(children.first())) {
            list.filterNot { children.contains(it) }
        } else {
            val indexOf = list.indexOf(model) + 1
            list.toMutableList().apply { addAll(indexOf, children) }
        }
    }
}

internal class DatabaseListItemViewHolder(parent: ViewGroup) : ViewHolder(parent.inflate(R.layout.bigbrother_item_database)) {

    private val icon: AppCompatImageView by lazy { itemView.findViewById(R.id.database_item_icon) }
    private val title: AppCompatTextView by lazy { itemView.findViewById(R.id.database_item_title) }

    private val context get() = itemView.context

    fun bind(model: FileListItem, onClick: () -> Unit) {
        when (model.type) {
            DATABASE -> itemView.setBackgroundColor(context.getColor(CR.color.bb_background))
            LABEL -> {
                title.setTypeface(title.typeface, BOLD)
                itemView.setBackgroundColor(context.getColor(CR.color.bb_background))
            }
            else -> itemView.background = null
        }

        itemView.updateLayoutParams<RecyclerView.LayoutParams> {
            val spacing = context.resources.getDimensionPixelOffset(CR.dimen.bb_spacing_xl)
            leftMargin = spacing * model.nodeLvl
        }

        model.icon.takeIf { it != -1 }?.let { getDrawable(context, it) }
            ?.run(icon::setImageDrawable)
            ?.run { icon.visible() }
            ?: icon.gone()

        title.text = model.title

        itemView.setOnClickListener {
            onClick()
        }
    }
}