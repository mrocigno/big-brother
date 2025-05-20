package br.com.mrocigno.bigbrother.report

import android.app.Activity
import android.os.Bundle
import android.util.Log
import br.com.mrocigno.bigbrother.common.BBTAG
import br.com.mrocigno.bigbrother.core.BigBrotherTask
import br.com.mrocigno.bigbrother.report.BigBrotherReport.nestedLevel
import br.com.mrocigno.bigbrother.report.model.ReportType

internal class ReportTask : BigBrotherTask() {

    private val mapping = mutableMapOf<Int, Int>()
    override val priority: Int = 2

    override fun onStartTask() {
        try {
            BigBrotherReport.createSession()
        } catch (e: Exception) {
            Log.e(BBTAG, "failed to initialize big brother report task", e)
        }
    }

    override fun onActivityCreated(activity: Activity, bundle: Bundle?) {
        mapping[activity.hashCode()] = ++nestedLevel
        bbTrack(ReportType.TRACK) {
            "---> ${activity::class.simpleName}"
        }
    }

    override fun onActivityResume(activity: Activity) {
        nestedLevel = mapping[activity.hashCode()] ?: 0
    }

    override fun onActivityDestroyed(activity: Activity) {
        val lvl = mapping[activity.hashCode()] ?: nestedLevel
        mapping.remove(activity.hashCode())

        bbTrack(ReportType.TRACK, lvl) {
            "---x ${activity::class.simpleName}"
        }
    }
}
