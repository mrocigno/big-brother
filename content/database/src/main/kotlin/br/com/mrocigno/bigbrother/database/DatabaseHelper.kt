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
        val result = mutableListOf("sqlite_master")
        val db = readableDatabase ?: return result
        db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null).use {
            it.moveToFirst()
            do {
                result.add(it.getString(0))
            } while (it.moveToNext())
        }

        return result
    }

    fun listAll(tableName: String) =
        execSQL("SELECT * FROM $tableName")

    fun getColumnData(tableName: String, columnName: String) =
        execSQL("SELECT $columnName FROM $tableName GROUP BY $columnName LIMIT 10 ")

    fun execSQL(sql: String) =
        TableDump.Builder {
            val db = readableDatabase ?: error("readableDatabase cannot be reached")
            db.runQuery(sql) {
                val rowContent = mutableMapOf<String, String>()
                columnNames.forEach { columnName ->
                    val columnIndex = getColumnIndex(columnName)
                    rowContent[columnName] = getString(columnIndex)
                }
                data.add(rowContent)
            }
        }.build()
}