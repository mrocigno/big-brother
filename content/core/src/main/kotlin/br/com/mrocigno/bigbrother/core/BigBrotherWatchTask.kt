package br.com.mrocigno.bigbrother.core

import android.app.Activity
import android.os.Bundle
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import br.com.mrocigno.bigbrother.common.utils.decorView
import br.com.mrocigno.bigbrother.common.utils.rootView
import br.com.mrocigno.bigbrother.core.BigBrother.config
import br.com.mrocigno.bigbrother.core.ui.BigBrotherView

class BigBrotherWatchTask : BigBrotherTask() {

    fun kill(parent: ViewGroup?) {
        val view = parent?.findViewById<BigBrotherView>(R.id.bigbrother) ?: return
        parent.removeView(view)
        config.isAlive = false
    }

    override fun onActivityCreated(activity: Activity, bundle: Bundle?) {
        if (!config.isAlive) return
        activity.rootView.addView(BigBrotherView(activity))
    }

    override fun onActivityStarted(activity: Activity) {
        if (!config.isAlive) kill(activity.rootView)
    }

    override fun onActivityPaused(activity: Activity) {
        activity.rootView.removeView(BigBrotherView.get(activity))
    }

    override fun onFragmentStarted(fragment: Fragment) {
        if (!config.isAlive) return
        fragment.decorView?.addView(BigBrotherView.getOrCreate(fragment))
    }

    override fun onFragmentStopped(fragment: Fragment) {
        fragment.decorView?.removeView(BigBrotherView.get(fragment))
    }
}