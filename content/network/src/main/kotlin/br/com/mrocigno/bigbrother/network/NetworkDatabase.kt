package br.com.mrocigno.bigbrother.network

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import br.com.mrocigno.bigbrother.common.converter.LocalDateTimeConverter
import br.com.mrocigno.bigbrother.network.dao.NetworkDao
import br.com.mrocigno.bigbrother.network.entity.NetworkEntry

@Database(entities = [
    NetworkEntry::class
], version = 1)
@TypeConverters(LocalDateTimeConverter::class)
internal abstract class NetworkDatabase : RoomDatabase() {

    abstract fun networkDao(): NetworkDao
}