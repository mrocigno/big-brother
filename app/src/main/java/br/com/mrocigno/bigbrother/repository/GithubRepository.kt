package br.com.mrocigno.bigbrother.repository

import br.com.mrocigno.bigbrother.R
import br.com.mrocigno.bigbrother.network.GithubApi
import br.com.mrocigno.bigbrother.network.NetworkKtor.ktorClient
import br.com.mrocigno.bigbrother.repository.model.ApiBase
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody

class GithubRepository(
    private val githubApi: GithubApi
) {

    fun getList(client: Int) = flow {
        val response = when (client) {
            R.id.okhttp_3 -> githubApi.getRepos(0)
            R.id.ktor -> ktorClient.get("search/repositories?q=language:kotlin&sort=stars&per_page=10")
                .body<ApiBase>()
            else -> error("Client not found")
        }

        emit(response)
    }

    fun getListSlowly(client: Int) = flow {
        val response = when (client) {
            R.id.okhttp_3 -> githubApi.getRepos(1, true)
            R.id.ktor ->
                ktorClient.get("search/repositories?q=language:kotlin&sort=stars&per_page=10") {
                    header("slowly", true)
                }.body<ApiBase>()
            else -> error("Client not found")
        }

        emit(response)
    }

    fun getError(client: Int) = flow {
        val response = when (client) {
            R.id.okhttp_3 -> githubApi.getError()
            R.id.ktor -> ktorClient.get("this/will/give/a/404").body<ApiBase>()
            else -> error("Client not found")
        }

        emit(response)
    }

    fun simulatePost(client: Int) = flow {
        val body = mapOf(
            "test1" to "value1",
            "test2" to "value2"
        )
        val response = when (client) {
            R.id.okhttp_3 -> githubApi.simulatePost(body)
            R.id.ktor -> ktorClient.post("this/will/give/a/404") {
                contentType(ContentType.Application.Json)
                setBody(body)
            }.body<ApiBase>()
            else -> error("Client not found")
        }

        emit(response)
    }

    suspend fun uploadImage(img: MultipartBody.Part) =
        githubApi.uploadImage(img, "teste da silva")

    suspend fun xmlApi(client: Int) {
        when (client) {
            R.id.okhttp_3 -> githubApi.xmlApi()
            R.id.ktor -> ktorClient.get("https://shapechange.net/resources/test/testEA.xml").body<ApiBase>()
            else -> error("Client not found")
        }
    }
}