package br.com.mrocigno.bigbrother.core

import br.com.mrocigno.bigbrother.core.BigBrother.interceptors
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

interface BigBrotherInterceptor {

    val priority: Int

    fun onRequest(request: Request): Request

    fun onResponse(response: Response): Response

    fun onError(e: Exception): Exception
}

internal class BBInterceptor(private vararg val blockList: String) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        if (chain.request().isBlocked()) return chain.proceed(chain.request())

        val orderedInterceptors = interceptors.sortedBy { it.priority }

        try {
            var request = chain.request()
            orderedInterceptors.forEach {
                request = it.onRequest(request)
            }

            var response = chain.proceed(request)
            orderedInterceptors.reversed().forEach {
                response = it.onResponse(response)
            }

            return response
        } catch (e: Exception) {
            var exception = e
            orderedInterceptors.reversed().forEach {
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