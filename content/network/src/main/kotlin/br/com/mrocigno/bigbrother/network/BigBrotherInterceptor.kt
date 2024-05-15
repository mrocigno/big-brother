package br.com.mrocigno.bigbrother.network

import br.com.mrocigno.bigbrother.network.model.NetworkEntryModel
import br.com.mrocigno.bigbrother.network.model.NetworkPayloadModel
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class BigBrotherInterceptor(private vararg val blockList: String) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val startingAt = System.currentTimeMillis()
        val request = chain.request()

        if (request.isBlocked()) return chain.proceed(request)

        val entry = NetworkEntryModel(request)
        entry.generatedId = NetworkHolder.addEntry(entry)
        try {
            val response = chain.proceed(request)

            val endingAt = System.currentTimeMillis()

            entry.elapsedTime = "${endingAt - startingAt}ms"
            entry.statusCode = response.code
            entry.response = NetworkPayloadModel(response)

            NetworkHolder.updateEntry(entry)

            return response
        } catch (exception: Exception) {
            val endingAt = System.currentTimeMillis()

            entry.elapsedTime = "${endingAt - startingAt}ms"
            entry.statusCode = -1
            entry.response = NetworkPayloadModel(exception)

            NetworkHolder.updateEntry(entry)

            throw exception
        }
    }

    private fun Request.isBlocked(): Boolean {
        val strUrl = url.toString()
        return blockList.any { strUrl.contains(it, true) }
    }
}