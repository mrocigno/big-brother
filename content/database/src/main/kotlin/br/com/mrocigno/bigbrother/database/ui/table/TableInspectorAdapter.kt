package br.com.mrocigno.bigbrother.database.ui.table

import android.content.Context
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import br.com.mrocigno.bigbrother.common.helpers.SQLBuilder
import br.com.mrocigno.bigbrother.common.table.LinkedAdaptiveTableAdapter
import br.com.mrocigno.bigbrother.common.table.ViewHolder
import br.com.mrocigno.bigbrother.common.utils.inflate
import br.com.mrocigno.bigbrother.database.R
import br.com.mrocigno.bigbrother.database.model.TableDump
import br.com.mrocigno.bigbrother.database.ui.table.filter.FilterSort
import br.com.mrocigno.bigbrother.common.R as CR

class TableInspectorAdapter(
    context: Context,
    private val tableDump: TableDump
) : LinkedAdaptiveTableAdapter<ViewHolder>() {

    private val columnWidths = tableDump.measureColumnSize(context)

    override val rowCount: Int
        get() = tableDump.rowCount + 1
    override val columnCount: Int
        get() = tableDump.columnNames.size + 1

    override fun onCreateItemViewHolder(parent: ViewGroup) = ContentViewHolder(parent)

    override fun onCreateColumnHeaderViewHolder(parent: ViewGroup) = HeaderViewHolder(parent)

    override fun onCreateRowHeaderViewHolder(parent: ViewGroup) = ContentViewHolder(parent)

    override fun onCreateLeftTopHeaderViewHolder(parent: ViewGroup) = ContentViewHolder(parent)

    override fun getColumnWidth(column: Int) = columnWidths[column]

    override fun getRowHeight(row: Int): Int = 100

    override val headerColumnHeight: Int = 150

    override val headerRowWidth: Int = 0

    override fun onBindLeftTopHeaderViewHolder(viewHolder: ViewHolder) {
        (viewHolder as ContentViewHolder).bind("")
    }

    override fun onBindHeaderRowViewHolder(viewHolder: ViewHolder, row: Int) {
        (viewHolder as ContentViewHolder).bind("")
    }

    override fun onBindHeaderColumnViewHolder(viewHolder: ViewHolder, column: Int) {
        val columnName = tableDump.columnNames[column - 1]
        val sort = SQLBuilder(tableDump.sql)
            .orderBy[columnName]
            ?.uppercase()
            ?.run(FilterSort::valueOf)
        (viewHolder as HeaderViewHolder).bind(columnName, sort)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, row: Int, column: Int) {
        val columnName = tableDump.columnNames[column - 1]
        (viewHolder as ContentViewHolder).bind(tableDump.data[row - 1][columnName].orEmpty())
    }
}

class HeaderViewHolder(parent: ViewGroup) : ViewHolder(parent.inflate(R.layout.bigbrother_cell_content)) {

    private val textView: AppCompatTextView get() = itemView as AppCompatTextView
    private val context: Context get() = itemView.context

    fun bind(data: String, sort: FilterSort?) {
        textView.background = when (sort) {
            FilterSort.ASC -> ContextCompat.getDrawable(context, R.drawable.bigbrother_cell_header_az_background)
            FilterSort.DESC -> ContextCompat.getDrawable(context, R.drawable.bigbrother_cell_header_za_background)
            null -> ContextCompat.getDrawable(context, R.drawable.bigbrother_cell_header_background)
        }
        textView.text = data
        textView.setTextColor(context.getColor(CR.color.text_title))
    }
}

class ContentViewHolder(parent: ViewGroup) : ViewHolder(parent.inflate(R.layout.bigbrother_cell_content)) {

    private val textView: AppCompatTextView get() = itemView as AppCompatTextView

    fun bind(data: String) {
        textView.text = data
    }
}