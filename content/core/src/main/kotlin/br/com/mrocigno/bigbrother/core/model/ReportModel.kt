package br.com.mrocigno.bigbrother.core.model

import org.threeten.bp.LocalDateTime
import java.io.Serializable

abstract class ReportModel(
    val type: ReportModelType,
    val time: LocalDateTime = LocalDateTime.now()
) : Serializable {

    open fun asTxt(): String = "> ${type.name}"
}

enum class ReportModelType {
    TRACK,
    NETWORK,
    LOG,
    CRASH,
    OTHER;
}