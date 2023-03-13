package br.com.mrocigno.sandman.vortex

import android.app.Activity
import android.graphics.PointF
import android.os.Bundle
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import br.com.mrocigno.sandman.R
import br.com.mrocigno.sandman.morpheus.MorpheusTask
import br.com.mrocigno.sandman.utils.decorView
import br.com.mrocigno.sandman.utils.rootView

class VortexTask : MorpheusTask() {

    private val lastPoint = PointF(0f, 200f)
    private var alive = true

    override fun onActivityCreated(activity: Activity, bundle: Bundle?) {
        if (!alive) return
        activity.rootView.addView(VortexView.getOrCreate(activity) {
            kill(activity.rootView)
        })
    }

    override fun onActivityPaused(activity: Activity) {
        val vortex = VortexView.get(activity) ?: return
        lastPoint.set(vortex.x, vortex.y)
    }

    override fun onActivityStarted(activity: Activity) {
        val vortex = VortexView.get(activity) ?: return
        if (!alive) {
            kill(activity.rootView)
        } else if (!vortex.isExpanded) {
            vortex.x = lastPoint.x
            vortex.y = lastPoint.y
        }
    }

    override fun onFragmentStarted(fragment: Fragment) {
        if (!alive) return
        fragment.decorView?.addView(VortexView.getOrCreate(fragment) {
            kill(fragment.decorView)
        })
    }

    override fun onFragmentStopped(fragment: Fragment) {
        fragment.decorView?.removeView(VortexView.get(fragment))
    }

    private fun kill(parent: ViewGroup?) {
        val vortex = parent?.findViewById<VortexView>(R.id.vortex) ?: return
        alive = false
        parent.removeView(vortex)
    }
}