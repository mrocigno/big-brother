package br.com.mrocigno.bigbrother.repository.model

import com.google.gson.annotations.SerializedName

data class ApiBase(
    @SerializedName("total_count") val totalCount : Int,
    @SerializedName("incomplete_results") val incompleteResults : Boolean,
    @SerializedName("items") val items : List<Github>
)

data class Github(
    @SerializedName("placeholder") val placeholder : Boolean = false,
    @SerializedName("infoCard") val infoCard : Boolean = false,
    @SerializedName("name") val name : String? = null,
    @SerializedName("full_name") val fullName : String? = null,
    @SerializedName("owner") val owner : GithubOwner? = null,
    @SerializedName("html_url") val htmlUrl : String? = null,
    @SerializedName("description") val description : String? = null,
    @SerializedName("forks_count") val forksCount : Int? = null,
    @SerializedName("stargazers_count") val stargazersCount : Int? = null
)

data class GithubOwner(
    @SerializedName("id") val id: Int,
    @SerializedName("login") val login: String?,
    @SerializedName("avatar_url") val avatarUrl: String?,
    @SerializedName("url") val url: String?
)