package br.com.mrocigno.bigbrother.core

import br.com.mrocigno.bigbrother.core.model.RequestModel
import br.com.mrocigno.bigbrother.core.model.ResponseModel
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.call.HttpClientCall
import io.ktor.client.plugins.HttpSend
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.plugins.plugin
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.HttpRequestData
import io.ktor.client.request.HttpResponseData
import io.ktor.client.request.invoke
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.TextContent
import io.ktor.http.invoke
import io.ktor.util.InternalAPI
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

        val interceptors = BigBrother.interceptors.map { it.invoke() }

        val orderedInterceptors = interceptors
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

        val interceptors = BigBrother.interceptors.map { it.invoke() }

        val orderedInterceptors = interceptors
            .sortedByDescending { it.priority }

        try {
            var request = RequestModel(chain)
            orderedInterceptors.forEach {
                request = it.onRequest(request)
            }

            val newRequest = request.applyToBuilder(chain)
            val execute = execute(newRequest)

            var response = ResponseModel(execute.response)
            orderedInterceptors.forEach {
                response = it.onResponse(response)
            }

//            HttpClientCall(
//                client = this@addBigBrotherInterceptor,
//                requestData = newRequest.build(),
//                responseData = HttpResponseData(
//                    statusCode = HttpStatusCode.fromValue(response.code),
//                    requestTime = execute.response.requestTime,
//                    headers = response.headers?.toKtorHeaders() ?: execute.response.headers,
//                    version = execute.response.version,
//                    body = execute.response,
//                    callContext = execute.coroutineContext
//                ),
//            )
            execute
        } catch (e: Exception) {
            var exception = e
            orderedInterceptors.forEach {
                exception = it.onError(exception)
            }
            throw e
        }
    }
}

fun HttpClientConfig<*>.addBigBrotherInterceptor(vararg blockList: String) {

    fun HttpRequestBuilder.isBlocked(): Boolean {
        val strUrl = url.toString()
        return blockList.any { strUrl.contains(it, true) }
    }

    install(createClientPlugin("BigBrotherInterceptor") {
        onRequest { builder, content ->
            if (builder.isBlocked()) return@onRequest

            val interceptors = BigBrother.interceptors.map { it.invoke() }

            val orderedInterceptors = interceptors
                .sortedByDescending { it.priority }

            try {
                var request = RequestModel(builder)
                orderedInterceptors.forEach {
                    request = it.onRequest(request)
                }

                request.applyToBuilder(builder)
            } catch (e: Exception) {
                var exception = e
                orderedInterceptors.forEach {
                    exception = it.onError(exception)
                }
                throw e
            }
        }

        transformResponseBody { response, content, type ->
            val interceptors = BigBrother.interceptors.map { it.invoke() }

            val orderedInterceptors = interceptors
                .sortedByDescending { it.priority }

            var responseModel = ResponseModel(response)
            orderedInterceptors.forEach {
                responseModel = it.onResponse(responseModel)
            }

            ""
        }

//        transformRequestBody { request, body ->
//            var response = ResponseModel(execute.response)
//            orderedInterceptors.forEach {
//                response = it.onResponse(response)
//            }
//
//            execute
//        }
    })


}