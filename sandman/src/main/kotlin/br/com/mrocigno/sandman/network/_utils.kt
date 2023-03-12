package br.com.mrocigno.sandman.network

import android.content.res.ColorStateList
import android.view.View
import br.com.mrocigno.sandman.R

internal fun View.byStatusCode(statusCode: Int?) {
    val containerColor = when (statusCode) {
        null -> R.color.text_highlight
        in 200..399 -> R.color.icon_positive
        else -> R.color.icon_negative
    }
    backgroundTintList = ColorStateList.valueOf(context.getColor(containerColor))
}