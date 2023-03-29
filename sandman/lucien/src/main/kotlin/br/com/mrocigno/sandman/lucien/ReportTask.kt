package br.com.mrocigno.sandman.lucien

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import br.com.mrocigno.sandman.common.utils.decorView
import br.com.mrocigno.sandman.common.utils.rootView
import br.com.mrocigno.sandman.core.MorpheusTask
import br.com.mrocigno.sandman.core.isOutOfDomain
import br.com.mrocigno.sandman.core.utils.globalTracker
import br.com.mrocigno.sandman.core.utils.localTracker

class ReportTask : MorpheusTask() {

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

        if (currentRoot == null)
            globalTracker.add(tracked) else currentRoot?.tracker?.add(tracked)
        currentRoot = tracked
    }

    override fun onActivityStarted(activity: Activity) {
        val activityName = activity::class.simpleName.toString()
        if (activityName != currentRoot?.name) {
            currentRoot = currentRoot?.parent
        }
    }

    override fun onFragmentStarted(fragment: Fragment) {
        if (fragment.isOutOfDomain) return
        fragment.decorView?.addView(ClickObserverView.getOrCreate(fragment))

        currentRoot?.tracker?.add(ActivityTrack(
            name = fragment::class.simpleName.toString(),
            parent = currentRoot,
            screenType = "Fragment"
        ))
    }

    override fun onFragmentStopped(fragment: Fragment) {
        fragment.decorView?.removeView(ClickObserverView.get(fragment))
    }
}
