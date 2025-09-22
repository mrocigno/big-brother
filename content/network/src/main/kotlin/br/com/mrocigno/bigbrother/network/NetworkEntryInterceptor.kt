package br.com.mrocigno.bigbrother.network

import br.com.mrocigno.bigbrother.common.route.PROXY_APPLIED_HEADER
import br.com.mrocigno.bigbrother.core.BBInterceptor
import br.com.mrocigno.bigbrother.core.model.RequestModel
import br.com.mrocigno.bigbrother.core.model.ResponseModel
import br.com.mrocigno.bigbrother.network.model.NetworkEntryModel
import br.com.mrocigno.bigbrother.network.model.NetworkPayloadModel
import java.util.Objects

internal class NetworkEntryInterceptor : BBInterceptor {

    override val priority: Int = 0

    private val startingAt = ThreadLocal<Long>()
    private val entry = ThreadLocal<NetworkEntryModel>()

    override fun onRequest(request: RequestModel): RequestModel {
        startingAt.set(System.currentTimeMillis())
        val requestModel = NetworkEntryModel(request)
        entry.set(requestModel.copy(id = BigBrotherNetworkHolder.addEntry(requestModel)))
        return request
    }

    override fun onResponse(response: ResponseModel): ResponseModel {
        val endingAt = System.currentTimeMillis()
        val payloadModel = NetworkPayloadModel(response)
        val currentEntry = entry.get()?.copy(
            elapsedTime = "${endingAt - (startingAt.get() ?: endingAt)}ms",
            statusCode = response.code,
            response = payloadModel,
            proxyRules = payloadModel.headers?.get(PROXY_APPLIED_HEADER)?.joinToString()
        ) ?: throw IllegalStateException("Unknow request")
        entry.remove()
        startingAt.remove()

        BigBrotherNetworkHolder.updateEntry(currentEntry)

        return response
    }

    override fun onError(e: Exception): Exception {
        val endingAt = System.currentTimeMillis()
        val currentEntry = entry.get()?.copy(
            elapsedTime = "${endingAt - (startingAt.get() ?: endingAt)}ms",
            statusCode = -1,
            response = NetworkPayloadModel(e)
        ) ?: throw IllegalStateException("Unknow request")
        entry.remove()
        startingAt.remove()

        BigBrotherNetworkHolder.updateEntry(currentEntry)

        return e
    }

    override fun hashCode(): Int = Objects.hashCode("NetworkEntryInterceptor")

    override fun equals(other: Any?): Boolean =
        hashCode() == other?.hashCode()
}
