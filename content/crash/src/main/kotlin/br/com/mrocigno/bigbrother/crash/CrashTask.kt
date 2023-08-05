package br.com.mrocigno.bigbrother.crash

import android.app.Activity
import br.com.mrocigno.bigbrother.core.BigBrotherTask
import java.lang.ref.WeakReference

class CrashTask : BigBrotherTask() {

    override fun onActivityResume(activity: Activity) {
        val currentObserver = Thread.getDefaultUncaughtExceptionHandler()
        val default = if (currentObserver is CrashObserver) {
            currentObserver.activity.clear()
            currentObserver.default
        } else {
            currentObserver
        }

        Thread.setDefaultUncaughtExceptionHandler(
            CrashObserver(
                WeakReference(activity),
                default
            )
        )
    }
}