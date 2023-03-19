package br.com.mrocigno.sandman.corinthian

import android.content.res.ColorStateList
import android.view.View
import br.com.mrocigno.sandman.common.R
import br.com.mrocigno.sandman.core.TheDreamingProvider
import okhttp3.OkHttpClient

internal fun View.byStatusCode(statusCode: Int?) {
    val containerColor = when (statusCode) {
        null -> R.color.text_highlight
        in 200..399 -> R.color.icon_positive
        else -> R.color.icon_negative
    }
    backgroundTintList = ColorStateList.valueOf(context.getColor(containerColor))
}

fun TheDreamingProvider.addCorinthian(customName: String = "Corinthian") {
    addNightmare(customName) { NetworkFragment() }
}

fun OkHttpClient.Builder.corinthian() =
    addInterceptor(CorinthianInterceptor())