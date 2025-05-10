package br.com.mrocigno.bigbrother.proxy

import androidx.core.net.toUri
import br.com.mrocigno.bigbrother.common.dao.ProxyDao
import br.com.mrocigno.bigbrother.common.db.BigBrotherDatabase.Companion.bbdb
import br.com.mrocigno.bigbrother.core.BigBrotherInterceptor
import br.com.mrocigno.bigbrother.proxy.model.ProxyActionModel
import br.com.mrocigno.bigbrother.proxy.model.ProxyActions
import br.com.mrocigno.bigbrother.proxy.model.ProxyRuleModel
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response

internal class ProxyInterceptor : BigBrotherInterceptor {

    private val proxyDao: ProxyDao? get() = bbdb?.proxyDao()
    override val priority: Int = 1000

    override fun onRequest(request: Request): Request {
        val rules = proxyDao?.getAllEnabled()
            ?.map(::ProxyRuleModel)
            ?.filter { it.matches(request) }
            ?.takeIf { it.isNotEmpty() }
            ?: return request

        val actions = rules.flatMap { it.actions }
        return request.applyAllActions(actions)
    }

    override fun onResponse(response: Response): Response {
        return response.newBuilder().addHeader("proxied_response", "true").build()
    }

    override fun onError(e: Exception): Exception {
        throw e
    }

    private fun Request.applyAllActions(actions: List<ProxyActionModel>): Request {
        val pathBuilder = url.toString().toUri().buildUpon()
        val builder = newBuilder()
        actions.forEach {
            when (it.action) {
                ProxyActions.EMPTY -> Unit
                ProxyActions.SET_BODY -> {
                    val body = it.body
                        ?.takeIf { !method.equals("get", true) }
                        ?.toRequestBody()

                    builder.method(method, body)
                }

                ProxyActions.SET_HEADER -> {
                    builder.header(it.name.orEmpty(), it.value.orEmpty())
                }

                ProxyActions.SET_PATH -> {
                    val newPath = it.value.orEmpty().toUri()
                    pathBuilder.encodedAuthority(newPath.encodedAuthority)
                    pathBuilder.encodedPath(newPath.encodedPath)
                    pathBuilder.encodedQuery(newPath.encodedQuery)
                }

                ProxyActions.SET_QUERY -> {
                    pathBuilder.appendQueryParameter(it.name.orEmpty(), it.value.orEmpty())
                }

                ProxyActions.REMOVE_HEADER -> {
                    builder.removeHeader(it.name.orEmpty())
                }

                ProxyActions.REMOVE_QUERY -> {

                }
            }
        }
        builder.url(pathBuilder.build().toString())
        return builder.build()
    }
}