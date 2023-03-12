package br.com.mrocigno.sandman.network

import okhttp3.Interceptor
import okhttp3.Response

class SandmanInterceptor : Interceptor {

    private val TAG = "SandmanInterceptor"

    override fun intercept(chain: Interceptor.Chain): Response {
        val startingAt = System.currentTimeMillis()
        val request = chain.request()
        val entry = NetworkEntryModel(request)
        NetworkHolder.addEntry(entry)
        try {
            val response = chain.proceed(request)

            Thread.sleep(5000)

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