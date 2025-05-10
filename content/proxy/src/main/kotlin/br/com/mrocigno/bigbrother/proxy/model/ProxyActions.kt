package br.com.mrocigno.bigbrother.proxy.model

import br.com.mrocigno.bigbrother.proxy.R

internal enum class ProxyActions(
    val label: Int = br.com.mrocigno.bigbrother.common.R.string.close,
    val message: Int = br.com.mrocigno.bigbrother.common.R.string.close,
    val group: Int = R.id.proxy_action_name_value_group
) {
    EMPTY(
        label = R.string.proxy_empty_label,
        message = R.string.proxy_empty_message,
        group = -1
    ),
    SET_BODY(
        label = R.string.proxy_set_body_label,
        message = R.string.proxy_set_body_message,
        group = R.id.proxy_action_body_layout
    ),
    SET_HEADER(
        label = R.string.proxy_set_header_label,
        message = R.string.proxy_set_header_message,
        group = R.id.proxy_action_name_value_group
    ),
    SET_PATH(
        label = R.string.proxy_set_path_label,
        message = R.string.proxy_set_path_message,
        group = R.id.proxy_action_value_layout
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