package br.com.mrocigno.bigbrother.network

import br.com.mrocigno.bigbrother.network.model.NetworkEntryModel
import br.com.mrocigno.bigbrother.network.model.NetworkPayloadModel
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object NetworkConfig {

    private const val BASE_URL = "https://api.github.com/"

    private val okHttpClient : OkHttpClient = OkHttpClient.Builder()
        .bigBrotherIntercept(blockList = arrayOf(
            "dont/intercept/this",
            "not/even/this"
        ))
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .addInterceptor {
            if (it.request().headers["slowly"] == "true") Thread.sleep(5000L)
            it.proceed(it.request())
        }
        .build()

    val retrofit : Retrofit = Retrofit.Builder()
        .addConverterFactory(
            GsonConverterFactory.create(
                GsonBuilder().apply {
                    setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
                }.create()
            )
        )
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .build()

    init {
        BigBrotherNetworkHolder.addEntry(
            NetworkEntryModel(
                fullUrl = "www.google.com/example",
                url = "/example",
                statusCode = 200,
                hour = "10:00",
                elapsedTime = "10ms",
                method = "GET",
                request = NetworkPayloadModel(
                    headers = mapOf("Authorization" to listOf("abc123")),
                    body = null
                ),
                response = NetworkPayloadModel(
                    headers = emptyMap(),
                    body = """
                        {"response": "value"}
                    """
                )
            )
        )
    }
}