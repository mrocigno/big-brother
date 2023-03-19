package br.com.mrocigno.sandman.cain

import android.app.Activity
import android.os.Process
import br.com.mrocigno.sandman.common.utils.printScreen
import br.com.mrocigno.sandman.common.utils.save
import br.com.mrocigno.sandman.core.utils.lastClickPosition
import kotlin.system.exitProcess

class CrashObserver(
    private val activity: Activity
) : Thread.UncaughtExceptionHandler {

    override fun uncaughtException(t: Thread, e: Throwable) {
        activity
            .printScreen(lastClickPosition)
            .save(activity, "print_crash.png")

        activity.startActivity(
            CrashActivity.intent(
                activity,
                activity::class.simpleName.orEmpty(),
                Exception(e)
            )
        )

        Process.killProcess(Process.myPid())
        exitProcess(0)
    }
}