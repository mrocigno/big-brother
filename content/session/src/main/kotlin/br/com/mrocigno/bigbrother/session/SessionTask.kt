package br.com.mrocigno.bigbrother.session

import androidx.room.Room
import br.com.mrocigno.bigbrother.core.BigBrotherTask
import br.com.mrocigno.bigbrother.core.utils.bbSessionId
import com.jakewharton.threetenabp.AndroidThreeTen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

internal class SessionTask : BigBrotherTask() {

    override fun onCreate(): Boolean {
        AndroidThreeTen.init(context)
        db = Room.databaseBuilder(context!!, BigBrotherDatabase::class.java, "bb-db").build()

        CoroutineScope(Dispatchers.IO).launch { bbSessionId = db.sessionDao().create() }
        return super.onCreate()
    }

    companion object {

        lateinit var db: BigBrotherDatabase
    }
}