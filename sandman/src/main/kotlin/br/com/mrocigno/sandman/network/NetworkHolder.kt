package br.com.mrocigno.sandman.network

import androidx.lifecycle.MutableLiveData

internal object NetworkHolder {

    private val _networkEntries = mutableListOf<NetworkEntryModel>()
    val networkEntries = MutableLiveData<List<NetworkEntryModel>>()

    fun addEntry(entry: NetworkEntryModel) {
        _networkEntries.add(0, entry)
        networkEntries.postValue(_networkEntries)
    }

    fun clear() {
        _networkEntries.clear()
        networkEntries.postValue(emptyList())
    }
}