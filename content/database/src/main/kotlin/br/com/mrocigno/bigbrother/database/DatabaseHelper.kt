package br.com.mrocigno.bigbrother.database

import android.database.sqlite.SQLiteDatabase
import java.io.BufferedReader
import java.io.File
import java.io.FileReader

internal class DatabaseHelper
    @Throws(IllegalStateException::class)
    constructor(file: File) {

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
                error("the file is not a database")
        }

    private fun listTablesName(): List<String> {
        val result = mutableListOf<String>()
        val db = readableDatabase ?: return result
        db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null).use {
            it.moveToFirst()
            while (it.moveToNext()) {
                result.add(it.getString(0))
            }
        }

        return result
    }
}