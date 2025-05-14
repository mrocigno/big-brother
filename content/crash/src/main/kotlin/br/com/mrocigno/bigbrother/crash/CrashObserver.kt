package br.com.mrocigno.bigbrother.crash

import android.app.Activity
import android.content.Intent
import android.os.Process
import br.com.mrocigno.bigbrother.common.db.BigBrotherDatabase.Companion.bbdb
import br.com.mrocigno.bigbrother.common.entity.CrashEntity
import br.com.mrocigno.bigbrother.common.route.intentToCrash
import br.com.mrocigno.bigbrother.common.utils.bbSessionId
import br.com.mrocigno.bigbrother.common.utils.printScreen
import br.com.mrocigno.bigbrother.common.utils.save
import br.com.mrocigno.bigbrother.core.utils.lastClickPosition
import br.com.mrocigno.bigbrother.report.BigBrotherReport
import kotlinx.coroutines.runBlocking
import java.lang.ref.WeakReference
import kotlin.system.exitProcess

internal class CrashObserver(
    val activity: WeakReference<Activity>,
    val default: Thread.UncaughtExceptionHandler?
) : Thread.UncaughtExceptionHandler {

    override fun uncaughtException(t: Thread, e: Throwable) {
        runCatching { BigBrotherReport.trackCrash(e) }

        val activity = activity.get() ?: run {
            default?.uncaughtException(t, e)
            return
        }

        runBlocking {
            bbdb?.crashDao()?.insert(
                CrashEntity(
                    activityName = activity::class.simpleName.orEmpty(),
                    stackTrace = e.stackTraceToString()
                )
            )
        }

        activity
            .printScreen(lastClickPosition)
            .save(activity, "print_crash_session_$bbSessionId.png")

        activity.intentToCrash(bbSessionId)
            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            .run(activity::startActivity)

        default?.uncaughtException(t, e)
        Process.killProcess(Process.myPid())
        exitProcess(0)
    }
}