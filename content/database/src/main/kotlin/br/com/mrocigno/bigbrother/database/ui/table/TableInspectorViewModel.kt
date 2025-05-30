package br.com.mrocigno.bigbrother.database.ui.table

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import br.com.mrocigno.bigbrother.common.helpers.SQLBuilder
import br.com.mrocigno.bigbrother.core.utils.getBigBrotherTask
import br.com.mrocigno.bigbrother.database.DatabaseHelper
import br.com.mrocigno.bigbrother.database.DatabaseTask
import br.com.mrocigno.bigbrother.database.model.TableDump
import br.com.mrocigno.bigbrother.database.ui.table.filter.FilterData
import br.com.mrocigno.bigbrother.database.ui.table.filter.FilterSort
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

internal class TableInspectorViewModel(
    private val tableName: String,
    private val dbName: String
) : ViewModel() {

    private val _tableDump: MutableLiveData<TableDump> = MutableLiveData()
    val tableDump: LiveData<TableDump> get() = _tableDump
    val isLoading: MutableLiveData<Boolean> = MutableLiveData(false)

    private val filters = hashMapOf<Int, FilterData>()

    private val dbHelper: DatabaseHelper by lazy {
        checkNotNull(getBigBrotherTask(DatabaseTask::class)?.databases?.get(dbName))
    }

    fun listAll() = viewModelScope.launch {
        isLoading.value = true
        async(Dispatchers.IO) {
            _tableDump.postValue(dbHelper.listAll(tableName))
        }.await()
        isLoading.value = false
    }

    fun executeSQL(sql: String) = CoroutineScope(Dispatchers.IO).launch {
        isLoading.postValue(true)
        async(Dispatchers.IO) {
            _tableDump.postValue(dbHelper.execSQL(sql))
        }.await()
        isLoading.postValue(false)
    }

    fun getColumnContent(sql: String): LiveData<TableDump> =
        MutableLiveData<TableDump>().apply {
            viewModelScope.launch {
                isLoading.value = true
                postValue(dbHelper.execSQL(sql))
                isLoading.value = false
            }
        }

    fun getColumnData(columnIndex: Int) = filters[columnIndex] ?: run {
        val columnName = tableDump.value!!.columnNames[columnIndex - 1]
        FilterData(dump = dbHelper.getColumnData(tableName, columnName))
    }

    fun filterColumn(columnIndex: Int, filterData: FilterData) = runCatching {
        filters[columnIndex] = filterData

        val sqlBuilder = SQLBuilder(tableDump.value?.sql) {
            clearConditions()
            clearOrderBy()

            filters.values.forEach {
                condition("AND", it.whereStatement)
                when (it.sort) {
                    FilterSort.ASC -> orderAsc(it.columnName)
                    FilterSort.DESC -> orderDesc(it.columnName)
                    null -> Unit
                }
            }
        }

        executeSQL(sqlBuilder.getSelectSql())
    }.onFailure {
        _tableDump.postValue(TableDump(exception = it))
    }

    class Factory(
        private val tableName: String,
        private val dbName: String
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return TableInspectorViewModel(tableName, dbName) as T
        }
    }
}