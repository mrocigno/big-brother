package br.com.mrocigno.sandman.lucien

import br.com.mrocigno.sandman.common.utils.appendSpace
import br.com.mrocigno.sandman.core.model.ReportModel
import br.com.mrocigno.sandman.core.model.ReportModelType
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
class ActivityTrack(
    val parent: ActivityTrack? = null,
    val name: String,
    val screenType: String,
    val reportModels: MutableList<ReportModel> = mutableListOf()
) : ReportModel(
    type = ReportModelType.TRACK,
    trackable = false
) {

    @IgnoredOnParcel
    val lvl: Int = countLvl(1, parent)

    private fun countLvl(current: Int, activityTrack: ActivityTrack?): Int =
        if (activityTrack == null) current
        else countLvl(current + 1, activityTrack.parent)

    override fun asTxt() = StringBuilder().apply {
        appendName()
        appendReports()
    }.toString()


    private fun StringBuilder.appendName() {
        append("----> $name")
    }

    private fun StringBuilder.appendReports() {
        if (reportModels.isEmpty()) {
            appendLine()
            appendSpace(lvl)
            append("> -- nothing --")
        } else {
            reportModels.forEach {
                appendLine()
                appendSpace(lvl)
                append(it.asTxt())
            }
        }
    }
}
