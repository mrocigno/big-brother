package br.com.mrocigno.bigbrother.log

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import br.com.mrocigno.bigbrother.common.converter.LocalDateTimeConverter
import br.com.mrocigno.bigbrother.log.dao.LogDao
import br.com.mrocigno.bigbrother.log.entity.LogEntry

@Database(entities = [
    LogEntry::class
], version = 1)
@TypeConverters(LocalDateTimeConverter::class)
internal abstract class LogDatabase : RoomDatabase() {

    abstract fun logDao(): LogDao
}