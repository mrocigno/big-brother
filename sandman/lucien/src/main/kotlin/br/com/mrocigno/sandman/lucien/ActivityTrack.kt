package br.com.mrocigno.sandman.lucien

import br.com.mrocigno.sandman.core.model.ReportModel
import br.com.mrocigno.sandman.core.model.ReportModelType
import kotlinx.parcelize.Parcelize

@Parcelize
class ActivityTrack(
    val tracker: MutableList<ActivityTrack> = mutableListOf(),
    val parent: ActivityTrack? = null,
    val name: String,
    val screenType: String,
    val reportModels: MutableList<ReportModel> = mutableListOf()
) : ReportModel(
    type = ReportModelType.TRACK,
    trackable = false
)