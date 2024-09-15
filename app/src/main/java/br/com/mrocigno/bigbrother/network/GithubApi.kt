package br.com.mrocigno.bigbrother.network

import br.com.mrocigno.bigbrother.repository.model.ApiBase
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface GithubApi {

    @Headers("application: v2")
    @GET("search/repositories?q=language:kotlin&sort=stars&per_page=10")
    suspend fun getRepos(
        @Query("page") page : Int,
        @Header("slowly") slowly: Boolean = false
    ): ApiBase

    @GET("this/will/give/a/404")
    suspend fun getError(): ApiBase

    @POST("test")
    suspend fun simulatePost(@Body map: Map<String, String>): Any
}