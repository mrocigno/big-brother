package br.com.mrocigno.bigbrother.database.ui.table

import android.content.Context
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import br.com.mrocigno.bigbrother.common.table.LinkedAdaptiveTableAdapter
import br.com.mrocigno.bigbrother.common.table.ViewHolderImpl
import br.com.mrocigno.bigbrother.common.utils.inflate
import br.com.mrocigno.bigbrother.database.R
import br.com.mrocigno.bigbrother.database.model.TableDump

class TableInspectorAdapter(
    val tableDump: TableDump,
    val columnWidths: List<Int>
) : LinkedAdaptiveTableAdapter<ViewHolderImpl>() {

    override val rowCount: Int
        get() = tableDump.rowCount + 1
    override val columnCount: Int
        get() = tableDump.columnNames.size + 1

    override fun onCreateItemViewHolder(parent: ViewGroup) = TestViewHolder(parent)

    override fun onCreateColumnHeaderViewHolder(parent: ViewGroup) = HeaderViewHolder(parent)

    override fun onCreateRowHeaderViewHolder(parent: ViewGroup) = TestViewHolder(parent)

    override fun onCreateLeftTopHeaderViewHolder(parent: ViewGroup) = TestViewHolder(parent)

    override fun getColumnWidth(column: Int) = columnWidths[column]

    override fun getRowHeight(row: Int): Int = 100

    override val headerColumnHeight: Int = 150

    override val headerRowWidth: Int = 0

    override fun onBindLeftTopHeaderViewHolder(viewHolder: ViewHolderImpl) {
        (viewHolder as TestViewHolder).bind("")
    }

    override fun onBindHeaderRowViewHolder(viewHolder: ViewHolderImpl, row: Int) {
        (viewHolder as TestViewHolder).bind("")
    }

    override fun onBindHeaderColumnViewHolder(viewHolder: ViewHolderImpl, column: Int) {
        (viewHolder as HeaderViewHolder).bind(tableDump.columnNames[column - 1])
    }

    override fun onBindViewHolder(viewHolder: ViewHolderImpl, row: Int, column: Int) {
        val columnName = tableDump.columnNames[column - 1]
        (viewHolder as TestViewHolder).bind(tableDump.data[row - 1][columnName].orEmpty())
    }
}

class HeaderViewHolder(parent: ViewGroup) : ViewHolderImpl(parent.inflate(R.layout.bigbrother_cell_content)) {

    private val textView: AppCompatTextView get() = itemView as AppCompatTextView
    private val context: Context get() = itemView.context

    fun bind(data: String) {
        textView.text = data
        textView.setTextColor(context.getColor(br.com.mrocigno.bigbrother.common.R.color.text_title))
    }
}

class TestViewHolder(parent: ViewGroup) : ViewHolderImpl(parent.inflate(R.layout.bigbrother_cell_content)) {

    private val textView: AppCompatTextView get() = itemView as AppCompatTextView
    private val context: Context get() = itemView.context

    fun bind(data: String) {
        textView.text = data
    }
}