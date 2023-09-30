package br.com.mrocigno.bigbrother.crash

import android.app.Activity
import android.os.Process
import br.com.mrocigno.bigbrother.common.utils.canBeSerialized
import br.com.mrocigno.bigbrother.common.utils.printScreen
import br.com.mrocigno.bigbrother.common.utils.save
import br.com.mrocigno.bigbrother.core.utils.bbSessionId
import br.com.mrocigno.bigbrother.core.utils.lastClickPosition
import br.com.mrocigno.bigbrother.report.BigBrotherReport
import java.lang.ref.WeakReference
import kotlin.system.exitProcess

class CrashObserver(
    val activity: WeakReference<Activity>,
    val default: Thread.UncaughtExceptionHandler?
) : Thread.UncaughtExceptionHandler {

    override fun uncaughtException(t: Thread, e: Throwable) {
        runCatching { BigBrotherReport.trackCrash(e) }

        val activity = activity.get() ?: run {
            default?.uncaughtException(t, e)
            return
        }

        activity
            .printScreen(lastClickPosition)
            .save(activity, "print_crash_session_$bbSessionId.png")

        // Normalize serializable exception, cause CoroutinesInternalError cannot be serialized
        val throwable = if (e.canBeSerialized()) e else {
            Exception(e.message, e.cause)
        }

        activity.startActivity(
            CrashActivity.intent(
                activity,
                activity::class.simpleName.orEmpty(),
                bbSessionId,
                throwable
            )
        )

        Process.killProcess(Process.myPid())
        exitProcess(0)
    }
}