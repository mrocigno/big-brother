package br.com.mrocigno.bigbrother.network.json

import androidx.recyclerview.widget.DiffUtil
import org.json.JSONArray
import org.json.JSONObject

fun JSONObject.toListModel(lvl: Int): List<JsonViewerModel> {
    val result = mutableListOf<JsonViewerModel>()

    for (key in keys()) {
        val value = this[key]
        val type = value::class.simpleName
        val children = when (value) {
            is JSONObject -> value.toListModel(lvl + 1)
            is JSONArray -> value.toListModel(lvl + 1)
            else -> null
        }
        result.add(
            JsonViewerModel(
            key = key,
            value = value.toString(),
            lvl = lvl,
            children = children,
            type = type.toString()
        )
        )
    }

    return result
}

fun JSONArray.toListModel(lvl: Int): List<JsonViewerModel> {
    val result = mutableListOf<JsonViewerModel>()

    for (index in 0 until length()) {
        val value = this[index]
        val type = value::class.simpleName
        val children = when (value) {
            is JSONObject -> value.toListModel(lvl + 1)
            is JSONArray -> value.toListModel(lvl + 1)
            else -> null
        }

        result.add(
            JsonViewerModel(
            key = index.toString(),
            value = value.toString(),
            lvl = lvl,
            children = children,
            type = type.toString()
        )
        )
    }

    return result
}

class JsonViewerModel(
    val lvl: Int,
    val key: String,
    val value: String?,
    val children: List<JsonViewerModel>? = null,
    var expanded: Boolean = false,
    val type: String
) {

    override fun toString() = StringBuilder()
        .appendLine(key)
        .appendLine(value)
        .toString()

    class Differ : DiffUtil.ItemCallback<JsonViewerModel>() {

        override fun areItemsTheSame(oldItem: JsonViewerModel, newItem: JsonViewerModel) =
            oldItem.lvl == newItem.lvl
                && oldItem.key == newItem.key
                && oldItem.value == newItem.value

        override fun areContentsTheSame(
            oldItem: JsonViewerModel,
            newItem: JsonViewerModel
        ) = false
    }
}