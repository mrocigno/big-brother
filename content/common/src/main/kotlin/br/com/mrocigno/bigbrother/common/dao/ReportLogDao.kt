package br.com.mrocigno.bigbrother.common.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import br.com.mrocigno.bigbrother.common.entity.ReportLogEntity

@Dao
interface ReportLogDao {

    @Insert
    suspend fun add(report: ReportLogEntity): Long

    @Query("SELECT * FROM tblReportLogs WHERE session_id = :sessionId")
    suspend fun getSession(sessionId: Long): List<ReportLogEntity>
}
