package br.com.mrocigno.bigbrother.report

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import br.com.mrocigno.bigbrother.common.BBTAG
import br.com.mrocigno.bigbrother.common.utils.decorView
import br.com.mrocigno.bigbrother.common.utils.rootView
import br.com.mrocigno.bigbrother.core.BigBrotherTask
import br.com.mrocigno.bigbrother.core.utils.localTracker
import br.com.mrocigno.bigbrother.core.utils.track

class ReportTask : BigBrotherTask() {

    private val mapping = mutableMapOf<Int, ActivityReport>()
    private var currentRoot: ActivityReport? = null
        set(value) {
            field = value
            localTracker = value?.reportModels
        }

    override fun onActivityCreated(activity: Activity, bundle: Bundle?) {
        activity.rootView.addView(ClickObserverView.getOrCreate(activity))

        val activityReport = ActivityReport(
            name = activity::class.simpleName.toString(),
            parent = currentRoot,
            screenType = "Activity"
        ).track()

        mapping[activity.hashCode()] = activityReport
    }

    override fun onActivityResume(activity: Activity) {
        Log.d(BBTAG, "report task resumed")
        currentRoot = mapping[activity.hashCode()]
    }

    override fun onActivityDestroyed(activity: Activity) {
        Log.d(BBTAG, "report task destroyed")
        mapping.remove(activity.hashCode())
        val destroyed = ActivityDestroyedReport(activity::class.simpleName.toString())
        currentRoot?.reportModels?.add(destroyed)
    }

    override fun onFragmentStarted(fragment: Fragment) {
        fragment.decorView?.addView(ClickObserverView.getOrCreate(fragment))

        currentRoot?.reportModels?.add(ActivityReport(
            name = fragment::class.simpleName.toString(),
            parent = currentRoot,
            screenType = "Fragment"
        ))
    }

    override fun onFragmentStopped(fragment: Fragment) {
        fragment.decorView?.removeView(ClickObserverView.get(fragment))
    }
}
