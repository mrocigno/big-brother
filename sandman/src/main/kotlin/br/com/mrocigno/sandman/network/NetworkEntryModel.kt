package br.com.mrocigno.sandman.network

import android.os.Parcelable
import androidx.recyclerview.widget.DiffUtil
import kotlinx.parcelize.Parcelize
import okhttp3.Headers.Companion.toHeaders
import okhttp3.Request
import okhttp3.Response
import okio.Buffer
import org.json.JSONArray
import org.json.JSONObject
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter

@Parcelize
class NetworkEntryModel(
    val fullUrl: String,
    val url: String,
    val statusCode: Int,
    val elapsedTime: String,
    val hour: String,
    val method: String,
    val request: NetworkPayloadModel,
    val response: NetworkPayloadModel
) : Parcelable {

    constructor(response: Response, elapsedTime: String) : this(
        fullUrl = response.request.url.toString(),
        url = response.request.url.encodedPath,
        statusCode = response.code,
        elapsedTime = elapsedTime,
        hour = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME),
        method = response.request.method,
        request = NetworkPayloadModel(response.request),
        response = NetworkPayloadModel(response)
    )

    class Differ : DiffUtil.ItemCallback<NetworkEntryModel>() {
        override fun areItemsTheSame(oldItem: NetworkEntryModel, newItem: NetworkEntryModel) =
            oldItem.hour == newItem.hour
                && oldItem.url == newItem.url

        override fun areContentsTheSame(oldItem: NetworkEntryModel, newItem: NetworkEntryModel) = false

    }

    override fun toString() = StringBuilder()
        .appendLine(method)
        .appendLine(statusCode)
        .appendLine(url)
        .appendLine(elapsedTime)
        .appendLine(hour)
        .toString()
}

@Parcelize
class NetworkPayloadModel(
    val headers: Map<String, String>?,
    val body: String?
) : Parcelable {

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

    val formattedBody: CharSequence? get() = if (body.isNullOrBlank()) "empty" else runCatching {
        JSONObject(body!!).toString(2)
    }.recoverCatching {
        JSONArray(body).toString(2)
    }.getOrNull()

    val formattedHeaders: CharSequence? get() = if (headers.isNullOrEmpty()) "empty" else {
        headers.toHeaders().toString()
    }
}