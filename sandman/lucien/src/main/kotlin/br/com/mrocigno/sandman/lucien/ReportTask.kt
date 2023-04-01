package br.com.mrocigno.sandman.lucien

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import br.com.mrocigno.sandman.common.utils.decorView
import br.com.mrocigno.sandman.common.utils.rootView
import br.com.mrocigno.sandman.core.MorpheusTask
import br.com.mrocigno.sandman.core.utils.globalTracker
import br.com.mrocigno.sandman.core.utils.localTracker

class ReportTask : MorpheusTask() {

    private val yoBro = mutableMapOf<Activity, ActivityTrack>()

    private var currentRoot: ActivityTrack? = null
        set(value) {
            field = value
            localTracker = value?.reportModels
        }

    override fun onActivityCreated(activity: Activity, bundle: Bundle?) {
        activity.rootView.addView(ClickObserverView.getOrCreate(activity))

        val tracked = ActivityTrack(
            name = activity::class.simpleName.toString(),
            parent = currentRoot,
            screenType = "Activity"
        )

        yoBro[activity] = tracked

        if (currentRoot == null) globalTracker.add(tracked) else currentRoot?.reportModels?.add(tracked)
        currentRoot = tracked
    }

    override fun onActivityResume(activity: Activity) {
        currentRoot = yoBro[activity]
    }

    override fun onActivityDestroyed(activity: Activity) {
        yoBro.remove(activity)
        val destroyed = ActivityDestroyedReport(activity::class.simpleName.toString())
        currentRoot?.reportModels?.add(destroyed)
    }

    override fun onFragmentStarted(fragment: Fragment) {
        fragment.decorView?.addView(ClickObserverView.getOrCreate(fragment))

        currentRoot?.reportModels?.add(ActivityTrack(
            name = fragment::class.simpleName.toString(),
            parent = currentRoot,
            screenType = "Fragment"
        ))
    }

    override fun onFragmentStopped(fragment: Fragment) {
        fragment.decorView?.removeView(ClickObserverView.get(fragment))
    }
}
