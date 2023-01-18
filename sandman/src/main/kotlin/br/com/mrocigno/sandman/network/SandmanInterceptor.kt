package br.com.mrocigno.sandman.network

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import java.lang.Exception

class SandmanInterceptor : Interceptor {

    private val TAG = "SandmanInterceptor"

    override fun intercept(chain: Interceptor.Chain): Response {
        val startingAt = System.currentTimeMillis()
        try {
            val response = chain.proceed(chain.request())
            val endingAt = System.currentTimeMillis()

            NetworkHolder.addEntry(
                NetworkEntryModel(
                    response,
                    "${endingAt - startingAt}ms"
                )
            )

            return response
        } catch (e: Exception) {
            throw e
        }
    }
}