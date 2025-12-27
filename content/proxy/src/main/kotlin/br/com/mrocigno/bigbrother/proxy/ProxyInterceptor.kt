package br.com.mrocigno.bigbrother.proxy

import android.net.Uri
import androidx.core.net.toUri
import br.com.mrocigno.bigbrother.common.dao.ProxyDao
import br.com.mrocigno.bigbrother.common.db.BigBrotherDatabase.Companion.bbdb
import br.com.mrocigno.bigbrother.common.route.PROXY_APPLIED_HEADER
import br.com.mrocigno.bigbrother.core.interceptor.BigBrotherInterceptor
import br.com.mrocigno.bigbrother.core.model.Body
import br.com.mrocigno.bigbrother.core.model.RequestModel
import br.com.mrocigno.bigbrother.core.model.ResponseModel
import br.com.mrocigno.bigbrother.proxy.model.ProxyActionModel
import br.com.mrocigno.bigbrother.proxy.model.ProxyActions
import br.com.mrocigno.bigbrother.proxy.model.ProxyRuleModel

internal class ProxyInterceptor : BigBrotherInterceptor {

    override val priority: Int = 1000
    private val proxyDao: ProxyDao? get() = bbdb?.proxyDao()
    private var rules: List<ProxyRuleModel> = emptyList()

    override fun onRequest(request: RequestModel): RequestModel {
        val list = proxyDao?.getAllEnabled()
            ?.map(::ProxyRuleModel)
            ?.filter { it.matches(request) }
            ?.takeIf { it.isNotEmpty() }
            ?: return request

        rules = list
        val actions = list.flatMap { it.actions }
        return request.applyAllActions(actions)
    }

    override fun onResponse(response: ResponseModel): ResponseModel {
        val list = rules.ifEmpty { return response }

        return response.applyAllActions(list)
    }

    override fun onError(e: Exception): Exception {
        rules = emptyList()
        return e
    }

    private fun RequestModel.applyAllActions(actions: List<ProxyActionModel>): RequestModel {
        val pathBuilder = url.toUri().buildUpon()
        var builder = this
        var newMethod = method
        var newBody = body
        actions.forEach {
            when (it.action) {
                ProxyActions.EMPTY,
                ProxyActions.SET_BODY_RESPONSE,
                ProxyActions.SET_RESPONSE_CODE -> Unit

                ProxyActions.SET_BODY_REQUEST -> {
                    newBody = Body.Text(it.body)
                }

                ProxyActions.SET_HEADER -> {
                    builder = builder.copy(
                        headers = (builder.headers ?: mutableMapOf()) + mapOf(
                            it.name.orEmpty() to listOf(it.value.toString())
                        )
                    )
                }

                ProxyActions.SET_METHOD -> {
                    newMethod = it.value.orEmpty()
                    if (!newMethod.equals("get", true) && newBody == null) {
                        newBody = Body.Text("")
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
                    builder = builder.copy(
                        headers = (builder.headers ?: mutableMapOf()) - it.name.orEmpty()
                    )
                }

                ProxyActions.REMOVE_QUERY -> {
                    pathBuilder.setQuery(it.name.orEmpty(), null)
                }
            }
        }

        return builder.copy(
            method = newMethod,
            body = newBody.takeIf { !newMethod.equals("get", true) },
            url = pathBuilder.build().toString()
        )
    }

    private fun ResponseModel.applyAllActions(rules: List<ProxyRuleModel>): ResponseModel {
        val actions = rules.flatMap { it.actions }
        var builder = copy(
            headers = (headers ?: mutableMapOf()) + mapOf(
                PROXY_APPLIED_HEADER to listOf(rules.joinToString { it.id.toString() })
            )
        )

        actions.forEach {
            when (it.action) {
                ProxyActions.EMPTY,
                ProxyActions.SET_BODY_REQUEST,
                ProxyActions.SET_HEADER,
                ProxyActions.SET_METHOD,
                ProxyActions.SET_PATH,
                ProxyActions.SET_QUERY,
                ProxyActions.REMOVE_HEADER,
                ProxyActions.REMOVE_QUERY -> Unit

                ProxyActions.SET_BODY_RESPONSE -> {
                    builder = builder.copy(body = it.body)
                }

                ProxyActions.SET_RESPONSE_CODE -> {
                    builder = builder.copy(code = it.value?.toIntOrNull() ?: -1)
                }
            }
        }

        return builder
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