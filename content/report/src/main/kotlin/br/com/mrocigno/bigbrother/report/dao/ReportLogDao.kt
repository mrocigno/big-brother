package br.com.mrocigno.bigbrother.report.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import br.com.mrocigno.bigbrother.report.entity.ReportLogEntity

@Dao
internal interface ReportLogDao {

    @Insert
    suspend fun add(report: ReportLogEntity): Long

    @Query("SELECT * FROM tblReportLogs WHERE session_id = :sessionId")
    suspend fun getSession(sessionId: Long): List<ReportLogEntity>
}
