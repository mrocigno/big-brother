package br.com.mrocigno.sandman.corinthian

import androidx.lifecycle.MutableLiveData
import br.com.mrocigno.sandman.common.utils.update

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
    }

    fun clear() {
        _networkEntries.clear()
        networkEntries.postValue(emptyList())
    }
}