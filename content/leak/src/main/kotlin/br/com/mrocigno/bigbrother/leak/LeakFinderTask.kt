package br.com.mrocigno.bigbrother.leak

import android.app.Activity
import android.util.Log
import br.com.mrocigno.bigbrother.core.BigBrotherTask
import java.lang.ref.WeakReference

class LeakFinderTask : BigBrotherTask() {

    override fun onActivityDestroyed(activity: Activity) {
        super.onActivityDestroyed(activity)
        val teste = WeakReference(activity)
        Thread {
            Runtime.getRuntime().gc()
            Thread.sleep(5000)
            Log.d("asda", "${teste.get()}")
        }.start()
    }
}