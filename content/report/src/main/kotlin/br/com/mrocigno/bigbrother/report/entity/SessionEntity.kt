package br.com.mrocigno.bigbrother.report.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import br.com.mrocigno.bigbrother.report.model.SessionStatus
import org.threeten.bp.LocalDateTime

@Entity(tableName = "tblSessions")
internal data class SessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo(name = "date_time") val dateTime: LocalDateTime,
    @ColumnInfo(name = "status") val status: SessionStatus
)