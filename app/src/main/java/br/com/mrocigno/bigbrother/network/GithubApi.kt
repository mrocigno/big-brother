package br.com.mrocigno.bigbrother.network

import br.com.mrocigno.bigbrother.repository.model.ApiBase
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface GithubApi {

    @GET("search/repositories?q=language:kotlin&sort=stars&per_page=10")
    suspend fun getRepos(
        @Query("page") page : Int,
        @Header("slowly") slowly: Boolean = false
    ): ApiBase
}