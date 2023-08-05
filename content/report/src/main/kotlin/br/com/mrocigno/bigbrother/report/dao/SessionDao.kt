package br.com.mrocigno.bigbrother.report.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import br.com.mrocigno.bigbrother.report.entity.SessionEntity
import org.threeten.bp.LocalDateTime

@Dao
internal interface SessionDao {

    @Insert
    suspend fun create(
        entity: SessionEntity = SessionEntity(
            id = 0,
            dateTime = LocalDateTime.now(),
            status = "RUNNING"
        )
    ): Long

    @Query("DELETE FROM tblSessions WHERE id = :id")
    suspend fun deleteSession(id: Long)

    @Query("UPDATE tblSessions SET status = 'FINISHED' WHERE status = 'RUNNING'")
    suspend fun closePreviousSession()

    @Query("UPDATE tblSessions SET status = 'CRASHED' WHERE id = :sessionId")
    suspend fun sessionCrashed(sessionId: Long)
}