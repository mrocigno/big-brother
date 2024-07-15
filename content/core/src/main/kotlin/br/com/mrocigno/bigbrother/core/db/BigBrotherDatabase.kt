package br.com.mrocigno.bigbrother.core.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import br.com.mrocigno.bigbrother.common.converter.LocalDateTimeConverter
import br.com.mrocigno.bigbrother.core.dao.LogDao
import br.com.mrocigno.bigbrother.core.dao.NetworkDao
import br.com.mrocigno.bigbrother.core.dao.ReportLogDao
import br.com.mrocigno.bigbrother.core.dao.SessionDao
import br.com.mrocigno.bigbrother.core.entity.LogEntity
import br.com.mrocigno.bigbrother.core.entity.NetworkEntity
import br.com.mrocigno.bigbrother.core.entity.ReportLogEntity
import br.com.mrocigno.bigbrother.core.entity.SessionEntity

@Database(
    version = 1,
    entities = [
        SessionEntity::class,
        ReportLogEntity::class,
        LogEntity::class,
        NetworkEntity::class
    ]
)
@TypeConverters(LocalDateTimeConverter::class)
abstract class BigBrotherDatabase : RoomDatabase() {

    abstract fun sessionDao(): SessionDao

    abstract fun reportLogDao(): ReportLogDao

    abstract fun logDao(): LogDao

    abstract fun networkDao(): NetworkDao
}