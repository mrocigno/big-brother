package br.com.mrocigno.sandman

import android.app.Activity
import android.app.Application
import android.app.Application.ActivityLifecycleCallbacks
import android.content.Context
import android.graphics.PointF
import android.os.Bundle
import android.view.ViewGroup
import android.view.Window
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

class Morpheus : ActivityLifecycleCallbacks {

    private val lastPoint = PointF(0f, 200f)
    private val Activity.vortex: VortexView get() = findViewById(R.id.vortex) ?: create(this)
    private val Fragment.vortex: VortexView get() = decorView?.findViewById(R.id.vortex) ?: create(requireContext())

    private fun create(context: Context) = VortexView(context).apply {
        id = R.id.vortex
    }

    override fun onActivityCreated(activity: Activity, bundle: Bundle?) {
        if (!isVortexAlive) return
        try {
            (activity.window.decorView as FrameLayout).addView(activity.vortex)

            if (activity is AppCompatActivity) {
                activity.supportFragmentManager.addFragmentOnAttachListener { _, fragment ->
                    if (!isVortexAlive) return@addFragmentOnAttachListener
                    with(fragment.lifecycle) {
                        onStart {
                            fragment.decorView?.addView(fragment.vortex)
                        }
                        onStop {
                            fragment.decorView?.removeView(fragment.vortex)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onActivityPaused(activity: Activity) {
        val vortex = activity.vortex
        lastPoint.set(vortex.x, vortex.y)
    }

    override fun onActivityStarted(activity: Activity) {
        activity.vortex.x = lastPoint.x
        activity.vortex.y = lastPoint.y
    }

    override fun onActivityResumed(activity: Activity) {}
    override fun onActivityStopped(activity: Activity) {}
    override fun onActivitySaveInstanceState(activity: Activity, bundle: Bundle) {}
    override fun onActivityDestroyed(activity: Activity) {}

    companion object {

        private var onVortextKilled: (() -> Unit)? = null
        var isVortexAlive = true

        fun init(context: Application, onVortextKilled: (() -> Unit)? = null) {
            context.registerActivityLifecycleCallbacks(Morpheus())
            this.onVortextKilled = onVortextKilled
        }

        fun killVortex(vortex: VortexView?) {
            isVortexAlive = false
            (vortex?.parent as? ViewGroup)?.removeView(vortex)
            onVortextKilled?.invoke()
            onVortextKilled = null
        }

        fun killVortex(window: Window) {
            val vortex = window.findViewById<VortexView>(R.id.vortex) ?: return
            (vortex.parent as ViewGroup).removeView(vortex)
        }
    }
}