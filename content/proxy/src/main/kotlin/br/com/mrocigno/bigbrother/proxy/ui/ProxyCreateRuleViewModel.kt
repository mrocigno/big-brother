package br.com.mrocigno.bigbrother.proxy.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.mrocigno.bigbrother.common.dao.ProxyDao
import br.com.mrocigno.bigbrother.common.db.BigBrotherDatabase.Companion.bbdb
import br.com.mrocigno.bigbrother.common.entity.ProxyRuleEntity
import br.com.mrocigno.bigbrother.proxy.model.ProxyActionModel
import br.com.mrocigno.bigbrother.proxy.model.ProxyRuleModel
import kotlinx.coroutines.launch

internal class ProxyCreateRuleViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {

    private val proxyDao: ProxyDao? = bbdb?.proxyDao()

    private val _actions: MutableLiveData<List<ProxyActionModel>> =
        savedStateHandle.getLiveData(::actions.name, emptyList())
    val actions: LiveData<List<ProxyActionModel>>
        get() = _actions

    fun addActions(action: List<ProxyActionModel>) {
        _actions.value = action
    }

    fun addAction(action: ProxyActionModel) {
        _actions.value = _actions.value.orEmpty() + action
    }

    fun updateAction(old: ProxyActionModel?, new: ProxyActionModel) {
        val mutableList = _actions.value.orEmpty().toMutableList()
        val oldIndex = old?.let(mutableList::indexOf) ?: mutableList.size
        mutableList[oldIndex] = new
        _actions.value = mutableList
    }

    fun removeAction(action: ProxyActionModel) = viewModelScope.launch {
        _actions.value = _actions.value.orEmpty() - action
        proxyDao?.deleteActionById(action.id)
    }

    fun save(
        currentRule: ProxyRuleModel?,
        ruleName: String,
        methodCondition: String,
        pathCondition: String,
        headerCondition: String
    ) {
        viewModelScope.launch {
            val ruleId = insertRule(ProxyRuleEntity(
                id = currentRule?.id ?: 0,
                ruleName = ruleName,
                methodCondition = methodCondition,
                pathCondition = pathCondition,
                headerCondition = headerCondition
            ))
            insertActions(ruleId)
        }
    }

    fun delete(proxyRuleModel: ProxyRuleModel) {
        val id = proxyRuleModel.id
        viewModelScope.launch {
            proxyDao?.deleteRuleById(id)
            proxyDao?.deleteActionsByRuleId(id)
        }
    }

    private suspend fun insertRule(entity: ProxyRuleEntity): Long =
        proxyDao?.insert(entity) ?: -1L

    private suspend fun insertActions(ruleId: Long) {
        val linkedActions = _actions.value.orEmpty().map { it.toEntity(ruleId) }
        proxyDao?.insertAll(linkedActions)
    }
}