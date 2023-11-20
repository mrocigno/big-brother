package br.com.mrocigno.bigbrother.database.ui.table

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import br.com.mrocigno.bigbrother.core.utils.getTask
import br.com.mrocigno.bigbrother.database.DatabaseHelper
import br.com.mrocigno.bigbrother.database.DatabaseTask
import br.com.mrocigno.bigbrother.database.model.TableDump
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TableInspectorViewModel(
    private val tableName: String,
    private val dbName: String
) : ViewModel() {

    private val _tableDump: MutableLiveData<TableDump> = MutableLiveData()
    val tableDump: LiveData<TableDump> get() = _tableDump
    val isLoading: MutableLiveData<Boolean> = MutableLiveData(false)

    private val dbHelper: DatabaseHelper by lazy {
        checkNotNull(getTask(DatabaseTask::class)?.databases?.get(dbName))
    }

    fun listAll() = viewModelScope.launch {
        isLoading.value = true
        _tableDump.postValue(dbHelper.listAll(tableName))
        delay(300)
        isLoading.value = false
    }

    fun executeSQL(sql: String) = viewModelScope.launch {
        isLoading.postValue(true)
        _tableDump.postValue(dbHelper.execSQL(sql))
        delay(300)
        isLoading.postValue(false)
    }

    fun filterColumn(columnIndex: Int) {
        val columnName = tableDump.value!!.columnNames[columnIndex - 1]
        dbHelper.getColumnData(tableName, columnName)
    }

    class Factory(
        private val tableName: String,
        private val dbName: String
    ) : ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return TableInspectorViewModel(tableName, dbName) as T
        }
    }
}