package br.com.mrocigno.bigbrother.report

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import br.com.mrocigno.bigbrother.common.BBTAG
import br.com.mrocigno.bigbrother.common.utils.decorView
import br.com.mrocigno.bigbrother.common.utils.rootView
import br.com.mrocigno.bigbrother.core.BigBrotherTask
import br.com.mrocigno.bigbrother.report.BigBrotherReport.nestedLevel
import br.com.mrocigno.bigbrother.report.model.ReportType
import br.com.mrocigno.bigbrother.report.ui.ClickObserverView

internal class ReportTask : BigBrotherTask() {

    private val mapping = mutableMapOf<Int, Int>()

    override fun onCreate(): Boolean {
        try {
            BigBrotherReport.createSession()
        } catch (e: Exception) {
            Log.e(BBTAG, "failed to initialize big brother report task", e)
            return false
        }

        return super.onCreate()
    }

    override fun onActivityCreated(activity: Activity, bundle: Bundle?) {
        mapping[activity.hashCode()] = ++nestedLevel
        activity.rootView.addView(ClickObserverView.getOrCreate(activity))
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

    override fun onFragmentStarted(fragment: Fragment) {
        fragment.decorView?.addView(ClickObserverView.getOrCreate(fragment))
    }

    override fun onFragmentStopped(fragment: Fragment) {
        fragment.decorView?.removeView(ClickObserverView.get(fragment))
    }
}
