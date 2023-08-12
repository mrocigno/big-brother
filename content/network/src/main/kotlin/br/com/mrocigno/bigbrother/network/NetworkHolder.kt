package br.com.mrocigno.bigbrother.network

import androidx.lifecycle.MutableLiveData
import br.com.mrocigno.bigbrother.common.utils.update

internal object NetworkHolder {

    private val _networkEntries = mutableListOf<NetworkEntryModel>()
    val networkEntries = MutableLiveData<List<NetworkEntryModel>>()

    fun addEntry(entry: NetworkEntryModel) {
        _networkEntries.add(0, entry)
        networkEntries.postValue(_networkEntries)
    }

    fun updateEntry(entry: NetworkEntryModel) {
        _networkEntries.update(entry)
        networkEntries.postValue(_networkEntries)
        entry.track()
    }

    fun clear() {
        _networkEntries.clear()
        networkEntries.postValue(emptyList())
    }
}