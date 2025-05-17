package br.com.mrocigno.bigbrother.proxy

import android.net.Uri
import androidx.core.net.toUri
import br.com.mrocigno.bigbrother.common.dao.ProxyDao
import br.com.mrocigno.bigbrother.common.db.BigBrotherDatabase.Companion.bbdb
import br.com.mrocigno.bigbrother.common.route.PROXY_APPLIED_HEADER
import br.com.mrocigno.bigbrother.core.BBInterceptor
import br.com.mrocigno.bigbrother.proxy.model.ProxyActionModel
import br.com.mrocigno.bigbrother.proxy.model.ProxyActions
import br.com.mrocigno.bigbrother.proxy.model.ProxyRuleModel
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response

internal class ProxyInterceptor : BBInterceptor {

    override val priority: Int = 1000
    private val proxyDao: ProxyDao? get() = bbdb?.proxyDao()
    private val rules: ThreadLocal<List<ProxyRuleModel>> = ThreadLocal()

    override fun onRequest(request: Request): Request {
        val list = proxyDao?.getAllEnabled()
            ?.map(::ProxyRuleModel)
            ?.filter { it.matches(request) }
            ?.takeIf { it.isNotEmpty() }
            ?: return request

        rules.set(list)
        val actions = list.flatMap { it.actions }
        return request.applyAllActions(actions)
    }

    override fun onResponse(response: Response): Response {
        val list = rules.get() ?: return response
        rules.remove()

        return response.newBuilder()
            .addHeader(PROXY_APPLIED_HEADER, list.joinToString { it.id.toString() })
            .build()
    }

    override fun onError(e: Exception): Exception {
        rules.remove()
        return e
    }

    private fun Request.applyAllActions(actions: List<ProxyActionModel>): Request {
        val pathBuilder = url.toString().toUri().buildUpon()
        val builder = newBuilder()
        var newMethod = method
        var newBody = body
        actions.forEach {
            when (it.action) {
                ProxyActions.EMPTY -> Unit
                ProxyActions.SET_BODY -> {
                    newBody = it.body?.toRequestBody()
                }

                ProxyActions.SET_HEADER -> {
                    builder.header(it.name.orEmpty(), it.value.orEmpty())
                }

                ProxyActions.SET_METHOD -> {
                    newMethod = it.value.orEmpty()
                    if (!newMethod.equals("get", true) && newBody == null) {
                        newBody = "".toRequestBody()
                    }
                }

                ProxyActions.SET_PATH -> {
                    val newPath = it.value.orEmpty().toUri()
                    newPath.scheme?.run(pathBuilder::scheme)
                    newPath.encodedAuthority?.run(pathBuilder::encodedAuthority)
                    newPath.encodedPath?.run(pathBuilder::encodedPath)
                    newPath.encodedQuery?.run(pathBuilder::encodedQuery)
                }

                ProxyActions.SET_QUERY -> {
                    pathBuilder.setQuery(it.name.orEmpty(), it.value.orEmpty())
                }

                ProxyActions.REMOVE_HEADER -> {
                    builder.removeHeader(it.name.orEmpty())
                }

                ProxyActions.REMOVE_QUERY -> {
                    pathBuilder.setQuery(it.name.orEmpty(), null)
                }
            }
        }
        builder.method(newMethod, newBody.takeIf { !newMethod.equals("get", true) })
        builder.url(pathBuilder.build().toString())
        return builder.build()
    }

    private fun Uri.Builder.setQuery(name: String, value: String? = null) {
        val current = toString().toUri()
        clearQuery()
        current.queryParameterNames
            .filterNot { it == name }
            .forEach { query ->
                appendQueryParameter(query, current.getQueryParameter(query))
            }

        value?.takeIf { it.isNotBlank() }?.run {
            appendQueryParameter(name, value)
        }
    }
}