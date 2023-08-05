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
import com.jakewharton.threetenabp.AndroidThreeTen

class ReportTask : BigBrotherTask() {

    private val mapping = mutableMapOf<Int, Int>()

    override fun onCreate(): Boolean {
        try {
            val context = context ?: throw IllegalStateException("context is null")
            AndroidThreeTen.init(context)
            BigBrotherReport.createSession(context)
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
        mapping.remove(activity.hashCode())
        bbTrack(ReportType.TRACK) {
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
