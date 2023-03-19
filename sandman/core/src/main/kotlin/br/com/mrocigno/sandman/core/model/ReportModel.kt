package br.com.mrocigno.sandman.core.model

import android.os.Parcelable
import org.threeten.bp.LocalDateTime

@Suppress("LeakingThis")
abstract class ReportModel(
    val time: LocalDateTime = LocalDateTime.now(),
    val type: ReportModelType,
    private val trackable: Boolean = true
) : Parcelable {

    init {
        if (trackable) br.com.mrocigno.sandman.core.utils.localTracker?.add(this)
    }
}

enum class ReportModelType {
    TRACK,
    NETWORK,
    LOG;
}