package br.com.mrocigno.bigbrother.ui.network

import androidx.lifecycle.ViewModel
import br.com.mrocigno.bigbrother.di.DI
import br.com.mrocigno.bigbrother.network.MutableResponseFlow
import br.com.mrocigno.bigbrother.network.ResponseFlow
import br.com.mrocigno.bigbrother.repository.GithubRepository
import br.com.mrocigno.bigbrother.repository.model.ApiBase

class NetworkViewModel(
    private val githubRepository: GithubRepository = DI.githubRepository
) : ViewModel() {

    private val _list = MutableResponseFlow<ApiBase>()
    val list: ResponseFlow<ApiBase> get() = _list

    fun fetchList() = _list.sync(githubRepository.getList())
    fun fetchListSlowly() = _list.sync(githubRepository.getListSlowly())
}