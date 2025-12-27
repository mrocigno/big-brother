package br.com.mrocigno.bigbrother.core.model

import br.com.mrocigno.bigbrother.core.utils.toBase64
import io.ktor.client.utils.EmptyContent
import io.ktor.http.ContentType
import io.ktor.http.content.OutgoingContent
import io.ktor.http.content.TextContent
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okio.Buffer

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

        override fun toString() = "bigBrotherIdentifier" + parts.joinToString("<br><hr><br>")
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