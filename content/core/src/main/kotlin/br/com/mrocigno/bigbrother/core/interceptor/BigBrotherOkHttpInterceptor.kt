package br.com.mrocigno.bigbrother.core.interceptor

import br.com.mrocigno.bigbrother.core.BigBrother
import br.com.mrocigno.bigbrother.core.model.RequestModel
import br.com.mrocigno.bigbrother.core.model.ResponseModel
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class BigBrotherOkHttpInterceptor(private vararg val blockList: String) : Interceptor {

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