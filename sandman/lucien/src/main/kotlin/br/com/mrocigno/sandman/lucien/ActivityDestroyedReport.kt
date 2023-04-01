package br.com.mrocigno.sandman.lucien

import br.com.mrocigno.sandman.core.model.ReportModel
import br.com.mrocigno.sandman.core.model.ReportModelType
import kotlinx.parcelize.Parcelize

@Parcelize
class ActivityDestroyedReport(
    private val activityName: String
) : ReportModel(
    type = ReportModelType.OTHER,
    trackable = false
) {

    override fun asTxt() = "----X $activityName"
}