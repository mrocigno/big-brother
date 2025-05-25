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
    val actions: List<String> = emptyList(),
    val categories: List<String> = emptyList()
) {
    fun getFullPath(): String = buildString {
        append(scheme)
        if (scheme.isNotBlank()) append("://")
        append(host)
        if (path.isNotEmpty()) {
            append("/")
            append(path)
        }
    }
}
