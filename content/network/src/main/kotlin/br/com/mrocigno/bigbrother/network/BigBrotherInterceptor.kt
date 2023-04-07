package br.com.mrocigno.bigbrother.network

import br.com.mrocigno.bigbrother.core.utils.track
import okhttp3.Interceptor
import okhttp3.Response

class BigBrotherInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val startingAt = System.currentTimeMillis()
        val request = chain.request()
        val entry = NetworkEntryModel(request)
        entry.track()
        NetworkHolder.addEntry(entry)
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
}