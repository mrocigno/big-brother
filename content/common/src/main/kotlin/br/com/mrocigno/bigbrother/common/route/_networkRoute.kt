package br.com.mrocigno.bigbrother.common.route

import android.content.Context

fun Context.intentToNetworkList(sessionId: Long) =
    intentAction("LIST_NETWORK_SESSION")
        .putExtra(SESSION_ID_ARG, sessionId)