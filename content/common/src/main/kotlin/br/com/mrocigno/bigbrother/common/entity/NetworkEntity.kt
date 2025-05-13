package br.com.mrocigno.bigbrother.common.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import br.com.mrocigno.bigbrother.common.utils.bbSessionId

@Entity(tableName = "tblNetwork")
class NetworkEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") val id: Long = 0,
    @ColumnInfo(name = "session_id") val sessionId: Long = bbSessionId,
    @ColumnInfo(name = "full_url") val fullUrl: String,
    @ColumnInfo(name = "url") val url: String,
    @ColumnInfo(name = "status_code") var statusCode: Int? = null,
    @ColumnInfo(name = "elapsed_time") var elapsedTime: String? = null,
    @ColumnInfo(name = "hour") val hour: String,
    @ColumnInfo(name = "method") val method: String,
    @ColumnInfo(name = "proxy_rules") val proxyRules: String? = null,
    @ColumnInfo(name = "request_header") val requestHeader: String? = null,
    @ColumnInfo(name = "request_body") val requestBody: String? = null,
    @ColumnInfo(name = "response_header") val responseHeader: String? = null,
    @ColumnInfo(name = "response_body") val responseBody: String? = null
)