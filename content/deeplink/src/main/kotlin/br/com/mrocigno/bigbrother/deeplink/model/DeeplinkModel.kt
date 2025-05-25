package br.com.mrocigno.bigbrother.deeplink.model

import kotlinx.serialization.Serializable

@Serializable
internal data class DeeplinkModel(
    val activityName: String,
    val exported: Boolean,
    val type: DeeplinkType,
    val links: List<DeeplinkInfo>
)

@Serializable
internal data class DeeplinkInfo(
    val scheme: String,
    val host: String,
    val path: String,
    val hasView: Boolean,
    val hasBrowsable: Boolean
) {
    fun getFullPath(): String = buildString {
        append(scheme)
        append("://")
        append(host)
        if (path.isNotEmpty()) {
            append("/")
            append(path)
        }
    }
}
