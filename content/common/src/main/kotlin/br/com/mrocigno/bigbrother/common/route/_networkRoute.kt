package br.com.mrocigno.bigbrother.common.route

import android.content.Context

const val SESSION_ID_ARG = "BigBrother.NetworkListActivity.SESSION_ID"

fun Context.intentToNetworkList(sessionId: Long) =
    intentAction("LIST_NETWORK_SESSION")
        .putExtra(SESSION_ID_ARG, sessionId)