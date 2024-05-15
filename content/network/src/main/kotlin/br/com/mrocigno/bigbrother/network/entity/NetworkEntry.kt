package br.com.mrocigno.bigbrother.network.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import br.com.mrocigno.bigbrother.core.utils.bbSessionId
import br.com.mrocigno.bigbrother.network.model.NetworkEntryModel

@Entity(tableName = "tblNetwork")
class NetworkEntry(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") val id: Long = 0,
    @ColumnInfo(name = "session_id") val sessionId: Long = 0,
    @ColumnInfo(name = "full_url") val fullUrl: String,
    @ColumnInfo(name = "url") val url: String,
    @ColumnInfo(name = "status_code") var statusCode: Int? = null,
    @ColumnInfo(name = "elapsed_time") var elapsedTime: String? = null,
    @ColumnInfo(name = "hour") val hour: String,
    @ColumnInfo(name = "method") val method: String,
    @ColumnInfo(name = "request_header") val requestHeader: String? = null,
    @ColumnInfo(name = "request_body") val requestBody: String? = null,
    @ColumnInfo(name = "response_header") val responseHeader: String? = null,
    @ColumnInfo(name = "response_body") val responseBody: String? = null
) {

    constructor(model: NetworkEntryModel) : this(
        id = model.id,
        sessionId = bbSessionId,
        fullUrl = model.fullUrl,
        url = model.url,
        statusCode = model.statusCode,
        elapsedTime = model.elapsedTime,
        hour = model.hour,
        method = model.method,
        requestHeader = model.request.formattedHeaders.toString(),
        requestBody = model.request.formattedBody.toString(),
        responseHeader = model.response?.formattedHeaders.toString(),
        responseBody = model.response?.formattedBody.toString()
    )
}