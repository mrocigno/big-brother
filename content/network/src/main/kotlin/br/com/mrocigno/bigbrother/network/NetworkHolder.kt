package br.com.mrocigno.bigbrother.network

import android.content.Context
import androidx.room.Room
import br.com.mrocigno.bigbrother.network.dao.NetworkDao
import br.com.mrocigno.bigbrother.network.entity.NetworkEntry
import br.com.mrocigno.bigbrother.network.model.NetworkEntryModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

internal object NetworkHolder {

    private lateinit var db: NetworkDatabase
    private val dao: NetworkDao
        get() = db.networkDao()

    fun init(context: Context) {
        db = Room.databaseBuilder(context, NetworkDatabase::class.java, "bb-network-db").build()
    }

    fun addEntry(entry: NetworkEntryModel) =
        dao.insert(NetworkEntry(entry))

    fun updateEntry(entry: NetworkEntryModel) {
        entry.track()
        dao.insert(NetworkEntry(entry))
    }

    fun clear(sessionId: Long) = CoroutineScope(Dispatchers.IO).launch {
        dao.clearSession(sessionId)
    }

    fun getById(entryId: Long) = dao.getById(entryId)
        .map(::NetworkEntryModel)

    fun getBySessionId(sessionId: Long) = dao.getBySession(sessionId)
        .map { it.map(::NetworkEntryModel) }
}