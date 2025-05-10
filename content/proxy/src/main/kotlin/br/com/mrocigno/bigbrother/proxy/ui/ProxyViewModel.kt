package br.com.mrocigno.bigbrother.proxy.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import br.com.mrocigno.bigbrother.proxy.model.ProxyActionModel

internal class ProxyViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {

    private val _actions: MutableLiveData<List<ProxyActionModel>>
        get() = savedStateHandle.getLiveData(::actions.name, emptyList())
    val actions: LiveData<List<ProxyActionModel>>
        get() = _actions

    fun addAction(action: ProxyActionModel) {
        _actions.value = _actions.value.orEmpty() + action
    }
}