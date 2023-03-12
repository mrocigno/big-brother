package br.com.mrocigno.sandman

import android.app.Activity
import android.app.Application
import android.graphics.PointF
import android.os.Bundle
import android.widget.FrameLayout
import androidx.fragment.app.FragmentActivity
import br.com.mrocigno.sandman.Sandman.killVortex
import br.com.mrocigno.sandman.report.ClickObserverView
import br.com.mrocigno.sandman.utils.decorView
import br.com.mrocigno.sandman.utils.onStart
import br.com.mrocigno.sandman.utils.onStop

class Morpheus : Application.ActivityLifecycleCallbacks {

    private val lastPoint = PointF(0f, 200f)

    override fun onActivityCreated(activity: Activity, bundle: Bundle?) {
        if (!Sandman.isVortexAlive || !activity.isVortexAllowed) return
        try {
            (activity.window.decorView as FrameLayout).apply {
                addView(VortexView.getOrCreate(activity))
                addView(ClickObserverView.getOrCreate(activity))
            }

            if (activity is FragmentActivity) {
                activity.supportFragmentManager.addFragmentOnAttachListener { _, fragment ->
                    if (!Sandman.isVortexAlive) return@addFragmentOnAttachListener
                    with(fragment.lifecycle) {
                        onStart {
                            fragment.decorView?.addView(VortexView.getOrCreate(fragment))
                            fragment.decorView?.addView(ClickObserverView.getOrCreate(fragment))
                        }
                        onStop {
                            fragment.decorView?.removeView(VortexView.get(fragment))
                            fragment.decorView?.removeView(ClickObserverView.get(fragment))
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onActivityPaused(activity: Activity) {
        val vortex = VortexView.get(activity) ?: return
        lastPoint.set(vortex.x, vortex.y)
    }

    override fun onActivityStarted(activity: Activity) {
        val vortex = VortexView.get(activity) ?: return
        if (!Sandman.isVortexAlive) killVortex(vortex)
        vortex.x = lastPoint.x
        vortex.y = lastPoint.y
    }

    override fun onActivityResumed(activity: Activity) {}
    override fun onActivityStopped(activity: Activity) {}
    override fun onActivitySaveInstanceState(activity: Activity, bundle: Bundle) {}
    override fun onActivityDestroyed(activity: Activity) {}
}
