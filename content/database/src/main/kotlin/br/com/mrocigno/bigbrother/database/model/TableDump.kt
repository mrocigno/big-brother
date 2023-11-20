package br.com.mrocigno.bigbrother.database.model

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Build
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.WindowManager
import androidx.appcompat.widget.AppCompatTextView
import br.com.mrocigno.bigbrother.common.utils.cast
import br.com.mrocigno.bigbrother.database.R
import kotlin.math.roundToInt

class TableDump(
    val data: List<Map<String, String>> = emptyList(),
    val sql: String = "",
    val rowCount: Int = 0,
    val page: Int = 0,
    val exception: Throwable? = null,
    val executionTime: String = "0ms"
) {

    val columnNames = data.firstOrNull()?.keys?.toTypedArray().orEmpty()

    val status: TableDumpStatus = when {
        data.isNotEmpty() -> TableDumpStatus.SUCCESS
        exception != null -> TableDumpStatus.ERROR
        else -> TableDumpStatus.EMPTY
    }

    @Suppress("DEPRECATION", "InflateParams")
    fun measureColumnSize(context: Context): List<Int> {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.bigbrother_cell_content, null, false)
            .cast(AppCompatTextView::class)

        val paint = view.paint
        val padding = view.paddingStart + view.paddingEnd

        val manager = context.getSystemService(WindowManager::class.java)
        val fullWidth = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            manager.currentWindowMetrics.bounds.width()
        } else {
            val metrics = DisplayMetrics()
            manager.defaultDisplay.getMetrics(metrics)
            metrics.widthPixels
        }

        val result = mutableListOf(0)
        columnNames.forEach { column ->
            var currentSize = paint.measureText(column)
            data.forEach { row ->
                val dataSize = paint.measureText(row[column])
                if (dataSize > currentSize) currentSize = dataSize
            }
            result.add((currentSize + padding).roundToInt())
        }

        if (result.sum() < fullWidth) {
            val sum = result.dropLast(1).sum()
            val math = fullWidth - sum
            result[result.lastIndex] = math
        }

        return result
    }

    class Builder(block: Builder.() -> Unit) {

        val data: MutableList<Map<String, String>> = mutableListOf()
        var rowCount: Int = 0
        var sql: String = ""
        var executionTime: String = "0ms"
        var exception: Throwable? = null

        init {
            val start = System.currentTimeMillis()
            runCatching(block).onFailure { exception = it }
            val end = System.currentTimeMillis()
            executionTime = "${end - start}ms"
        }

        fun SQLiteDatabase.runQuery(sql: String, eachLine: Cursor.() -> Unit) {
            this@Builder.sql = sql
            rawQuery(sql, null).use {
                if (it.moveToFirst()) do {
                    rowCount++
                    eachLine(it)
                } while (it.moveToNext())
            }
        }

        fun build() = TableDump(data, sql, rowCount, 0, exception)
    }
}

enum class TableDumpStatus {
    SUCCESS,
    EMPTY,
    ERROR
}