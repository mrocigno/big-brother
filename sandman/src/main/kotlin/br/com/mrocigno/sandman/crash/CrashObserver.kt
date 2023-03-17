package br.com.mrocigno.sandman.crash

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Process
import br.com.mrocigno.sandman.utils.drawClick
import br.com.mrocigno.sandman.utils.rootView
import kotlin.system.exitProcess

class CrashObserver(
    private val activity: Activity
) : Thread.UncaughtExceptionHandler {

    override fun uncaughtException(t: Thread, e: Throwable) {
        val root = activity.rootView
        val bitmap = Bitmap.createBitmap(root.width, root.height, Bitmap.Config.ARGB_8888)
        Canvas(bitmap).apply {
            root.draw(this)

            drawClick()
        }

        activity.openFileOutput("print_crash.png", Context.MODE_PRIVATE).use {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
        }

        activity.startActivity(CrashActivity.intent(activity, activity::class.simpleName.orEmpty(), Exception(e)))

        Process.killProcess(Process.myPid())
        exitProcess(0)
    }
}