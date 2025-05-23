package br.com.mrocigno.bigbrother.repository

import br.com.mrocigno.bigbrother.network.GithubApi
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody

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

    fun simulatePost() = flow {
        emit(githubApi.simulatePost(mapOf(
            "test1" to "value1",
            "test2" to "value2"
        )))
    }

    suspend fun uploadImage(img: MultipartBody.Part) =
        githubApi.uploadImage(img, "teste da silva")

    suspend fun xmlApi() =
        githubApi.xmlApi()
}