package br.com.mrocigno.sandman.report

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import br.com.mrocigno.sandman.isOutOfDomain
import br.com.mrocigno.sandman.morpheus.MorpheusTask
import br.com.mrocigno.sandman.utils.decorView
import br.com.mrocigno.sandman.utils.rootView
import kotlinx.parcelize.Parcelize

class ReportTask : MorpheusTask() {

    private val tracker = mutableListOf<ActivityTrack>()
    private var currentRoot: ActivityTrack? = null

    override fun onActivityCreated(activity: Activity, bundle: Bundle?) {
        activity.rootView.addView(ClickObserverView.getOrCreate(activity))

        if (activity.isOutOfDomain) return
        val tracked = ActivityTrack(
            name = activity::class.simpleName.toString(),
            parent = currentRoot,
            screenType = "Activity"
        )

        if (currentRoot == null) tracker.add(tracked) else currentRoot?.tracker?.add(tracked)
        currentRoot = tracked
    }

    override fun onActivityDestroyed(activity: Activity) {
        currentRoot = currentRoot?.parent
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

@Parcelize
class ActivityTrack(
    val tracker: MutableList<ActivityTrack> = mutableListOf(),
    val parent: ActivityTrack? = null,
    val name: String,
    val screenType: String // Activity/Fragment
) : ReportModel(type = ReportModelType.TRACK)