package br.com.mrocigno.bigbrother.common.route

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager

fun Context.intentAction(name: String) =
    Intent("br.com.mrocigno.bigbrother.$name")

fun Context.checkIntent(intent: Intent) =
    packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY) != null