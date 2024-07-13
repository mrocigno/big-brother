package br.com.mrocigno.bigbrother.log

import android.app.Application
import android.util.Log
import br.com.mrocigno.bigbrother.common.BBTAG
import br.com.mrocigno.bigbrother.core.BigBrotherTask
import com.jakewharton.threetenabp.AndroidThreeTen

internal class LogTask : BigBrotherTask() {

    override fun onCreate(): Boolean {
        try {
            val context = context as Application
            AndroidThreeTen.init(context)
            BBLog.init(context)
        } catch (e: Exception) {
            Log.e(BBTAG, "failed to initialize big brother network task", e)
            return false
        }
        return super.onCreate()
    }
}