package br.com.mrocigno.sandman.lucien.crash

import android.app.Activity
import br.com.mrocigno.sandman.core.MorpheusTask
import java.lang.ref.WeakReference

class CrashTask : MorpheusTask() {

    override fun onActivityResume(activity: Activity) {
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