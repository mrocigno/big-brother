package br.com.mrocigno.bigbrother.network.dao

import androidx.room.Dao
import androidx.room.Insert
import br.com.mrocigno.bigbrother.network.entity.NetworkEntry

@Dao
interface NetworkDao {

    @Insert
    fun add(entry: NetworkEntry): Long
}