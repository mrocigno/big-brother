package br.com.mrocigno.bigbrother.network.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.mrocigno.bigbrother.common.utils.applyScoped
import br.com.mrocigno.bigbrother.network.NetworkHolder
import br.com.mrocigno.bigbrother.network.model.NetworkEntryModel

class NetworkEntryDetailsViewModel : ViewModel() {

    fun getNetworkEntry(entryId: Long): LiveData<NetworkEntryModel> =
        MutableLiveData<NetworkEntryModel>().applyScoped(viewModelScope) {
            NetworkHolder.getById(entryId).collect {
                postValue(it)
            }
        }
}