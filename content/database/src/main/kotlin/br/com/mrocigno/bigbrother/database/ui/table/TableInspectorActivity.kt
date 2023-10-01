package br.com.mrocigno.bigbrother.database.ui.table

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import br.com.mrocigno.bigbrother.core.utils.getTask
import br.com.mrocigno.bigbrother.database.DatabaseHelper
import br.com.mrocigno.bigbrother.database.DatabaseTask
import br.com.mrocigno.bigbrother.database.R
import br.com.mrocigno.bigbrother.database.model.TableDump
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class TableInspectorActivity : AppCompatActivity(R.layout.bigbrother_activity_table_inspector) {

    private val toolbar: Toolbar by lazy { findViewById(R.id.table_inspector_toolbar) }
    private val sqlLayout: TextInputLayout by lazy { findViewById(R.id.table_inspector_sql_layout) }
    private val sql: TextInputEditText by lazy { findViewById(R.id.table_inspector_sql) }

    private val tableName: String get() = checkNotNull(intent.getStringExtra(TABLE_NAME_ARG))
    private val dbName: String get() = checkNotNull(intent.getStringExtra(DB_NAME_ARG))
    private val dbHelper: DatabaseHelper by lazy {
        checkNotNull(getTask(DatabaseTask::class)?.databases?.get(dbName))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        toolbar.setNavigationOnClickListener { onBackPressed() }
        dbHelper.listAll(tableName)?.run(::setupTable)
    }

    private fun setupTable(dump: TableDump) {
        sql.setText(dump.executedSql)
        toolbar.title = tableName
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