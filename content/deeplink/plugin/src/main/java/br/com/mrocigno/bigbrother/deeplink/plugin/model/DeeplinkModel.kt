package br.com.mrocigno.bigbrother.deeplink.plugin.model

import kotlinx.serialization.Serializable

@Serializable
internal data class DeeplinkModel(
    val activityName: String,
    val exported: Boolean = false,
    val links: List<DeeplinkFilterModel>
) {

    val type: String = if (exported) "EXTERNAL" else "INTERNAL"
}

@Serializable
internal data class DeeplinkFilterModel(
    var scheme: String = "",
    var host: String = "",
    var path: String = "",
    var hasView: Boolean = false,
    var hasBrowsable: Boolean = false
)
