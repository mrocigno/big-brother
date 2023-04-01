package br.com.mrocigno.sandman.lucien

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import br.com.mrocigno.sandman.common.utils.decorView
import br.com.mrocigno.sandman.common.utils.rootView
import br.com.mrocigno.sandman.core.MorpheusTask
import br.com.mrocigno.sandman.core.utils.localTracker
import br.com.mrocigno.sandman.core.utils.track

class ReportTask : MorpheusTask() {

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
        currentRoot = mapping[activity.hashCode()]
    }

    override fun onActivityDestroyed(activity: Activity) {
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
