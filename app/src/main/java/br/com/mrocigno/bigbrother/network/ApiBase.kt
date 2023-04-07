package br.com.mrocigno.bigbrother.network

data class ApiBase(
    val total_count : Int,
    val incomplete_results : Boolean,
    val items : List<Github>
)

data class Github(
    val placeholder : Boolean = false,
    val infoCard : Boolean = false,
    val name : String? = null,
    val full_name : String? = null,
    val owner : GithubOwner? = null,
    val html_url : String? = null,
    val description : String? = null,
    val forks_count : Int? = null,
    val stargazers_count : Int? = null
)

data class GithubOwner(
    val id: Int,
    val login: String?,
    val avatar_url: String?,
    val url: String?
)