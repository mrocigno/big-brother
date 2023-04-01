package br.com.mrocigno.sandman.core

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.FragmentActivity
import br.com.mrocigno.sandman.common.utils.onStart
import br.com.mrocigno.sandman.common.utils.onStop
import br.com.mrocigno.sandman.core.Sandman.tasks

internal class Morpheus : Application.ActivityLifecycleCallbacks {

    override fun onActivityCreated(activity: Activity, bundle: Bundle?) {
        if (activity.isOutOfDomain) return
        tasks.forEach {
            try {
                it.onActivityCreated(activity, bundle)
            } catch (e: Exception) {
                Log.e("SADMAN", "${it::class.simpleName} - Error inside onActivityCreated task", e)
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
                            Log.e("SANDMAN", "${it::class.simpleName} - Error inside onFragmentStarted task", e)
                        }
                    }
                }
                fragment.lifecycle.onStop {
                    tasks.forEach {
                        try {
                            it.onFragmentStopped(fragment)
                        } catch (e: Exception) {
                            Log.e("SANDMAN", "${it::class.simpleName} - Error inside onFragmentStopped task", e)
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
                Log.e("SANDMAN", "${it::class.simpleName} - Error inside onActivityPaused task", e)
            }
        }
    }

    override fun onActivityStarted(activity: Activity) {
        if (activity.isOutOfDomain) return
        tasks.forEach {
            try {
                it.onActivityStarted(activity)
            } catch (e: Exception) {
                Log.e("SANDMAN", "${it::class.simpleName} - Error inside onActivityStarted task", e)
            }
        }
    }

    override fun onActivityDestroyed(activity: Activity) {
        if (activity.isOutOfDomain) return
        tasks.forEach {
            try {
                it.onActivityDestroyed(activity)
            } catch (e: Exception) {
                Log.e("SANDMAN", "${it::class.simpleName} - Error inside onActivityDestroyed task", e)
            }
        }
    }

    override fun onActivityResumed(activity: Activity) {
        if (activity.isOutOfDomain) return
        tasks.forEach {
            try {
                it.onActivityResume(activity)
            } catch (e: Exception) {
                Log.e("SANDMAN", "${it::class.simpleName} - Error inside onActivityResume task", e)
            }
        }
    }

    override fun onActivityStopped(activity: Activity) {
        if (activity.isOutOfDomain) return
        tasks.forEach {
            try {
                it.onActivityStopped(activity)
            } catch (e: Exception) {
                Log.e("SANDMAN", "${it::class.simpleName} - Error inside onActivityStopped task", e)
            }
        }
    }

    override fun onActivitySaveInstanceState(activity: Activity, bundle: Bundle) = Unit
}
