package br.com.mrocigno.bigbrother.report.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import br.com.mrocigno.bigbrother.core.utils.bbSessionId
import br.com.mrocigno.bigbrother.report.model.ReportType
import org.threeten.bp.LocalDateTime

@Entity(tableName = "tblReportLogs")
internal class ReportLogEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") val id: Long = 0,

    @ColumnInfo(name = "session_id") val sessionId: Long = bbSessionId,
    @ColumnInfo(name = "nested_level") val nestedLevel: Int = 0,
    @ColumnInfo(name = "type") val type: ReportType,
    @ColumnInfo(name = "txt_content") val txtContent: String,
    @ColumnInfo(name = "time") val time: LocalDateTime = LocalDateTime.now()
)