package br.com.mrocigno.bigbrother.report

import br.com.mrocigno.bigbrother.common.utils.appendSpace
import br.com.mrocigno.bigbrother.core.model.ReportModel
import br.com.mrocigno.bigbrother.core.model.ReportModelType

class ActivityReport(
    val parent: ActivityReport? = null,
    val name: String,
    val screenType: String,
    val reportModels: MutableList<ReportModel> = mutableListOf()
) : ReportModel(ReportModelType.TRACK) {

    val lvl: Int = countLvl(1, parent)

    private fun countLvl(current: Int, activityReport: ActivityReport?): Int =
        if (activityReport == null) current
        else countLvl(current + 1, activityReport.parent)

    override fun asTxt() = StringBuilder()
        .append("----> $name")
        .appendReports()
        .toString()

    private fun StringBuilder.appendReports() = apply {
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
