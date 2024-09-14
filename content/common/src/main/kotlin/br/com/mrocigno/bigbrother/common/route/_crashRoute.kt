package br.com.mrocigno.bigbrother.common.route

import android.content.Context

fun Context.intentToCrash(
    sessionId: Long,
    animate: Boolean = true
) =
    intentAction("CRASH_ACTIVITY")
        .putExtra(SESSION_ID_ARG, sessionId)
        .putExtra(ANIMATE_ARG, animate)
