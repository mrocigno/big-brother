package br.com.mrocigno.bigbrother.network.model

import br.com.mrocigno.bigbrother.common.utils.toHtml
import br.com.mrocigno.bigbrother.network.toBase64
import kotlinx.serialization.Serializable
import okhttp3.MultipartBody
import okio.Buffer

@Serializable
data class NetworkMultiPartModel(
    val bigBrotherIdentifier: String,
    val parts: List<NetworkPartModel>
) {

    constructor(multiPart: MultipartBody) : this(
        bigBrotherIdentifier = "soy yo",
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

    fun toHtml(): String = parts.joinToString("<br><hr><br>") {
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

@Serializable
data class NetworkPartModel(
    val headers: Map<String, List<String>>?,
    val body: String?,
    val contentType: String?,
    val img: String?
)

