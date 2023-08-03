package br.com.mrocigno.bigbrother.session

import android.app.Activity
import android.util.Log
import androidx.room.Room
import br.com.mrocigno.bigbrother.common.BBTAG
import br.com.mrocigno.bigbrother.core.BigBrotherTask
import br.com.mrocigno.bigbrother.core.utils.bbSessionId
import br.com.mrocigno.bigbrother.core.utils.globalTracker
import br.com.mrocigno.bigbrother.session.dao.aaadsada
import com.jakewharton.threetenabp.AndroidThreeTen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

internal class SessionTask : BigBrotherTask() {

    private lateinit var db: BigBrotherDatabase

    override val priority = 0

    override fun onActivityPaused(activity: Activity) {
        super.onActivityPaused(activity)

        CoroutineScope(Dispatchers.IO).launch {
            db.reportLogDao().aaadsada(globalTracker)
        }
    }

    override fun onCreate(): Boolean {
        try {
            val context = context ?: throw IllegalStateException("context is null")
            AndroidThreeTen.init(context)
            db = Room.databaseBuilder(context, BigBrotherDatabase::class.java, "bb-db").build()

            CoroutineScope(Dispatchers.IO).launch {
                db.sessionDao().closePreviousSession()
                bbSessionId = db.sessionDao().create()
            }
        } catch (e: Exception) {
            Log.e(BBTAG, "failed to initialize session", e)
            return false
        }

        return super.onCreate()
    }
}