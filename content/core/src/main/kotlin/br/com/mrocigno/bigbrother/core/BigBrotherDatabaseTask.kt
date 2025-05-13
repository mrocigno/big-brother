package br.com.mrocigno.bigbrother.core

import android.app.Application
import android.util.Log
import androidx.room.Room
import br.com.mrocigno.bigbrother.common.BBTAG
import br.com.mrocigno.bigbrother.common.db.BigBrotherDatabase
import br.com.mrocigno.bigbrother.common.db.BigBrotherDatabase.Companion.bbdb
import com.jakewharton.threetenabp.AndroidThreeTen

internal class BigBrotherDatabaseTask : BigBrotherTask() {

    override val priority: Int get() = 0

    override fun onStartTask() {
        try {
            val context = context as Application
            AndroidThreeTen.init(context)

            // main thread allowed to prevent session -1
            bbdb = Room.databaseBuilder(context, BigBrotherDatabase::class.java, "big-brother-database")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build()
        } catch (e: Exception) {
            Log.e(BBTAG, "failed to initialize big brother network task", e)
        }
    }
}