package br.com.mrocigno.sandman.crash

import android.app.Activity
import br.com.mrocigno.sandman.morpheus.MorpheusTask

class CrashTask : MorpheusTask() {

    override fun onActivityStarted(activity: Activity) {
        super.onActivityStarted(activity)

        Thread.setDefaultUncaughtExceptionHandler(CrashObserver(activity))
    }
}