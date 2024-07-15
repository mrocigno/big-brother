package br.com.mrocigno.bigbrother.core

import android.app.Application
import android.util.Log
import androidx.room.Room
import br.com.mrocigno.bigbrother.common.BBTAG
import br.com.mrocigno.bigbrother.core.db.BigBrotherDatabase
import br.com.mrocigno.bigbrother.core.utils.getTask
import com.jakewharton.threetenabp.AndroidThreeTen

class BigBrotherDatabaseTask : BigBrotherTask() {

    private var db: BigBrotherDatabase? = null

    override fun onCreate(): Boolean {
        try {
            val context = context as Application
            AndroidThreeTen.init(context)

            // main thread allowed to prevent session -1
            db = Room.databaseBuilder(context, BigBrotherDatabase::class.java, "big-brother-database")
                .allowMainThreadQueries()
                .build()
        } catch (e: Exception) {
            Log.e(BBTAG, "failed to initialize big brother network task", e)
            return false
        }
        return super.onCreate()
    }

    companion object {

        val bbdb: BigBrotherDatabase?
            get() = getTask(BigBrotherDatabaseTask::class)?.db
    }
}