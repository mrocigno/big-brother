package br.com.mrocigno.bigbrother.proxy.model

import br.com.mrocigno.bigbrother.common.utils.setInputLayoutError
import br.com.mrocigno.bigbrother.proxy.R
import com.google.android.material.textfield.TextInputEditText
import org.json.JSONArray
import org.json.JSONObject

internal enum class ProxyActions(
    val label: Int = br.com.mrocigno.bigbrother.common.R.string.close,
    val message: Int = br.com.mrocigno.bigbrother.common.R.string.close,
    val group: Int = R.id.proxy_action_name_value_group,
    val description: Int = R.string.proxy_set_header_description,
    val validate: (name: TextInputEditText, value: TextInputEditText, body: TextInputEditText) -> Boolean = { _, _, _ -> true }
) {
    EMPTY(
        label = R.string.proxy_empty_label,
        message = R.string.proxy_empty_message,
        group = -1,
        validate = { _, _, _ -> false }
    ),
    SET_BODY(
        label = R.string.proxy_set_body_label,
        message = R.string.proxy_set_body_message,
        group = R.id.proxy_action_body_layout,
        description = R.string.proxy_set_body_description,
        validate = { _, _, body ->
            val bodyText = body.text.toString().trim()
            val json = runCatching { JSONObject(bodyText) }
                .recoverCatching { JSONArray(bodyText) }
                .getOrElse { it.message }

            when {
                bodyText.isEmpty() -> { body.setInputLayoutError("Campo vazio"); false }
                json is String -> { body.setInputLayoutError("Formato inválido\n$json"); false }
                else -> true
            }
        }
    ),
    SET_HEADER(
        label = R.string.proxy_set_header_label,
        message = R.string.proxy_set_header_message,
        description = R.string.proxy_set_header_description,
        group = R.id.proxy_action_name_value_group
    ),
    SET_PATH(
        label = R.string.proxy_set_path_label,
        message = R.string.proxy_set_path_message,
        group = R.id.proxy_action_value_layout,
        description = R.string.proxy_set_path_description
    ),
    SET_QUERY(
        label = R.string.proxy_set_query_label,
        message = R.string.proxy_set_query_message,
        group = R.id.proxy_action_name_value_group
    ),
    REMOVE_HEADER(
        label = R.string.proxy_remove_header_label,
        message = R.string.proxy_remove_header_message,
        group = R.id.proxy_action_name_layout
    ),
    REMOVE_QUERY(
        label = R.string.proxy_remove_query_label,
        message = R.string.proxy_remove_query_message,
        group = R.id.proxy_action_name_layout
    )
}