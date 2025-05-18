package br.com.mrocigno.bigbrother.common.route

import android.content.Context

const val PROXY_APPLIED_HEADER = "proxy_rules_applied"
const val PATH_ARG = "BigBrother.PATH"
const val METHOD_ARG = "BigBrother.METHOD"
const val RULES_ARG = "BigBrother.RULES_ARG"

fun Context.intentToProxyCreateRule(method: String, path: String) =
    intentAction("CREATE_PROXY_RULE")
        .putExtra(METHOD_ARG, method)
        .putExtra(PATH_ARG, path)

fun Context.intentToProxyListRules(rulesId: LongArray?) =
    intentAction("LIST_PROXY_RULES")
        .putExtra(RULES_ARG, rulesId)
