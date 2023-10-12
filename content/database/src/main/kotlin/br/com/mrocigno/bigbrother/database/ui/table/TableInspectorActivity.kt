package br.com.mrocigno.bigbrother.database.ui.table

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.WindowCompat
import br.com.mrocigno.bigbrother.common.table.AdaptiveTableLayout
import br.com.mrocigno.bigbrother.core.OutOfDomain
import br.com.mrocigno.bigbrother.core.utils.getTask
import br.com.mrocigno.bigbrother.database.DatabaseHelper
import br.com.mrocigno.bigbrother.database.DatabaseTask
import br.com.mrocigno.bigbrother.database.R
import br.com.mrocigno.bigbrother.database.model.TableDump
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout


@OutOfDomain
class TableInspectorActivity : AppCompatActivity(R.layout.bigbrother_activity_table_inspector) {

    private val appBar: AppBarLayout by lazy { findViewById(R.id.table_inspector_app_bar) }
    private val toolbar: Toolbar by lazy { findViewById(R.id.table_inspector_toolbar) }
    private val sqlLayout: TextInputLayout by lazy { findViewById(R.id.table_inspector_sql_layout) }
    private val sql: TextInputEditText by lazy { findViewById(R.id.table_inspector_sql) }
    private val tableContainer: ViewGroup by lazy { findViewById(R.id.table_inspector_container) }

    private val tableName: String get() = checkNotNull(intent.getStringExtra(TABLE_NAME_ARG))
    private val dbName: String get() = checkNotNull(intent.getStringExtra(DB_NAME_ARG))
    private val dbHelper: DatabaseHelper by lazy {
        checkNotNull(getTask(DatabaseTask::class)?.databases?.get(dbName))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        toolbar.setNavigationOnClickListener { onBackPressed() }
        dbHelper.listAll(tableName)?.run(::setupTable)
        sqlLayout.setEndIconOnClickListener {
            dbHelper.execSQL(sql.text.toString().trim())?.run(::setupTable)
        }

        appBar.offsetTopAndBottom(-500)
    }

    private fun setupTable(dump: TableDump) {
        sql.setText(dump.executedSql)
        toolbar.title = tableName
        tableContainer.removeAllViews()
//        tableContainer.addView(NestedScrollView(this).apply {
//            layoutParams = FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
//            addView(LinearLayout(this@TableInspectorActivity).apply {
//                layoutParams = FrameLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
//                orientation = LinearLayout.VERTICAL
//                addView(View(this@TableInspectorActivity).apply {
//                    layoutParams = FrameLayout.LayoutParams(500, 500)
//                    setBackgroundColor(getColor(br.com.mrocigno.bigbrother.common.R.color.boy_red))
//                })
//            })
//        })
        tableContainer.addView(AdaptiveTableLayout(this).apply {
            layoutParams = FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
            isHeaderFixed = true
            isSolidRowHeader = true
            setAdapter(
                TableInspectorAdapter(
                    dump,
                    dump.measureColumnSize(this@TableInspectorActivity)
                )
            )
        })
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