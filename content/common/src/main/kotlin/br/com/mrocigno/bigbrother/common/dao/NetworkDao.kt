package br.com.mrocigno.bigbrother.common.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import br.com.mrocigno.bigbrother.common.entity.NetworkEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NetworkDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(entry: NetworkEntity): Long

    @Query("SELECT * FROM tblNetwork WHERE session_id = :sessionId ORDER BY hour DESC")
    fun getBySession(sessionId: Long): Flow<List<NetworkEntity>>

    @Query("DELETE FROM tblNetwork WHERE session_id = :sessionId")
    suspend fun clearSession(sessionId: Long)

    @Query("SELECT * FROM tblNetwork WHERE id = :id")
    fun getById(id: Long): Flow<NetworkEntity>

    @Query("SELECT DISTINCT full_url FROM tblNetwork")
    fun listEndpoints(): Flow<List<String>>
}