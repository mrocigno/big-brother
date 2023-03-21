package br.com.mrocigno.sandman.cain

import android.app.Activity
import java.lang.ref.WeakReference

class CrashTask : br.com.mrocigno.sandman.core.MorpheusTask() {

    override fun onActivityStarted(activity: Activity) {
        super.onActivityStarted(activity)

        val currentObserver = Thread.getDefaultUncaughtExceptionHandler()
        val default = if (currentObserver is CrashObserver) {
            currentObserver.activity.clear()
            currentObserver.default
        } else {
            currentObserver
        }

        Thread.setDefaultUncaughtExceptionHandler(CrashObserver(
            WeakReference(activity),
            default
        ))
    }
}