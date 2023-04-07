package br.com.mrocigno.bigbrother.network

import retrofit2.http.GET
import retrofit2.http.Query

interface GithubApi {

    @GET("search/repositories?q=language:kotlin&sort=stars&per_page=10")
    suspend fun getRepos(@Query("page") page : Int) : ApiBase
}