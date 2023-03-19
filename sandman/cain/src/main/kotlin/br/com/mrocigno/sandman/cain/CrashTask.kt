package br.com.mrocigno.sandman.cain

import android.app.Activity

class CrashTask : br.com.mrocigno.sandman.core.MorpheusTask() {

    override fun onActivityStarted(activity: Activity) {
        super.onActivityStarted(activity)

        Thread.setDefaultUncaughtExceptionHandler(CrashObserver(activity))
    }
}