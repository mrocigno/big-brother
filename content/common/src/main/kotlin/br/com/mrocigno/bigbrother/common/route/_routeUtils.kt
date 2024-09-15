package br.com.mrocigno.bigbrother.common.route

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager

const val ANIMATE_ARG = "bigbrother.ANIMATE_ARG"
const val SESSION_ID_ARG = "BigBrother.SESSION_ID"

fun Context.intentAction(name: String) =
    Intent("${packageName}.$name")
        .setPackage(packageName)

fun Context.checkIntent(intent: Intent) =
    packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY) != null

fun Context.intentToLogList(sessionId: Long) =
    intentAction("LOG_LIST_ACTIVITY")
        .putExtra(SESSION_ID_ARG, sessionId)
