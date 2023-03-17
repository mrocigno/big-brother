package br.com.mrocigno.sandman.morpheus

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import br.com.mrocigno.sandman.Sandman
import br.com.mrocigno.sandman.crash.CrashTask
import br.com.mrocigno.sandman.isOutOfDomain
import br.com.mrocigno.sandman.log.SandmanLog.Companion.tag
import br.com.mrocigno.sandman.report.ReportTask
import br.com.mrocigno.sandman.utils.onStart
import br.com.mrocigno.sandman.utils.onStop
import br.com.mrocigno.sandman.vortex.VortexTask

internal class Morpheus : Application.ActivityLifecycleCallbacks {

    private val tasks = listOf(
        VortexTask(),
        ReportTask(),
        CrashTask()
    )

    override fun onActivityCreated(activity: Activity, bundle: Bundle?) {
        if (activity.isOutOfDomain) return
        tasks.forEach {
            try {
                it.onActivityCreated(activity, bundle)
            } catch (e: Exception) {
                Sandman.tag().e("${it::class.simpleName} - Error inside onActivityCreated task", e)
            }
        }

        if (activity is FragmentActivity) {
            activity.supportFragmentManager.addFragmentOnAttachListener { _, fragment ->
                if (fragment.isOutOfDomain) return@addFragmentOnAttachListener
                fragment.lifecycle.onStart {
                    tasks.forEach {
                        try {
                            it.onFragmentStarted(fragment)
                        } catch (e: Exception) {
                            Sandman.tag().e("${it::class.simpleName} - Error inside onFragmentStarted task", e)
                        }
                    }
                }
                fragment.lifecycle.onStop {
                    tasks.forEach {
                        try {
                            it.onFragmentStopped(fragment)
                        } catch (e: Exception) {
                            Sandman.tag().e("${it::class.simpleName} - Error inside onFragmentStopped task", e)
                        }
                    }
                }
            }
        }
    }

    override fun onActivityPaused(activity: Activity) {
        if (activity.isOutOfDomain) return
        tasks.forEach {
            try {
                it.onActivityPaused(activity)
            } catch (e: Exception) {
                Sandman.tag().e("${it::class.simpleName} - Error inside onActivityPaused task", e)
            }
        }
    }

    override fun onActivityStarted(activity: Activity) {
        if (activity.isOutOfDomain) return
        tasks.forEach {
            try {
                it.onActivityStarted(activity)
            } catch (e: Exception) {
                Sandman.tag().e("${it::class.simpleName} - Error inside onActivityStarted task", e)
            }
        }
    }

    override fun onActivityDestroyed(activity: Activity) {
        if (activity.isOutOfDomain) return
        tasks.forEach {
            try {
                it.onActivityDestroyed(activity)
            } catch (e: Exception) {
                Sandman.tag().e("${it::class.simpleName} - Error inside onActivityDestroyed task", e)
            }
        }
    }

    override fun onActivityResumed(activity: Activity) = Unit

    override fun onActivityStopped(activity: Activity) = Unit

    override fun onActivitySaveInstanceState(activity: Activity, bundle: Bundle) = Unit
}
