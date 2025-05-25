package br.com.mrocigno.bigbrother.common.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import br.com.mrocigno.bigbrother.common.entity.DeeplinkEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DeeplinkDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inset(model: DeeplinkEntity): Long

    @Delete
    suspend fun delete(model: DeeplinkEntity)

    @Query("DELETE FROM tblDeeplink")
    suspend fun deleteAll()

    @Query("SELECT * FROM tblDeeplink ORDER BY time DESC")
    fun getAll(): Flow<List<DeeplinkEntity>>
}
