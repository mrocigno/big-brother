package br.com.mrocigno.bigbrother.core.utils

import android.graphics.PointF
import br.com.mrocigno.bigbrother.core.model.ReportModel

private val dataLake = mutableMapOf<String, Any?>()

var lastClickPosition: PointF?
    get() = dataLake["lastClick"] as? PointF
    set(value) { dataLake["lastClick"] = value }

@Suppress("UNCHECKED_CAST")
var localTracker: MutableList<ReportModel>?
    get() = dataLake["localTracker"] as? MutableList<ReportModel>
    set(value) { dataLake["localTracker"] = value }

@Suppress("UNCHECKED_CAST")
val globalTracker: MutableList<ReportModel> get() =
    dataLake["globalTracker"] as? MutableList<ReportModel>
        ?: mutableListOf<ReportModel>().also { dataLake["globalTracker"] = it }

fun <T : ReportModel> T.track(): T = apply {
    localTracker?.add(this) ?: globalTracker.add(this)
}