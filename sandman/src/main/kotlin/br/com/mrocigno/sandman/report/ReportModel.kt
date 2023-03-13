package br.com.mrocigno.sandman.report

import android.os.Parcelable
import org.threeten.bp.LocalDateTime

abstract class ReportModel(
    val time: LocalDateTime = LocalDateTime.now(),
    val type: ReportModelType
) : Parcelable

enum class ReportModelType {
    TRACK,
    NETWORK,
    LOG;
}