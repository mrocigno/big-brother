package br.com.mrocigno.bigbrother.log.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import br.com.mrocigno.bigbrother.log.entity.LogEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface LogDao {

    @Insert
    suspend fun insert(model: LogEntry)

    @Query("SELECT * FROM tblLog WHERE session_id = :sessionId ORDER BY time ASC")
    fun getBySession(sessionId: Long): Flow<List<LogEntry>>

    @Query("DELETE FROM tblLog WHERE session_id = :sessionId")
    suspend fun clearSession(sessionId: Long)

    @Query("SELECT * FROM tblLog WHERE id = :id")
    fun getById(id: Long): Flow<LogEntry>
}