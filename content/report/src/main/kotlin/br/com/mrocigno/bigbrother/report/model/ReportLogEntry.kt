package br.com.mrocigno.bigbrother.report.model

import br.com.mrocigno.bigbrother.common.entity.ReportLogEntity
import br.com.mrocigno.bigbrother.common.utils.bbSessionId
import org.threeten.bp.LocalDateTime

internal class ReportLogEntry(
    val id: Long = 0,
    val sessionId: Long = bbSessionId,
    val nestedLevel: Int = 0,
    val type: ReportType,
    val txtContent: String,
    val time: LocalDateTime = LocalDateTime.now()
) {

    constructor(entity: ReportLogEntity) : this(
        id = entity.id,
        sessionId = entity.sessionId,
        nestedLevel = entity.nestedLevel,
        type = ReportType.valueOf(entity.type),
        txtContent = entity.txtContent,
        time = entity.time
    )

    fun toEntity() = ReportLogEntity(
        id = id,
        sessionId = sessionId,
        nestedLevel = nestedLevel,
        type = type.name,
        txtContent = txtContent,
        time = time
    )
}