package br.com.mrocigno.bigbrother.core.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import br.com.mrocigno.bigbrother.core.entity.SessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionDao {

    @Insert
    fun create(entity: SessionEntity): Long

    @Query("DELETE FROM tblSessions WHERE id = :id")
    suspend fun deleteSession(id: Long)

    @Query("UPDATE tblSessions SET status = 'FINISHED' WHERE status = 'RUNNING'")
    fun closePreviousSession()

    @Query("UPDATE tblSessions SET status = 'CRASHED' WHERE id = :sessionId")
    suspend fun sessionCrashed(sessionId: Long)

    @Query("SELECT * FROM tblSessions ORDER BY id DESC")
    fun getAllSessions(): Flow<List<SessionEntity>>

    @Query("""
        SELECT * FROM tblSessions
        WHERE date_time >= :startDate AND date_time <= :endDate
        ORDER BY id DESC 
    """)
    fun getSessionByRange(startDate: String, endDate: String): Flow<List<SessionEntity>>
}