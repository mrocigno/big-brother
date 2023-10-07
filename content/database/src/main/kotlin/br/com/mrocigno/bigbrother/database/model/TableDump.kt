package br.com.mrocigno.bigbrother.database.model

import android.content.Context
import android.view.LayoutInflater
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

    fun measureColumnSize(context: Context): List<Int> {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.bigbrother_cell_content, null, false)
            .cast(AppCompatTextView::class)

        val paint = view.paint
        val padding = view.paddingStart + view.paddingEnd

        val result = mutableListOf(0)
        var currentSize = 0f
        columnNames.forEach { column ->
            currentSize = paint.measureText(column)
            data.forEach { row ->
                val dataSize = paint.measureText(row[column])
                if (dataSize > currentSize) currentSize = dataSize
            }
            result.add((currentSize + padding).roundToInt())
            currentSize = 0f
        }
        return result
    }

    private fun <T : Any> Any.cast(clazz: KClass<T>): T {
        return clazz.cast(this)
    }
}