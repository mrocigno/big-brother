package br.com.mrocigno.bigbrother.database.ui.table

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.WindowCompat
import androidx.core.view.isVisible
import br.com.mrocigno.bigbrother.common.table.AdaptiveTableLayout
import br.com.mrocigno.bigbrother.common.table.OnItemClickListener
import br.com.mrocigno.bigbrother.common.table.ViewHolder
import br.com.mrocigno.bigbrother.common.utils.cast
import br.com.mrocigno.bigbrother.common.utils.inflate
import br.com.mrocigno.bigbrother.core.OutOfDomain
import br.com.mrocigno.bigbrother.database.R
import br.com.mrocigno.bigbrother.database.model.TableDump
import br.com.mrocigno.bigbrother.database.model.TableDumpStatus
import br.com.mrocigno.bigbrother.database.ui.table.filter.FilterView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

@OutOfDomain
class TableInspectorActivity :
    AppCompatActivity(R.layout.bigbrother_activity_table_inspector), OnItemClickListener {

    private val loading: ViewGroup by lazy { findViewById(R.id.table_inspector_loading_container) }
    private val toolbar: Toolbar by lazy { findViewById(R.id.table_inspector_toolbar) }
    private val sqlLayout: TextInputLayout by lazy { findViewById(R.id.table_inspector_sql_layout) }
    private val sql: TextInputEditText by lazy { findViewById(R.id.table_inspector_sql) }
    private val tableContainer: ViewGroup by lazy { findViewById(R.id.table_inspector_container) }

    private val tableName: String get() = checkNotNull(intent.getStringExtra(TABLE_NAME_ARG))
    private val dbName: String get() = checkNotNull(intent.getStringExtra(DB_NAME_ARG))

    private val viewModel: TableInspectorViewModel by viewModels {
        TableInspectorViewModel.Factory(tableName, dbName)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        toolbar.setNavigationOnClickListener { onBackPressed() }

        setupObservables()
        viewModel.listAll()

        sqlLayout.setEndIconOnClickListener {
            viewModel.executeSQL(sql.text.toString().trim())
        }
    }

    private fun setupObservables() {
        viewModel.tableDump.observe(this, ::setupData)
        viewModel.isLoading.observe(this) {
            loading.isVisible = it
        }
    }

    private fun setupData(dump: TableDump) {
        sql.setText(dump.sql)
        toolbar.title = tableName
        tableContainer.removeAllViews()
        when (dump.status) {
            TableDumpStatus.SUCCESS -> setupTable(dump)
            TableDumpStatus.EMPTY -> setupEmpty()
            TableDumpStatus.ERROR -> setupError(dump)
        }
    }

    private fun setupTable(dump: TableDump) {
        tableContainer.addView(AdaptiveTableLayout(this).also { table ->
            table.layoutParams = FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
            table.isHeaderFixed = true
            table.isSolidRowHeader = true
            table.setAdapter(TableInspectorAdapter(this, dump).apply {
                onItemClickListener = this@TableInspectorActivity
            })
        })
    }

    override fun onColumnHeaderClick(viewHolder: ViewHolder, column: Int) {
        val filter = viewModel.getColumnData(column)
        if (!filter.isNotEmpty()) return

        val headerView = viewHolder.itemView
        val filterView = FilterView(context = this, refView = headerView, filterData = filter)
        tableContainer.addView(filterView)
        filterView.setOnConfirm {
            viewModel.filterColumn(column, it)
        }
    }

    override fun onItemClick(row: Int, column: Int) = Unit

    override fun onRowHeaderClick(row: Int) = Unit

    override fun onLeftTopHeaderClick() = Unit

    private fun setupEmpty() {
        tableContainer.inflate(R.layout.bigbrother_item_table_empty, true)
    }

    private fun setupError(dump: TableDump) {
        val textView = tableContainer
            .inflate(R.layout.bigbrother_item_table_error, false)
            .cast(AppCompatTextView::class)

        textView.text = dump.exception?.message
        tableContainer.addView(textView)
    }

    companion object {
        private const val TABLE_NAME_ARG = "BigBrother.TABLE_NAME_ARG"
        private const val DB_NAME_ARG = "BigBrother.DB_NAME_ARG"

        fun intent(context: Context, dbName: String, tableName: String) =
            Intent(context, TableInspectorActivity::class.java)
                .putExtra(TABLE_NAME_ARG, tableName)
                .putExtra(DB_NAME_ARG, dbName)
    }
}