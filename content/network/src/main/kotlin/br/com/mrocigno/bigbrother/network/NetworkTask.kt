package br.com.mrocigno.bigbrother.network

import android.app.Application
import android.util.Log
import androidx.room.Room
import br.com.mrocigno.bigbrother.common.BBTAG
import br.com.mrocigno.bigbrother.core.BigBrotherTask

class NetworkTask : BigBrotherTask() {

    private lateinit var db: NetworkDatabase

    override fun onCreate(): Boolean {
        try {
            val context = context as Application
            db = Room.databaseBuilder(context, NetworkDatabase::class.java, "bb-network-db").build()
        } catch (e: Exception) {
            Log.e(BBTAG, "failed to initialize big brother network task", e)
            return false
        }
        return super.onCreate()
    }
}