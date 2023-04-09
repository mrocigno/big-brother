package br.com.mrocigno.bigbrother.repository

import br.com.mrocigno.bigbrother.network.GithubApi
import kotlinx.coroutines.flow.flow

class GithubRepository(
    private val githubApi: GithubApi
) {

    fun getList() = flow {
        emit(githubApi.getRepos(0))
    }

    fun getListSlowly() = flow {
        emit(githubApi.getRepos(1, true))
    }

    fun getError() = flow {
        emit(githubApi.getError())
    }
}