package br.com.mrocigno.sandman.network

import okhttp3.Request
import okhttp3.Response
import okio.Buffer


class NetworkEntryModel(
    val url: String,
    val statusCode: String?,
    val elapsedTime: String,
    val request: NetworkPayloadModel,
    val response: NetworkPayloadModel
) {

    constructor(response: Response, elapsedTime: String) : this(
        url = response.request.url.encodedPath,
        statusCode = response.code.toString(),
        elapsedTime = elapsedTime,
        request = NetworkPayloadModel(response.request),
        response = NetworkPayloadModel(response)
    )
}

class NetworkPayloadModel(
    val headers: Map<String, String>?,
    val body: String?
) {

    constructor(request: Request) : this(
        headers = request.headers.toMap(),
        body = request.let {
            val buffer = Buffer()
            it.body
                ?.writeTo(buffer)
                ?.let { buffer.readUtf8() }
        }
    )

    constructor(response: Response) : this(
        headers = response.headers.toMap(),
        body = response.let {
            val source = it.body?.source()
            source?.request(Long.MAX_VALUE)
            val buffer = source?.buffer
            buffer?.clone()?.readUtf8()
        }
    )
}