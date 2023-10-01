package br.com.mrocigno.bigbrother.database.ui

import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import br.com.mrocigno.bigbrother.common.utils.inflate
import br.com.mrocigno.bigbrother.core.utils.getTask
import br.com.mrocigno.bigbrother.database.DatabaseTask
import br.com.mrocigno.bigbrother.database.R
import br.com.mrocigno.bigbrother.database.model.DatabaseListItem
import br.com.mrocigno.bigbrother.common.R as CR

internal class DatabaseAdapter(
    private val onClick: (DatabaseListItem) -> Unit
) : RecyclerView.Adapter<DatabaseListItemViewHolder>() {

    private val differ = DatabaseListItem.Differ(this)
    var list: List<DatabaseListItem>
        get() = differ.currentList
        set(value) {
            differ.submitList(value)
        }

    init {
        list = getTask(DatabaseTask::class)
            ?.databases
            ?.values
            ?.map { db ->
                DatabaseListItem(
                    nodeLvl = 0,
                    type = DatabaseListItem.DATABASE,
                    icon = R.drawable.bigbrother_ic_database,
                    title = db.name,
                    databaseHelper = db,
                    children = db.tablesName.map { tableName ->
                        DatabaseListItem(
                            nodeLvl = 1,
                            type = DatabaseListItem.TABLE,
                            icon = R.drawable.bigbrother_ic_table,
                            title = tableName,
                            databaseHelper = db
                        )
                    }
                )
            }
            ?: emptyList()
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

    private fun toggle(model: DatabaseListItem) {
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

    fun bind(model: DatabaseListItem, onClick: () -> Unit) {
        if (model.type == DatabaseListItem.DATABASE) itemView.setBackgroundColor(context.getColor(CR.color.background))
        itemView.updateLayoutParams<RecyclerView.LayoutParams> {
            val spacing = context.resources.getDimensionPixelOffset(CR.dimen.spacing_xl)
            leftMargin = spacing * model.nodeLvl
        }

        icon.setImageDrawable(ContextCompat.getDrawable(context, model.icon))
        title.text = model.title

        itemView.setOnClickListener {
            onClick()
        }
    }
}