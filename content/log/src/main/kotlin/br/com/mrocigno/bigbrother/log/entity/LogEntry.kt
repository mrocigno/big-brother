package br.com.mrocigno.bigbrother.log.entity

import androidx.annotation.ColorRes
import androidx.recyclerview.widget.DiffUtil
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import br.com.mrocigno.bigbrother.core.utils.bbSessionId
import br.com.mrocigno.bigbrother.report.bbTrack
import br.com.mrocigno.bigbrother.report.model.ReportType
import org.threeten.bp.LocalDateTime
import br.com.mrocigno.bigbrother.common.R as CommonR

@Entity(tableName = "tblLog")
class LogEntry(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") val id: Long = 0,
    @ColumnInfo(name = "session_id") val sessionId: Long = bbSessionId,
    @ColumnInfo(name = "lvl") val lvl: LogEntryType,
    @ColumnInfo(name = "tag") val tag: String,
    @ColumnInfo(name = "message") val message: String? = null,
    @ColumnInfo(name = "error_message") val errorMessage: String? = null,
    @ColumnInfo(name = "error_stacktrace") val errorStacktrace: String? = null,
    @ColumnInfo(name = "time") val time: LocalDateTime = LocalDateTime.now()
) {

    fun track() = runCatching {
        bbTrack(ReportType.LOG) {
            "> LOG - ${lvl.initial} - ${message ?: errorMessage ?: "empty"}"
        }
    }

    override fun toString() = StringBuilder()
        .appendLine(tag)
        .appendLine(message)
        .appendLine(errorMessage.toString())
        .toString()

    class Differ : DiffUtil.ItemCallback<LogEntry>() {
        override fun areItemsTheSame(oldItem: LogEntry, newItem: LogEntry) =
            newItem.tag == oldItem.tag
                && newItem.message == oldItem.message

        override fun areContentsTheSame(oldItem: LogEntry, newItem: LogEntry) = false
    }
}

enum class LogEntryType(
    val initial: String,
    @ColorRes val bgColor: Int,
    @ColorRes val fgColor: Int,
    @ColorRes val textColor: Int,
) {
    ERROR(
        "E",
        CommonR.color.icon_negative,
        android.R.color.white,
        CommonR.color.icon_negative
    ),
    WARN(
        "W",
        CommonR.color.text_highlight,
        android.R.color.black,
        CommonR.color.text_highlight
    ),
    DEBUG(
        "D",
        CommonR.color.boy_red,
        android.R.color.white,
        CommonR.color.text_paragraph
    ),
    VERBOSE(
        "V",
        CommonR.color.text_title,
        CommonR.color.text_title_inverse,
        CommonR.color.text_paragraph
    ),
    INFO(
        "I",
        CommonR.color.text_hyperlink,
        android.R.color.white,
        CommonR.color.text_title
    ),
    ASSERT(
        "WTF",
        CommonR.color.icon_negative,
        android.R.color.white,
        CommonR.color.icon_negative
    )
}