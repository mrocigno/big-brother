package br.com.mrocigno.bigbrother.common.route

import android.content.Context

const val PROXY_APPLIED_HEADER = "proxy_rules_applied"
const val PATH_ARG = "BigBrother.PATH"
const val METHOD_ARG = "BigBrother.METHOD"

fun Context.intentToProxyRule(method: String, path: String) =
    intentAction("CREATE_PROXY_RULE")
        .putExtra(METHOD_ARG, method)
        .putExtra(PATH_ARG, path)
