package br.com.mrocigno.bigbrother.proxy

import br.com.mrocigno.bigbrother.core.BigBrotherInterceptor
import okhttp3.Request
import okhttp3.Response

internal class ProxyInterceptor : BigBrotherInterceptor {

    override val priority: Int = 1000

    override fun onRequest(request: Request): Request {
        return request.newBuilder().addHeader("proxied_request", "true").build()
    }

    override fun onResponse(response: Response): Response {
        return response.newBuilder().addHeader("proxied_response", "true").build()
    }

    override fun onError(e: Exception): Exception {
        TODO("Not yet implemented")
    }

}