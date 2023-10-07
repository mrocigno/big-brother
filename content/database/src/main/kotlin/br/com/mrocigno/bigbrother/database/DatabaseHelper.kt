package br.com.mrocigno.bigbrother.database

import android.database.sqlite.SQLiteDatabase
import br.com.mrocigno.bigbrother.database.model.TableDump
import java.io.BufferedReader
import java.io.File
import java.io.FileReader

internal class DatabaseHelper(file: File) {

    val name: String = file.name

    var tablesName: List<String> = emptyList()
        private set

    private var readableDatabase: SQLiteDatabase? = null

    init {
        try {
            checkDatabaseFile(file)
            readableDatabase = SQLiteDatabase.openDatabase(file.path, null, SQLiteDatabase.OPEN_READONLY)
            tablesName = listTablesName()
            if (tablesName.isEmpty()) error("no tables in ${file.name} database")
        } catch (e: Exception) {
            readableDatabase?.close()
            throw e
        }
    }

    private fun checkDatabaseFile(file: File) =
        BufferedReader(FileReader(file)).use {
            val identification = it.readLine().take(15)
            if (identification != "SQLite format 3")
                error("the file ${file.name} is not a database")
        }

    private fun listTablesName(): List<String> {
        val result = mutableListOf<String>()
        val db = readableDatabase ?: return result
        db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null).use {
            it.moveToFirst()
            do {
                result.add(it.getString(0))
            } while (it.moveToNext())
        }

        return result
    }

    fun listAll(tableName: String) = runCatching {
        val data = mutableListOf<Map<String, String>>()
        val db = readableDatabase ?: return null
        val sql = "SELECT * FROM $tableName"
        var rowCount = 0
        db.rawQuery(sql, null).use {
            if (it.moveToFirst()) do {
                rowCount++
                val rowContent = mutableMapOf<String, String>()
                it.columnNames.forEach { columnName ->
                    val columnIndex = it.getColumnIndex(columnName)
                    rowContent[columnName] = it.getString(columnIndex)
                }
                data.add(rowContent)
            } while (it.moveToNext())
        }
        TableDump(
            data = data,
            executedSql = sql,
            rowCount = rowCount,
            page = 0
        )
    }.getOrNull()

    fun execSQL(sql: String) = runCatching {
        val data = mutableListOf<Map<String, String>>()
        val db = readableDatabase ?: return null
        var rowCount = 0
        db.rawQuery(sql, null).use {
            if (it.moveToFirst()) do {
                rowCount++
                val rowContent = mutableMapOf<String, String>()
                it.columnNames.forEach { columnName ->
                    val columnIndex = it.getColumnIndex(columnName)
                    rowContent[columnName] = it.getString(columnIndex)
                }
                data.add(rowContent)
            } while (it.moveToNext())
        }

        TableDump(
            data = data,
            executedSql = sql,
            rowCount = rowCount,
            page = 0
        )
    }.getOrNull()
}