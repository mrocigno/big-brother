package br.com.mrocigno.bigbrother.core.model

import br.com.mrocigno.bigbrother.common.utils.toHtml
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.content.TextContent
import io.ktor.http.contentType
import io.ktor.http.encodedPath
import io.ktor.util.InternalAPI
import io.ktor.util.toMap
import okhttp3.Headers
import okhttp3.MultipartBody

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
        body = when (val body = request.body) {
            is MultipartBody -> Body.Multipart(body)
            null -> null
            else -> Body.Text(body)
        }
    )

    constructor(request: HttpRequestBuilder) : this(
        url = request.url.toString(),
        encodedPath = request.url.encodedPath,
        method = request.method.value,
        headers = request.headers.build().toMap(),
        body = when (val body = request.body) {
            is TextContent -> Body.Text(body.text)
            else -> null
        }
    )

    fun toOkHttpRequest(builder: okhttp3.Request.Builder) = builder.apply {
        url(url)
        if (body !is Body.Multipart) method(method, body?.toOkHttpRequestBody())
        headers(Headers.headersOf(*getHeadersAsList()))
    }.build()


    @OptIn(InternalAPI::class)
    fun applyToBuilder(builder: HttpRequestBuilder) = builder.also { request ->
        request.url(url)
        request.method = HttpMethod.parse(method.uppercase())
        headers?.forEach { (name, values) ->
            request.headers[name] = values.joinToString()
        }
        val contentType = request.contentType() ?: ContentType.Application.Json
        body?.toKtorRequestBody(contentType)?.run(request::setBody)
    }

    private fun getHeadersAsList() = headers?.map { (name, values) ->
        listOf(name, values.joinToString())
    }?.flatten()?.toTypedArray() ?: emptyArray()
}

data class NetworkPartModel(
    val headers: Map<String, List<String>>?,
    val body: String?,
    val contentType: String?,
    val img: String?
) {

    override fun toString() =
        buildString {
            appendLine(headers.toHtml())
            appendLine()
            body?.run(::appendLine)
            img?.run {
                appendLine("<img src=\"data:image/png;base64,$this\"/>")
            }
        }
}