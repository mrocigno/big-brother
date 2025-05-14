package br.com.mrocigno.bigbrother.database.ui.table.filter

import androidx.core.text.isDigitsOnly
import br.com.mrocigno.bigbrother.database.model.TableDump

internal data class FilterData(
    val sort: FilterSort? = null,
    val search: String? = null,
    val dump: TableDump
) {

    val columnName: String = dump.columnNames.firstOrNull().orEmpty()

    val whereStatement get() =
        "$columnName = ${if (search.orEmpty().isDigitsOnly()) search else "'$search'"}"
            .takeIf { !search.isNullOrBlank() }

    val sortStatement get() = "ORDER BY $columnName ${sort?.name}".takeIf { sort != null }

    fun isNotEmpty() =
        sort != null || search != null || !dump.isEmpty
}

enum class FilterSort {
    ASC,
    DESC;
}