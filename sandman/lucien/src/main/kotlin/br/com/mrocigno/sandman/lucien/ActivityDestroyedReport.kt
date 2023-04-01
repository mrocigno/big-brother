package br.com.mrocigno.sandman.lucien

import br.com.mrocigno.sandman.core.model.ReportModel
import br.com.mrocigno.sandman.core.model.ReportModelType

class ActivityDestroyedReport(private val activityName: String) : ReportModel(ReportModelType.OTHER) {

    override fun asTxt() = "X $activityName - Destroyed"
}