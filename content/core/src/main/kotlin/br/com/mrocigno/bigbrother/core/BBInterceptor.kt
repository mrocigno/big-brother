package br.com.mrocigno.bigbrother.core

import br.com.mrocigno.bigbrother.core.model.RequestModel
import br.com.mrocigno.bigbrother.core.model.ResponseModel
import io.ktor.client.HttpClient
import io.ktor.client.call.HttpClientCall
import io.ktor.client.plugins.HttpSend
import io.ktor.client.plugins.plugin
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.HttpResponseData
import io.ktor.http.HttpStatusCode
import io.ktor.util.InternalAPI
import io.ktor.utils.io.ByteReadChannel
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

interface BBInterceptor {

    val priority: Int

    fun onRequest(request: RequestModel): RequestModel

    fun onResponse(response: ResponseModel): ResponseModel

    fun onError(e: Exception): Exception
}

class BigBrotherInterceptor(private vararg val blockList: String) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        if (chain.request().isBlocked()) return chain.proceed(chain.request())

        val orderedInterceptors = BigBrother.interceptors
            .map { it.invoke() }
            .sortedByDescending { it.priority }

        try {
            var request = RequestModel(chain.request())
            orderedInterceptors.forEach {
                request = it.onRequest(request)
            }

            val newRequest = request.toOkHttpRequest(chain.request().newBuilder())
            val proceed = chain.proceed(newRequest)

            var response = ResponseModel(proceed)
            orderedInterceptors.forEach {
                response = it.onResponse(response)
            }

            return response.toOkHttpResponse(proceed.newBuilder())
        } catch (e: Exception) {
            var exception = e
            orderedInterceptors.forEach {
                exception = it.onError(exception)
            }
            throw e
        }
    }

    private fun Request.isBlocked(): Boolean {
        val strUrl = url.toString()
        return blockList.any { strUrl.contains(it, true) }
    }
}

@OptIn(InternalAPI::class)
fun HttpClient.addBigBrotherInterceptor(vararg blockList: String) {

    fun Map<String, List<String>>.toKtorHeaders() =
        io.ktor.http.headersOf(*this.toList().toTypedArray())

    fun HttpRequestBuilder.isBlocked(): Boolean {
        val strUrl = url.toString()
        return blockList.any { strUrl.contains(it, true) }
    }

    plugin(HttpSend).intercept { chain ->
        if (chain.isBlocked()) return@intercept execute(chain)

        val interceptors = BigBrother
            .interceptors.map { it.invoke() }
            .sortedByDescending { it.priority }

        try {
            var request = RequestModel(chain)
            interceptors.forEach {
                request = it.onRequest(request)
            }

            val newRequest = request.applyToBuilder(chain)
            val execute = execute(newRequest)

            var response = ResponseModel(execute.response)
            interceptors.forEach {
                response = it.onResponse(response)
            }

            HttpClientCall(
                client = this@addBigBrotherInterceptor,
                requestData = newRequest.build(),
                responseData = HttpResponseData(
                    statusCode = HttpStatusCode.fromValue(response.code),
                    requestTime = execute.response.requestTime,
                    headers = response.headers?.toKtorHeaders() ?: execute.response.headers,
                    version = execute.response.version,
                    body = response.body?.run(::ByteReadChannel) ?: execute.response.content,
                    callContext = execute.coroutineContext
                ),
            )
        } catch (e: Exception) {
            var exception = e
            interceptors.forEach {
                exception = it.onError(exception)
            }
            throw e
        }
    }
}
