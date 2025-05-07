package br.com.mrocigno.bigbrother.common.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import br.com.mrocigno.bigbrother.common.converter.LocalDateTimeConverter
import br.com.mrocigno.bigbrother.common.dao.CrashDao
import br.com.mrocigno.bigbrother.common.dao.LogDao
import br.com.mrocigno.bigbrother.common.dao.NetworkDao
import br.com.mrocigno.bigbrother.common.dao.ReportLogDao
import br.com.mrocigno.bigbrother.common.dao.SessionDao
import br.com.mrocigno.bigbrother.common.entity.CrashEntity
import br.com.mrocigno.bigbrother.common.entity.LogEntity
import br.com.mrocigno.bigbrother.common.entity.NetworkEntity
import br.com.mrocigno.bigbrother.common.entity.ReportLogEntity
import br.com.mrocigno.bigbrother.common.entity.SessionEntity

@Database(
    version = 2,
    entities = [
        SessionEntity::class,
        ReportLogEntity::class,
        LogEntity::class,
        NetworkEntity::class,
        CrashEntity::class
    ]
)
@TypeConverters(LocalDateTimeConverter::class)
abstract class BigBrotherDatabase : RoomDatabase() {

    abstract fun sessionDao(): SessionDao

    abstract fun reportLogDao(): ReportLogDao

    abstract fun logDao(): LogDao

    abstract fun networkDao(): NetworkDao

    abstract fun crashDao(): CrashDao

    companion object {

        var bbdb: BigBrotherDatabase? = null
    }
}