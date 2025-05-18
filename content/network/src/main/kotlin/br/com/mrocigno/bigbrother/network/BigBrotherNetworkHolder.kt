package br.com.mrocigno.bigbrother.network

import br.com.mrocigno.bigbrother.common.dao.NetworkDao
import br.com.mrocigno.bigbrother.common.db.BigBrotherDatabase.Companion.bbdb
import br.com.mrocigno.bigbrother.network.model.NetworkEntryModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

object BigBrotherNetworkHolder {

    private val dao: NetworkDao? get() = bbdb?.networkDao()

    fun addEntry(entry: NetworkEntryModel) =
        dao?.insert(entry.toEntity()) ?: -1

    fun updateEntry(entry: NetworkEntryModel) {
        entry.track()
        dao?.insert(entry.toEntity())
    }

    fun clear(sessionId: Long) = CoroutineScope(Dispatchers.IO).launch {
        dao?.clearSession(sessionId)
    }

    fun getById(entryId: Long) = dao?.getById(entryId)
        ?.map(::NetworkEntryModel)

    fun getBySessionId(sessionId: Long) = dao?.getBySession(sessionId)
        ?.map { it.map(::NetworkEntryModel) }
}