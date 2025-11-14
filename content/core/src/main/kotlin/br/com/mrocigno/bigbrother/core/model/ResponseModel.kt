package br.com.mrocigno.bigbrother.core.model

import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.util.toMap
import kotlinx.coroutines.runBlocking
import okhttp3.Headers
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody

data class ResponseModel(
    val code: Int,
    val headers: Map<String, List<String>>?,
    val body: String?
) {

    constructor(response: Response) : this(
        code = response.code,
        headers = response.headers.toMultimap(),
        body = response.body?.let { body ->
            val source = body.source()
            source.request(Long.MAX_VALUE)
            val buffer = source.buffer
            buffer.clone().readUtf8()
        }
    )

    constructor(response: HttpResponse) : this(
        code = response.status.value,
        headers = response.headers.toMap(),
        body = runBlocking { response.bodyAsText() }
    )

    fun toOkHttpResponse(builder: Response.Builder) = builder
        .code(code)
        .headers(Headers.headersOf(*getHeadersAsList()))
        .body(body?.toResponseBody())
        .build()

    private fun getHeadersAsList() = headers?.map { (name, values) ->
        listOf(name, values.joinToString())
    }?.flatten()?.toTypedArray() ?: emptyArray()
}