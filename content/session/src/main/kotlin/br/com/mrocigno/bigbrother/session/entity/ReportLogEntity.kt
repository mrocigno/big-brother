package br.com.mrocigno.bigbrother.session.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import br.com.mrocigno.bigbrother.core.utils.bbSessionId
import org.threeten.bp.LocalDateTime

@Entity(tableName = "tblReportLogs", primaryKeys = ["id", "time"])
class ReportLogEntity(
    @ColumnInfo(name = "id") val id: Long,
    @ColumnInfo(name = "session_id") val sessionId: Long = bbSessionId ?: -1,
    @ColumnInfo(name = "type") val type: String,
    @ColumnInfo(name = "txt_content") val txtContent: String,
    @ColumnInfo(name = "html_content") val htmlContent: String,
    @ColumnInfo(name = "time") val time: LocalDateTime = LocalDateTime.now()
)