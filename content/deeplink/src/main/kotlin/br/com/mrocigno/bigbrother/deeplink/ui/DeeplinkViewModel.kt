package br.com.mrocigno.bigbrother.deeplink.ui

import android.content.res.AssetManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.mrocigno.bigbrother.common.dao.DeeplinkDao
import br.com.mrocigno.bigbrother.common.db.BigBrotherDatabase.Companion.bbdb
import br.com.mrocigno.bigbrother.common.entity.DeeplinkEntity
import br.com.mrocigno.bigbrother.deeplink.BIG_BROTHER_DEEPLINK_FILENAME
import br.com.mrocigno.bigbrother.deeplink.model.DeeplinkEntry
import br.com.mrocigno.bigbrother.deeplink.model.DeeplinkModel
import br.com.mrocigno.bigbrother.log.BBLog
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

internal class DeeplinkViewModel : ViewModel() {

    private val deeplinkDao: DeeplinkDao? = bbdb?.deeplinkDao()
    private var deeplinks: List<DeeplinkEntry>? = null

    fun getDeeplinks(assets: AssetManager): List<DeeplinkEntry> {
        deeplinks = deeplinks ?: assets.deeplinks()
        return deeplinks!!
    }

    fun getRecentDeeplinks() =
        deeplinkDao?.getAll()?.map { it.map(::DeeplinkEntry) }

    fun save(entity: DeeplinkEntity) =
        viewModelScope.launch {
            deeplinkDao?.inset(entity)
        }

    fun delete(toEntity: DeeplinkEntity) =
        viewModelScope.launch {
            deeplinkDao?.delete(toEntity)
        }

    private fun AssetManager.deeplinks(): List<DeeplinkEntry> =
        runCatching {
            open(BIG_BROTHER_DEEPLINK_FILENAME).use {
                val decoder = Json { ignoreUnknownKeys = true }
                decoder.decodeFromString<List<DeeplinkModel>>(it.bufferedReader().readText())
            }
        }.recoverCatching {
            BBLog.tag("deeplinks").e("error getting $BIG_BROTHER_DEEPLINK_FILENAME", it)
            emptyList()
        }.getOrElse {
            emptyList()
        }.let(DeeplinkEntry::fromList)

    fun deleteAll() = viewModelScope.launch {
        deeplinkDao?.deleteAll()
    }
}
