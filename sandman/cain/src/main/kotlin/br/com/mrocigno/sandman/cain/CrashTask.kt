package br.com.mrocigno.sandman.cain

import android.app.Activity
import br.com.mrocigno.sandman.core.MorpheusTask
import java.lang.ref.WeakReference

class CrashTask : MorpheusTask() {

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