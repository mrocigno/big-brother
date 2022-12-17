package br.com.mrocigno.sandman.json

import org.json.JSONArray
import org.json.JSONObject
import java.lang.StringBuilder

fun JSONObject.toListModel(lvl: Int): List<JsonViewerModel> {
    val result = mutableListOf<JsonViewerModel>()

    for (key in keys()) {
        val value = this[key]
        result.add(JsonViewerModel(
            key = key,
            value = value.toString(),
            lvl = lvl,
            children = when (value) {
                is JSONObject -> value.toListModel(lvl + 1)
                is JSONArray -> value.toListModel(lvl + 1)
                else -> null
            }
        ))
    }

    return result
}

fun JSONArray.toListModel(lvl: Int): List<JsonViewerModel> {
    val result = mutableListOf<JsonViewerModel>()

    for (index in 0 until length()) {
        val value = this[index]
        result.add(JsonViewerModel(
            key = index.toString(),
            value = value.toString(),
            lvl = lvl,
            children = when (value) {
                is JSONObject -> value.toListModel(lvl + 1)
                is JSONArray -> value.toListModel(lvl + 1)
                else -> null
            }
        ))
    }

    return result
}

class JsonViewerModel(
    val lvl: Int,
    val key: String,
    val value: String?,
    val children: List<JsonViewerModel>? = null,
    var expanded: Boolean = false
) {

    override fun toString() = StringBuilder()
        .appendLine(key)
        .appendLine(value)
        .toString()
}