package br.com.mrocigno.bigbrother.di

import br.com.mrocigno.bigbrother.network.GithubApi
import br.com.mrocigno.bigbrother.network.NetworkConfig.retrofit
import br.com.mrocigno.bigbrother.repository.GithubRepository

object DI {

    val githubApi = retrofit.create(GithubApi::class.java)

    val githubRepository = GithubRepository(githubApi)
}