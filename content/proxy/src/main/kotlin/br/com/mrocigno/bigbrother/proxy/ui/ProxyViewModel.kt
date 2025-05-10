package br.com.mrocigno.bigbrother.proxy.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.mrocigno.bigbrother.common.dao.ProxyDao
import br.com.mrocigno.bigbrother.common.db.BigBrotherDatabase.Companion.bbdb
import br.com.mrocigno.bigbrother.common.entity.ProxyActionEntity
import br.com.mrocigno.bigbrother.common.entity.ProxyRuleEntity
import br.com.mrocigno.bigbrother.proxy.model.ProxyActionModel
import kotlinx.coroutines.launch

internal class ProxyViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {

    private val proxyDao: ProxyDao? = bbdb?.proxyDao()

    private val _actions: MutableLiveData<List<ProxyActionModel>> =
        savedStateHandle.getLiveData(::actions.name, emptyList())
    val actions: LiveData<List<ProxyActionModel>>
        get() = _actions

    fun addAction(action: ProxyActionModel) {
        _actions.value = _actions.value.orEmpty() + action
    }

    fun save(ruleName: String, pathCondition: String, headerCondition: String) {
        viewModelScope.launch {
            val ruleId = insertRule(ProxyRuleEntity(
                ruleName = ruleName,
                pathCondition = pathCondition,
                headerCondition = headerCondition
            ))
            insertActions(ruleId)
        }
    }

    private suspend fun insertRule(entity: ProxyRuleEntity): Long =
        proxyDao?.insert(entity) ?: -1L

    private suspend fun insertActions(ruleId: Long) {
        val linkedActions = _actions.value.orEmpty().map {
            ProxyActionEntity(
                proxyId = ruleId,
                label = it.action.name,
                name = it.name,
                value = it.value,
                body = it.body
            )
        }
        proxyDao?.insertAll(linkedActions)
    }
}