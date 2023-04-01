package br.com.mrocigno.sandman.core.model

import android.os.Parcelable
import br.com.mrocigno.sandman.core.utils.localTracker
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

    open fun asTxt(): String = "- ${type.name}"
}

enum class ReportModelType {
    TRACK,
    NETWORK,
    LOG,
    OTHER;
}