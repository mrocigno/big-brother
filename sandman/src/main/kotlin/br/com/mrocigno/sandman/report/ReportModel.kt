package br.com.mrocigno.sandman.report

import android.os.Parcelable
import br.com.mrocigno.sandman.utils.localTracker
import org.threeten.bp.LocalDateTime

@Suppress("LeakingThis")
abstract class ReportModel(
    val time: LocalDateTime = LocalDateTime.now(),
    val type: ReportModelType,
    private val trackable: Boolean = true
) : Parcelable {

    init {
        if (trackable) localTracker?.add(this)
    }
}

enum class ReportModelType {
    TRACK,
    NETWORK,
    LOG;
}