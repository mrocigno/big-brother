package br.com.mrocigno.bigbrother.core.interceptor

import br.com.mrocigno.bigbrother.core.model.RequestModel
import br.com.mrocigno.bigbrother.core.model.ResponseModel

interface BigBrotherInterceptor {

    val priority: Int

    fun onRequest(request: RequestModel): RequestModel

    fun onResponse(response: ResponseModel): ResponseModel

    fun onError(e: Exception): Exception
}
