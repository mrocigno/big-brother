package br.com.mrocigno.bigbrother.core

import android.app.Activity
import android.graphics.PointF
import android.os.Bundle
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import br.com.mrocigno.bigbrother.common.utils.decorView
import br.com.mrocigno.bigbrother.common.utils.rootView

class BigBrotherWatchTask : BigBrotherTask() {

    private val lastPoint = PointF(0f, 200f)
    private var alive = true

    override fun onActivityCreated(activity: Activity, bundle: Bundle?) {
        if (!alive) return
        activity.rootView.addView(BigBrotherView.getOrCreate(activity) {
            kill(activity.rootView)
        })
    }

    override fun onActivityPaused(activity: Activity) {
        val bigBrotherView = BigBrotherView.get(activity) ?: return
        lastPoint.set(bigBrotherView.x, bigBrotherView.y)
    }

    override fun onActivityStarted(activity: Activity) {
        val bigBrotherView = BigBrotherView.get(activity) ?: return
        if (!alive) {
            kill(activity.rootView)
        } else if (!bigBrotherView.isExpanded) {
            bigBrotherView.x = lastPoint.x
            bigBrotherView.y = lastPoint.y
        }
    }

    override fun onFragmentStarted(fragment: Fragment) {
        if (!alive) return
        fragment.decorView?.addView(BigBrotherView.getOrCreate(fragment) {
            kill(fragment.decorView)
        })
    }

    override fun onFragmentStopped(fragment: Fragment) {
        fragment.decorView?.removeView(BigBrotherView.get(fragment))
    }

    private fun kill(parent: ViewGroup?) {
        val vortex = parent?.findViewById<BigBrotherView>(R.id.bigbrother) ?: return
        alive = false
        parent.removeView(vortex)
    }
}