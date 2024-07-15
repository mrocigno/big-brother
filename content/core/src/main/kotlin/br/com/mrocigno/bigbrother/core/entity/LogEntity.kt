package br.com.mrocigno.bigbrother.core.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import br.com.mrocigno.bigbrother.core.utils.bbSessionId
import org.threeten.bp.LocalDateTime

@Entity(tableName = "tblLog")
class LogEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") val id: Long = 0,
    @ColumnInfo(name = "session_id") val sessionId: Long = bbSessionId,
    @ColumnInfo(name = "lvl") val lvl: String,
    @ColumnInfo(name = "tag") val tag: String,
    @ColumnInfo(name = "message") val message: String? = null,
    @ColumnInfo(name = "error_message") val errorMessage: String? = null,
    @ColumnInfo(name = "error_stacktrace") val errorStacktrace: String? = null,
    @ColumnInfo(name = "time") val time: LocalDateTime = LocalDateTime.now()
)