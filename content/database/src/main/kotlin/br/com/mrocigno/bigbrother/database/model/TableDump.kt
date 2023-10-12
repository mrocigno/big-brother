package br.com.mrocigno.bigbrother.database.model

import android.content.Context
import android.os.Build
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.WindowManager
import androidx.appcompat.widget.AppCompatTextView
import br.com.mrocigno.bigbrother.database.R
import kotlin.math.roundToInt
import kotlin.reflect.KClass
import kotlin.reflect.cast

class TableDump(
    val data: List<Map<String, String>>,
    val executedSql: String,
    val rowCount: Int,
    val page: Int
) {
    val columnNames = data.firstOrNull()?.keys?.toTypedArray().orEmpty()

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

    private fun <T : Any> Any.cast(clazz: KClass<T>): T {
        return clazz.cast(this)
    }
}