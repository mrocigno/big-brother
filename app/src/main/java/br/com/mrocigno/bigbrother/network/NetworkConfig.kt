package br.com.mrocigno.bigbrother.network

import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object NetworkConfig {

    private const val BASE_URL = "https://api.github.com/"

    private val okHttpClient : OkHttpClient = OkHttpClient.Builder()
        .bigBrotherIntercept()
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

}