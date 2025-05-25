package br.com.mrocigno.bigbrother.common.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import br.com.mrocigno.bigbrother.common.converter.LocalDateTimeConverter
import br.com.mrocigno.bigbrother.common.dao.CrashDao
import br.com.mrocigno.bigbrother.common.dao.DeeplinkDao
import br.com.mrocigno.bigbrother.common.dao.LogDao
import br.com.mrocigno.bigbrother.common.dao.NetworkDao
import br.com.mrocigno.bigbrother.common.dao.ProxyDao
import br.com.mrocigno.bigbrother.common.dao.ReportLogDao
import br.com.mrocigno.bigbrother.common.dao.SessionDao
import br.com.mrocigno.bigbrother.common.entity.CrashEntity
import br.com.mrocigno.bigbrother.common.entity.DeeplinkEntity
import br.com.mrocigno.bigbrother.common.entity.LogEntity
import br.com.mrocigno.bigbrother.common.entity.NetworkEntity
import br.com.mrocigno.bigbrother.common.entity.ProxyActionEntity
import br.com.mrocigno.bigbrother.common.entity.ProxyRuleEntity
import br.com.mrocigno.bigbrother.common.entity.ReportLogEntity
import br.com.mrocigno.bigbrother.common.entity.SessionEntity

@Database(
    version = 8,
    entities = [
        SessionEntity::class,
        ReportLogEntity::class,
        LogEntity::class,
        NetworkEntity::class,
        CrashEntity::class,
        ProxyRuleEntity::class,
        ProxyActionEntity::class,
        DeeplinkEntity::class
    ]
)
@TypeConverters(LocalDateTimeConverter::class)
abstract class BigBrotherDatabase : RoomDatabase() {

    abstract fun sessionDao(): SessionDao

    abstract fun reportLogDao(): ReportLogDao

    abstract fun logDao(): LogDao

    abstract fun networkDao(): NetworkDao

    abstract fun crashDao(): CrashDao

    abstract fun proxyDao(): ProxyDao

    abstract fun deeplinkDao(): DeeplinkDao

    companion object {

        var bbdb: BigBrotherDatabase? = null
    }
}
