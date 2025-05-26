package br.com.mrocigno.bigbrother.deeplink

import android.content.Context
import br.com.mrocigno.bigbrother.core.BigBrother
import br.com.mrocigno.bigbrother.core.BigBrotherProvider
import br.com.mrocigno.bigbrother.deeplink.ui.DeeplinkFragment

const val BIG_BROTHER_DEEPLINK_FILENAME = "bb_deeplinks.json"

fun BigBrother.addDeeplinkPage(name: String = "Deeplink") =
    addPage(name) { DeeplinkFragment() }

fun BigBrotherProvider.addDeeplinkPage(name: String = "Deeplink") =
    addPage(name) { DeeplinkFragment() }

internal fun validateQuery(context: Context, queryString: String): String? = buildString {
    val queries = queryString.split("&")
    for (query in queries) {
        if (!query.contains("=")) {
            appendLine(context.getString(R.string.bigbrother_deeplink_query_no_value_error, query))
            continue
        }

        val key = query.substringBefore("=")
        val value = query.substringAfter("=")
        if (key.isBlank()) {
            appendLine(context.getString(R.string.bigbrother_deeplink_query_invalid_error, key, value))
        }
    }
}.takeIf { it.isNotBlank() }
