package br.com.mrocigno.sandman.corinthian

import android.content.Context
import android.os.Parcelable
import androidx.recyclerview.widget.DiffUtil
import br.com.mrocigno.sandman.common.utils.appendSeparation
import br.com.mrocigno.sandman.core.model.ReportModel
import br.com.mrocigno.sandman.core.model.ReportModelType
import kotlinx.parcelize.IgnoredOnParcel
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
    var statusCode: Int? = null,
    var elapsedTime: String? = null,
    val hour: String,
    val method: String,
    val request: NetworkPayloadModel,
    var response: NetworkPayloadModel? = null,
    var isSelected: Boolean = false
) : ReportModel(
    type = ReportModelType.NETWORK
) {

    constructor(request: Request) : this(
        fullUrl = request.url.toString(),
        url = request.url.encodedPath,
        hour = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME),
        method = request.method,
        request = NetworkPayloadModel(request)
    )

    class Differ : DiffUtil.ItemCallback<NetworkEntryModel>() {
        override fun areItemsTheSame(oldItem: NetworkEntryModel, newItem: NetworkEntryModel) =
            oldItem.hour == newItem.hour
                && oldItem.url == newItem.url

        override fun areContentsTheSame(oldItem: NetworkEntryModel, newItem: NetworkEntryModel) = false

    }

    override fun asTxt() = StringBuilder()
        .append("> ")
        .append(type.name)
        .appendSeparation()
        .append(method)
        .appendSeparation()
        .append(statusCode)
        .appendSeparation()
        .append(url)
        .toString()

    override fun toString() = StringBuilder()
        .appendLine(method)
        .appendLine(statusCode)
        .appendLine(url)
        .appendLine(elapsedTime)
        .appendLine(hour)
        .toString()

    fun all(context: Context) =
        context.getString(R.string.network_copy_all_template,
            fullUrl,
            method,
            (statusCode ?: -1).toString(),
            hour,
            elapsedTime,
            request.formattedHeaders,
            request.formattedBody,
            response?.formattedHeaders ?: "empty",
            response?.formattedBody ?: "empty"
        )
}

@Parcelize
class NetworkPayloadModel(
    val headers: Map<String, String>?,
    val body: String?
) : Parcelable {

    @IgnoredOnParcel
    var isBodyFormatted: Boolean = false

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

    constructor(exception: Exception) : this(
        headers = null,
        body = exception.stackTraceToString()
    )

    val formattedBody: CharSequence? get() =
        if (body.isNullOrBlank()) "empty" else runCatching {
            isBodyFormatted = true
            JSONObject(body!!).toString(2)
        }.recoverCatching {
            isBodyFormatted = true
            JSONArray(body).toString(2)
        }.getOrElse {
            isBodyFormatted = false
            body
        }

    val formattedHeaders: CharSequence? get() =
        if (headers.isNullOrEmpty()) "empty" else {
            headers.toHeaders().toString()
        }
}