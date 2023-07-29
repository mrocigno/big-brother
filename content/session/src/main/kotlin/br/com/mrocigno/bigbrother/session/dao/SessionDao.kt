package br.com.mrocigno.bigbrother.session.dao

import androidx.room.Dao
import androidx.room.Insert
import br.com.mrocigno.bigbrother.session.entity.SessionEntity
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
}