package br.com.mrocigno.bigbrother.core.model

import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.url
import io.ktor.http.HttpMethod
import io.ktor.http.encodedPath
import io.ktor.util.toMap
import okhttp3.Headers
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okio.Buffer

data class RequestModel(
    val url: String,
    val encodedPath: String,
    val method: String,
    val headers: Map<String, List<String>>?,
    val body: Body?
) {

    constructor(request: okhttp3.Request) : this(
        url = request.url.toString(),
        encodedPath = request.url.encodedPath,
        method = request.method,
        headers = request.headers.toMultimap(),
        body = request.body?.let { Body.Text(it) }
    )

    constructor(request: HttpRequestBuilder) : this(
        url = request.url.toString(),
        encodedPath = request.url.encodedPath,
        method = request.method.value,
        headers = request.headers.build().toMap(),
        body = request.body.toString().let { Body.Text(it) }
    )

    fun toOkHttpRequest(builder: okhttp3.Request.Builder) = builder
        .url(url)
        .method(method, body?.toOkHttpRequestBody())
        .headers(Headers.headersOf(*getHeadersAsList()))
        .build()

    fun applyToBuilder(builder: HttpRequestBuilder) = builder.also {
        it.url(url)
        it.method = HttpMethod.parse(method.uppercase())
        headers?.forEach { (name, values) ->
            it.headers[name] = values.joinToString()
        }
    }

    private fun getHeadersAsList() = headers?.map { (name, values) ->
        listOf(name, values.joinToString())
    }?.flatten()?.toTypedArray() ?: emptyArray()
}

sealed interface Body {

    fun toOkHttpRequestBody(): RequestBody?

    class Multipart() : Body {
        override fun toOkHttpRequestBody(): RequestBody {
            TODO("Not yet implemented")
        }
    }

    class Text(val body: String?) : Body {

        constructor(body: RequestBody) : this(
            body = Buffer().use { buffer ->
                body.writeTo(buffer)
                buffer.readUtf8()
            }
        )

        override fun toOkHttpRequestBody(): RequestBody? {
            return body?.toRequestBody()
        }

        override fun toString(): String = body.orEmpty()
    }
}
