package br.com.mrocigno.bigbrother.session

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import br.com.mrocigno.bigbrother.session.coverter.LocalDateTimeConverter
import br.com.mrocigno.bigbrother.session.dao.ReportLogDao
import br.com.mrocigno.bigbrother.session.dao.SessionDao
import br.com.mrocigno.bigbrother.session.entity.ReportLogEntity
import br.com.mrocigno.bigbrother.session.entity.SessionEntity

@Database(entities = [
    SessionEntity::class,
    ReportLogEntity::class
], version = 1)
@TypeConverters(LocalDateTimeConverter::class)
internal abstract class BigBrotherDatabase : RoomDatabase() {

    abstract fun sessionDao(): SessionDao

    abstract fun reportLogDao(): ReportLogDao
}