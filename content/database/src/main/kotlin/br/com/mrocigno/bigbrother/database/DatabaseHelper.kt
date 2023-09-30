package br.com.mrocigno.bigbrother.database

import android.database.sqlite.SQLiteDatabase
import java.io.File

internal class DatabaseHelper
    @Throws(IllegalStateException::class)
    constructor(file: File) {

    var tablesName: List<String> = emptyList()
        private set

    private var readableDatabase: SQLiteDatabase? = null

    init {
        readableDatabase = SQLiteDatabase.openDatabase(file.path, null, SQLiteDatabase.OPEN_READONLY)
        tablesName = listTablesName()
        if (tablesName.isEmpty()) error("no tables in ${file.name} database")
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