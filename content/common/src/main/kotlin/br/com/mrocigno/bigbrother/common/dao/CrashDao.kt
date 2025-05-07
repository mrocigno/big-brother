package br.com.mrocigno.bigbrother.common.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import br.com.mrocigno.bigbrother.common.entity.CrashEntity

@Dao
interface CrashDao {

    @Insert
    suspend fun insert(model: CrashEntity)

    @Query("SELECT * FROM tblCrash WHERE session_id = :sessionId LIMIT 1")
    suspend fun getBySessionId(sessionId: Long): CrashEntity?
}