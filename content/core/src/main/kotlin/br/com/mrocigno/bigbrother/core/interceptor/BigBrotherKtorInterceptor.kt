package br.com.mrocigno.bigbrother.core.interceptor

import br.com.mrocigno.bigbrother.core.BigBrother
import br.com.mrocigno.bigbrother.core.model.RequestModel
import br.com.mrocigno.bigbrother.core.model.ResponseModel
import io.ktor.client.call.HttpClientCall
import io.ktor.client.plugins.HttpSendInterceptor
import io.ktor.client.plugins.Sender
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.HttpResponseData
import io.ktor.http.HttpStatusCode
import io.ktor.util.InternalAPI
import io.ktor.utils.io.ByteReadChannel

class BigBrotherKtorInterceptor(private vararg val blockList: String) : HttpSendInterceptor {

    fun Map<String, List<String>>.toKtorHeaders() =
        io.ktor.http.headersOf(*this.toList().toTypedArray())

    fun HttpRequestBuilder.isBlocked(): Boolean {
        val strUrl = url.toString()
        return blockList.any { strUrl.contains(it, true) }
    }

    @OptIn(InternalAPI::class)
    override suspend fun invoke(
        sender: Sender,
        chain: HttpRequestBuilder
    ): HttpClientCall {
        if (chain.isBlocked()) return sender.execute(chain)

        val interceptors = BigBrother
            .interceptors.map { it.invoke() }
            .sortedByDescending { it.priority }

        try {
            var request = RequestModel(chain)
            interceptors.forEach {
                request = it.onRequest(request)
            }

            val newRequest = request.applyToBuilder(chain)
            val execute = sender.execute(newRequest)

            var response = ResponseModel(execute.response)
            interceptors.forEach {
                response = it.onResponse(response)
            }

            return HttpClientCall(
                client = execute.client,
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