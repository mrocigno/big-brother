package br.com.mrocigno.bigbrother.network.model

import androidx.recyclerview.widget.DiffUtil
import br.com.mrocigno.bigbrother.common.entity.NetworkEntity
import br.com.mrocigno.bigbrother.common.utils.bbSessionId
import br.com.mrocigno.bigbrother.common.utils.toReadable
import br.com.mrocigno.bigbrother.core.model.RequestModel
import br.com.mrocigno.bigbrother.core.model.ResponseModel
import br.com.mrocigno.bigbrother.report.bbTrack
import br.com.mrocigno.bigbrother.report.model.ReportType
import org.json.JSONArray
import org.json.JSONObject
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter
import java.io.Serializable

data class NetworkEntryModel(
    val id: Long = 0,
    val fullUrl: String,
    val url: String,
    var statusCode: Int? = null,
    var elapsedTime: String? = null,
    val hour: String,
    val method: String,
    val request: NetworkPayloadModel,
    var response: NetworkPayloadModel? = null,
    val proxyRules: String? = null
) : Serializable {

    constructor(request: RequestModel) : this(
        fullUrl = request.url,
        url = request.encodedPath,
        hour = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME),
        method = request.method,
        request = NetworkPayloadModel(request)
    )

    constructor(entry: NetworkEntity) : this(
        id = entry.id,
        fullUrl = entry.fullUrl,
        url = entry.url,
        statusCode = entry.statusCode,
        elapsedTime = entry.elapsedTime,
        hour = entry.hour,
        method = entry.method,
        proxyRules = entry.proxyRules,
        request = NetworkPayloadModel.fromString(entry.requestHeader, entry.requestBody)!!,
        response = NetworkPayloadModel.fromString(entry.responseHeader, entry.responseBody)
    )

    internal class Differ : DiffUtil.ItemCallback<NetworkEntryModel>() {
        override fun areItemsTheSame(oldItem: NetworkEntryModel, newItem: NetworkEntryModel) =
            oldItem.hour == newItem.hour
                && oldItem.url == newItem.url

        override fun areContentsTheSame(oldItem: NetworkEntryModel, newItem: NetworkEntryModel) = false

    }

    fun track() = runCatching {
        bbTrack(ReportType.NETWORK) {
            "> NETWORK - $method - $statusCode - $url"
        }
    }

    override fun toString() = StringBuilder()
        .appendLine(method)
        .appendLine(statusCode)
        .appendLine(url)
        .appendLine(elapsedTime)
        .appendLine(hour)
        .toString()

    fun toCURL() = StringBuilder("curl --location --request $method '$fullUrl'").apply {
        request.headers?.takeIf { it.isNotEmpty() }
            ?.map {
                "${it.key}: ${it.value.joinToString(", ")}"
            }
            ?.joinToString(" \\\n") { "--header '$it'" }
            ?.let { headers ->
                append(" \\")
                appendLine()
                append(headers)
            }

        if (!request.body.takeIf { it != "empty" }.isNullOrBlank()) {
            append(" \\")
            appendLine()
            append("--data '${request.body}'")
        }
    }.toString()

    internal fun toEntity(): NetworkEntity = NetworkEntity(
        id = id,
        sessionId = bbSessionId,
        fullUrl = fullUrl,
        url = url,
        statusCode = statusCode,
        elapsedTime = elapsedTime,
        hour = hour,
        method = method,
        proxyRules = proxyRules,
        requestHeader = request.formattedHeaders,
        requestBody = request.formattedBody,
        responseHeader = response?.formattedHeaders.toString(),
        responseBody = response?.formattedBody.toString()
    )
}

class NetworkPayloadModel(
    val headers: Map<String, List<String>>?,
    val body: String?
) : Serializable {

    constructor(request: RequestModel) : this(
        headers = request.headers,
        body = request.body?.toString()
    )

    constructor(response: ResponseModel) : this(
        headers = response.headers,
        body = response.body
    )

    constructor(exception: Exception) : this(
        headers = null,
        body = exception.stackTraceToString()
    )

    val formattedBody: String get() =
        if (body.isNullOrBlank()) "empty" else runCatching {
            JSONObject(body!!).toString(2)
        }.recoverCatching {
            JSONArray(body).toString(2)
        }.getOrElse {
            body.trim()
        }

    val formattedHeaders: String get() =
        if (headers.isNullOrEmpty()) "empty" else headers.toReadable()

    companion object {

        fun fromString(header: String?, body: String?): NetworkPayloadModel? {
            if (header == null && body == null) return null
            val headerMap = header?.trim()?.split("\n")?.mapNotNull {
                runCatching {
                    val (key, value) = it.split(": ")
                    key to value.split(", ")
                }.getOrNull()
            }?.toMap()

            return NetworkPayloadModel(headerMap, body)
        }
    }
}