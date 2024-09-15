package br.com.mrocigno.bigbrother.leak

import android.app.Activity
import android.os.Handler
import android.os.Looper
import android.util.Log
import br.com.mrocigno.bigbrother.core.BigBrotherTask
import java.lang.ref.ReferenceQueue
import java.lang.ref.WeakReference
import java.util.UUID
import java.util.concurrent.Executor

class LeakFinderTask : BigBrotherTask() {

    private val lock = Object()
    private val reference = ReferenceQueue<Activity>()
    private val destroyedActivities: HashMap<String, WeakReference<Activity>> = hashMapOf()
    private val executor = Executor {
        Handler(Looper.getMainLooper()).postDelayed(it, 7000)
    }

    override fun onActivityDestroyed(activity: Activity) {
        val key = UUID.randomUUID().toString()
        destroyedActivities[key] = WeakReference(activity, reference)
        executor.execute {
            Runtime.getRuntime().gc()
            Thread.sleep(100)
            System.runFinalization()

            synchronized(lock) {
                do {
                    val ref = reference.poll()
                    if (ref != null) destroyedActivities.remove(key)
                } while (ref != null)
            }


            Log.w("Leak", destroyedActivities[key]?.get().toString())
        }
    }
}