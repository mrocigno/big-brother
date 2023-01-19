package br.com.mrocigno.sandman.network

import androidx.recyclerview.widget.DiffUtil
import okhttp3.Request
import okhttp3.Response
import okio.Buffer
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter

class NetworkEntryModel(
    val url: String,
    val statusCode: Int,
    val elapsedTime: String,
    val hour: LocalDateTime,
    val method: String,
    val request: NetworkPayloadModel,
    val response: NetworkPayloadModel
) {

    constructor(response: Response, elapsedTime: String) : this(
        url = response.request.url.encodedPath,
        statusCode = response.code,
        elapsedTime = elapsedTime,
        hour = LocalDateTime.now(),
        method = response.request.method,
        request = NetworkPayloadModel(response.request),
        response = NetworkPayloadModel(response)
    )

    class Differ : DiffUtil.ItemCallback<NetworkEntryModel>() {
        override fun areItemsTheSame(oldItem: NetworkEntryModel, newItem: NetworkEntryModel) =
            oldItem.hour.format(DateTimeFormatter.ISO_TIME) == newItem.hour.format(DateTimeFormatter.ISO_TIME)
                    && oldItem.url == newItem.url

        override fun areContentsTheSame(oldItem: NetworkEntryModel, newItem: NetworkEntryModel) =
            oldItem.hour.format(DateTimeFormatter.ISO_TIME) == newItem.hour.format(DateTimeFormatter.ISO_TIME)
                && oldItem.url == newItem.url
                && oldItem.method == newItem.method
                && oldItem.elapsedTime == newItem.elapsedTime

    }
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