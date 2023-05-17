package br.com.mrocigno.bigbrother.crash

import android.app.Activity
import android.os.Process
import br.com.mrocigno.bigbrother.common.utils.printScreen
import br.com.mrocigno.bigbrother.common.utils.save
import br.com.mrocigno.bigbrother.core.utils.globalTracker
import br.com.mrocigno.bigbrother.core.utils.lastClickPosition
import br.com.mrocigno.bigbrother.core.utils.track
import java.lang.ref.WeakReference
import kotlin.system.exitProcess

class CrashObserver(
    val activity: WeakReference<Activity>,
    val default: Thread.UncaughtExceptionHandler?
) : Thread.UncaughtExceptionHandler {

    override fun uncaughtException(t: Thread, e: Throwable) {
        CrashReport(e).track()

        val activity = activity.get() ?: run {
            default?.uncaughtException(t, e)
            return
        }

        activity
            .printScreen(lastClickPosition)
            .save(activity, "print_crash.png")

        activity.startActivity(
            CrashActivity.intent(
                activity,
                activity::class.simpleName.orEmpty(),
                ArrayList(globalTracker),
                e
            )
        )

        Process.killProcess(Process.myPid())
        exitProcess(0)
    }
}