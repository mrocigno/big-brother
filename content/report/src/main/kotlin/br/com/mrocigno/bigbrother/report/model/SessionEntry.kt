package br.com.mrocigno.bigbrother.report.model

import br.com.mrocigno.bigbrother.common.entity.SessionEntity
import br.com.mrocigno.bigbrother.report.model.SessionStatus.RUNNING
import org.threeten.bp.LocalDateTime

internal class SessionEntry(
    val id: Long = 0,
    val dateTime: LocalDateTime = LocalDateTime.now(),
    val status: SessionStatus = RUNNING
) {

    constructor(entity: SessionEntity) : this(
        id = entity.id,
        dateTime = entity.dateTime,
        status = SessionStatus.valueOf(entity.status)
    )

    fun toEntity() = SessionEntity(
        id = id,
        dateTime = dateTime,
        status = status.name
    )
}