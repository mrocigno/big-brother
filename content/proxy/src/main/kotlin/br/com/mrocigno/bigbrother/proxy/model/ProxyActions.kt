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
    val description: Int = R.string.bigbrother_proxy_set_header_description,
    val validate: (name: TextInputEditText, value: TextInputEditText, body: TextInputEditText) -> Boolean = { _, _, _ -> true }
) {
    EMPTY(
        label = R.string.bigbrother_proxy_empty_label,
        message = R.string.bigbrother_proxy_empty_message,
        group = -1,
        validate = { _, _, _ -> false }
    ),
    SET_BODY_REQUEST(
        label = R.string.bigbrother_proxy_set_body_label,
        message = R.string.bigbrother_proxy_set_body_message,
        group = R.id.proxy_action_body_layout,
        description = R.string.bigbrother_proxy_set_body_description,
        validate = { _, _, body ->
            val context = body.context
            val bodyText = body.text.toString().trim()
            val json = runCatching { JSONObject(bodyText) }
                .recoverCatching { JSONArray(bodyText) }
                .getOrElse { it.message }

            when {
                bodyText.isEmpty() -> { body.setInputLayoutError(context.getString(R.string.bigbrother_proxy_required_field)); false }
                json is String -> { body.setInputLayoutError(context.getString(R.string.bigbrother_proxy_json_invalid, json)); false }
                else -> true
            }
        }
    ),
    SET_BODY_RESPONSE(
        label = R.string.bigbrother_proxy_set_body_response_label,
        message = R.string.bigbrother_proxy_set_body_response_message,
        group = R.id.proxy_action_body_layout,
        description = R.string.bigbrother_proxy_set_body_response_description,
        validate = { _, _, body ->
            val context = body.context
            val bodyText = body.text.toString().trim()
            val json = runCatching { JSONObject(bodyText) }
                .recoverCatching { JSONArray(bodyText) }
                .getOrElse { it.message }

            when {
                bodyText.isEmpty() -> { body.setInputLayoutError(context.getString(R.string.bigbrother_proxy_required_field)); false }
                json is String -> { body.setInputLayoutError(context.getString(R.string.bigbrother_proxy_json_invalid, json)); false }
                else -> true
            }
        }
    ),
    SET_HEADER(
        label = R.string.bigbrother_proxy_set_header_label,
        message = R.string.bigbrother_proxy_set_header_message,
        description = R.string.bigbrother_proxy_set_header_description,
        group = R.id.proxy_action_name_value_group,
        validate = { name, value, _ ->
            val context = name.context
            val nameText = name.text.toString().trim()
            val valueText = value.text.toString().trim()

            when {
                nameText.isEmpty() -> { name.setInputLayoutError(context.getString(R.string.bigbrother_proxy_required_field)); false }
                nameText.contains(" ") -> { name.setInputLayoutError(context.getString(R.string.bigbrother_proxy_spaces_error)); false }
                valueText.isEmpty() -> { value.setInputLayoutError(context.getString(R.string.bigbrother_proxy_required_field)); false }
                else -> true
            }
        }
    ),
    SET_METHOD(
        label = R.string.bigbrother_proxy_set_method_label,
        message = R.string.bigbrother_proxy_set_method_message,
        group = R.id.proxy_action_value_layout,
        description = R.string.bigbrother_proxy_set_method_description,
        validate = { _, value, _ ->
            val context = value.context
            val valueText = value.text.toString().trim()

            when {
                valueText.isEmpty() -> { value.setInputLayoutError(context.getString(br.com.mrocigno.bigbrother.proxy.R.string.bigbrother_proxy_required_field)); false }
                else -> true
            }
        }
    ),
    SET_PATH(
        label = R.string.bigbrother_proxy_set_path_label,
        message = R.string.bigbrother_proxy_set_path_message,
        group = R.id.proxy_action_value_layout,
        description = R.string.bigbrother_proxy_set_path_description,
        validate = { _, value, _ ->
            val context = value.context
            val valueText = value.text.toString().trim()

            when {
                valueText.isEmpty() -> { value.setInputLayoutError(context.getString(R.string.bigbrother_proxy_required_field)); false }
                else -> true
            }
        }
    ),
    SET_QUERY(
        label = R.string.bigbrother_proxy_set_query_label,
        message = R.string.bigbrother_proxy_set_query_message,
        group = R.id.proxy_action_name_value_group,
        description = R.string.bigbrother_proxy_set_query_description,
        validate = { name, value, _ ->
            val context = name.context
            val nameText = name.text.toString().trim()
            val valueText = value.text.toString().trim()

            when {
                nameText.isEmpty() -> { name.setInputLayoutError(context.getString(R.string.bigbrother_proxy_required_field)); false }
                nameText.contains(" ") -> { name.setInputLayoutError(context.getString(R.string.bigbrother_proxy_spaces_error)); false }
                valueText.isEmpty() -> { value.setInputLayoutError(context.getString(R.string.bigbrother_proxy_required_field)); false }
                else -> true
            }
        }
    ),
    SET_RESPONSE_CODE(
        label = R.string.bigbrother_proxy_set_response_code_label,
        message = R.string.bigbrother_proxy_set_response_code_message,
        group = R.id.proxy_action_value_layout,
        description = R.string.bigbrother_proxy_set_response_code_description,
        validate = { name, value, _ ->
            val context = name.context
            val valueText = value.text.toString().trim()

            when {
                valueText.isEmpty() -> { value.setInputLayoutError(context.getString(R.string.bigbrother_proxy_required_field)); false }
                valueText.any { !it.isDigit() } -> { value.setInputLayoutError(context.getString(R.string.bigbrother_proxy_number_field)); false }
                else -> true
            }
        }
    ),
    REMOVE_HEADER(
        label = R.string.bigbrother_proxy_remove_header_label,
        message = R.string.bigbrother_proxy_remove_header_message,
        group = R.id.proxy_action_name_layout,
        description = R.string.bigbrother_proxy_remove_header_description,
        validate = { name, _, _ ->
            val context = name.context
            val nameText = name.text.toString().trim()

            when {
                nameText.isEmpty() -> { name.setInputLayoutError(context.getString(R.string.bigbrother_proxy_required_field)); false }
                nameText.contains(" ") -> { name.setInputLayoutError(context.getString(R.string.bigbrother_proxy_spaces_error)); false }
                else -> true
            }
        }
    ),
    REMOVE_QUERY(
        label = R.string.bigbrother_proxy_remove_query_label,
        message = R.string.bigbrother_proxy_remove_query_message,
        group = R.id.proxy_action_name_layout,
        description = R.string.bigbrother_proxy_remove_query_description,
        validate = { name, _, _ ->
            val context = name.context
            val nameText = name.text.toString().trim()

            when {
                nameText.isEmpty() -> { name.setInputLayoutError(context.getString(R.string.bigbrother_proxy_required_field)); false }
                nameText.contains(" ") -> { name.setInputLayoutError(context.getString(R.string.bigbrother_proxy_spaces_error)); false }
                else -> true
            }
        }
    )
}