package br.com.mrocigno.bigbrother.core.model

import android.util.Base64
import br.com.mrocigno.bigbrother.common.utils.toHtml
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.client.utils.EmptyContent
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.content.OutgoingContent
import io.ktor.http.content.TextContent
import io.ktor.http.contentType
import io.ktor.http.encodedPath
import io.ktor.util.InternalAPI
import io.ktor.util.toMap
import okhttp3.Headers
import okhttp3.MultipartBody
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

sealed interface Body {

    fun toOkHttpRequestBody(): RequestBody?

    fun toKtorRequestBody(contentType: ContentType): OutgoingContent?

    class Multipart(
        val parts: List<NetworkPartModel>
    ) : Body {

        constructor(multiPart: MultipartBody) : this(
            parts = multiPart.parts.map {
                val contentType = it.body.contentType()?.toString()
                val isImg = contentType?.startsWith("image/") == true
                val buffer = Buffer()
                it.body.writeTo(buffer)
                NetworkPartModel(
                    headers = it.headers?.toMultimap(),
                    body = if (isImg) null else buffer.readUtf8(),
                    contentType = contentType,
                    img = if (isImg) buffer.toBase64() else null
                )
            }
        )

        override fun toOkHttpRequestBody(): RequestBody? = null

        override fun toKtorRequestBody(contentType: ContentType): OutgoingContent? = null

        override fun toString() = "bigBrotherIdentifier" + parts.joinToString("<br><hr><br>") {
            buildString {
                appendLine(it.headers.toHtml())
                appendLine()
                it.body?.run(::appendLine)
                it.img?.run {
                    appendLine("<img src=\"data:image/png;base64,$this\"/>")
                }
            }
        }
    }

    class Text(val body: String?) : Body {

        constructor(body: RequestBody) : this(
            body = Buffer().use { buffer ->
                body.writeTo(buffer)
                buffer.readUtf8()
            }
        )

        override fun toOkHttpRequestBody(): RequestBody? =
            body?.toRequestBody()

        override fun toKtorRequestBody(contentType: ContentType): OutgoingContent? =
            body?.let { TextContent(it, contentType) } ?: EmptyContent

        override fun toString(): String = body.orEmpty()
    }
}

internal fun Buffer.toBase64(): String =
    Base64.encodeToString(readByteArray(), Base64.NO_WRAP)

data class NetworkPartModel(
    val headers: Map<String, List<String>>?,
    val body: String?,
    val contentType: String?,
    val img: String?
)