package br.com.mrocigno.bigbrother.log.model

import androidx.recyclerview.widget.DiffUtil
import br.com.mrocigno.bigbrother.core.entity.LogEntity
import br.com.mrocigno.bigbrother.core.utils.bbSessionId
import br.com.mrocigno.bigbrother.report.bbTrack
import br.com.mrocigno.bigbrother.report.model.ReportType
import org.threeten.bp.LocalDateTime

class LogEntry(
    val id: Long = 0,
    val sessionId: Long = bbSessionId,
    val lvl: LogEntryType,
    val tag: String,
    val message: String? = null,
    val errorMessage: String? = null,
    val errorStacktrace: String? = null,
    val time: LocalDateTime = LocalDateTime.now()
) {

    constructor(entity: LogEntity) : this(
        id = entity.id,
        sessionId = entity.sessionId,
        lvl = LogEntryType.valueOf(entity.lvl),
        tag = entity.tag,
        message = entity.message,
        errorMessage = entity.errorMessage,
        errorStacktrace = entity.errorStacktrace,
        time = entity.time
    )

    fun track() = runCatching {
        bbTrack(ReportType.LOG) {
            "> LOG - ${lvl.initial} - ${message ?: errorMessage ?: "empty"}"
        }
    }

    fun toEntity() = LogEntity(
        id = id,
        sessionId = sessionId,
        lvl = lvl.name,
        tag = tag,
        message = message,
        errorMessage = errorMessage,
        errorStacktrace = errorStacktrace,
        time = time
    )

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