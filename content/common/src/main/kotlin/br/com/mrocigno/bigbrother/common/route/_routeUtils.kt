package br.com.mrocigno.bigbrother.common.route

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import br.com.mrocigno.bigbrother.common.BuildConfig

const val SCREEN_NAME_ARG = "bigbrother.SCREEN_NAME_ARG"
const val STACKTRACE_ARG = "bigbrother.STACKTRACE_ARG"
const val ANIMATE_ARG = "bigbrother.ANIMATE_ARG"
const val SESSION_ID_ARG = "BigBrother.SESSION_ID"

fun Context.intentAction(name: String) =
    Intent("${packageName}.$name")
        .setPackage(packageName)

fun Context.checkIntent(intent: Intent) =
    packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY) != null

fun Context.intentToNetworkList(sessionId: Long) =
    intentAction("LIST_NETWORK_SESSION")
        .putExtra(SESSION_ID_ARG, sessionId)

fun Context.intentToLogList(sessionId: Long) =
    intentAction("LOG_LIST_ACTIVITY")
        .putExtra(SESSION_ID_ARG, sessionId)

fun Context.intentToCrash(
    screenName: String,
    sessionId: Long,
    stackTrace: String,
    animate: Boolean = true
) =
    intentAction("CRASH_ACTIVITY")
        .putExtra(SCREEN_NAME_ARG, screenName)
        .putExtra(STACKTRACE_ARG, stackTrace)
        .putExtra(SESSION_ID_ARG, sessionId)
        .putExtra(ANIMATE_ARG, animate)
