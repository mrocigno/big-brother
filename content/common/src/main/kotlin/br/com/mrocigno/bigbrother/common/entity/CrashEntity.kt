package br.com.mrocigno.bigbrother.common.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import br.com.mrocigno.bigbrother.common.utils.bbSessionId
import org.threeten.bp.LocalDateTime

@Entity("tblCrash")
class CrashEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") val id: Long = 0,
    @ColumnInfo(name = "session_id") val sessionId: Long = bbSessionId,
    @ColumnInfo(name = "activity_name") val activityName: String,
    @ColumnInfo(name = "stack_trace") val stackTrace: String? = null,
    @ColumnInfo(name = "time") val time: LocalDateTime = LocalDateTime.now()
)