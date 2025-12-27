package br.com.mrocigno.bigbrother.network

import br.com.mrocigno.bigbrother.core.utils.addBigBrotherInterceptor
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.gson.gson

object NetworkKtor {

    val ktorClient = HttpClient {
        expectSuccess = true

        defaultRequest {
            url("https://api.github.com/")
        }

        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.ALL
        }

        install(HttpTimeout) {
            requestTimeoutMillis = 30_000L
            connectTimeoutMillis = 30_000L
            socketTimeoutMillis = 30_000L
        }

        install(ContentNegotiation) {
            gson {
                setPrettyPrinting()
            }
        }
    }.apply {
        addBigBrotherInterceptor("dont/intercept/this", "not/even/this")
    }
}