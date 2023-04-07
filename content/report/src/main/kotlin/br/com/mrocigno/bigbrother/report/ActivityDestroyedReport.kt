package br.com.mrocigno.bigbrother.report

import br.com.mrocigno.bigbrother.core.model.ReportModel
import br.com.mrocigno.bigbrother.core.model.ReportModelType

class ActivityDestroyedReport(private val activityName: String) : ReportModel(ReportModelType.OTHER) {

    override fun asTxt() = "X $activityName - Destroyed"
}