package br.com.mrocigno.bigbrother.report

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import br.com.mrocigno.bigbrother.report.coverter.LocalDateTimeConverter
import br.com.mrocigno.bigbrother.report.dao.ReportLogDao
import br.com.mrocigno.bigbrother.report.dao.SessionDao
import br.com.mrocigno.bigbrother.report.entity.ReportLogEntity
import br.com.mrocigno.bigbrother.report.entity.SessionEntity

@Database(entities = [
    SessionEntity::class,
    ReportLogEntity::class
], version = 1)
@TypeConverters(LocalDateTimeConverter::class)
internal abstract class ReportDatabase : RoomDatabase() {

    abstract fun sessionDao(): SessionDao

    abstract fun reportLogDao(): ReportLogDao

}