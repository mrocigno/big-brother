package br.com.mrocigno.bigbrother.network.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import br.com.mrocigno.bigbrother.network.entity.NetworkEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface NetworkDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(entry: NetworkEntry): Long

    @Query("SELECT * FROM tblNetwork WHERE session_id = :sessionId ORDER BY hour DESC")
    fun getBySession(sessionId: Long): Flow<List<NetworkEntry>>

    @Query("DELETE FROM tblNetwork WHERE session_id = :sessionId")
    suspend fun clearSession(sessionId: Long)

    @Query("SELECT * FROM tblNetwork WHERE id = :id")
    fun getById(id: Long): Flow<NetworkEntry>
}