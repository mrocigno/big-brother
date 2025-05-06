package br.com.mrocigno.bigbrother.network

import android.content.res.ColorStateList
import android.view.View
import br.com.mrocigno.bigbrother.common.R
import br.com.mrocigno.bigbrother.core.BigBrother
import br.com.mrocigno.bigbrother.core.BigBrotherProvider
import br.com.mrocigno.bigbrother.network.ui.NetworkFragment
import okhttp3.OkHttpClient

internal fun View.byStatusCode(statusCode: Int?) {
    val containerColor = when (statusCode) {
        null -> R.color.bb_text_highlight
        in 200..399 -> R.color.bb_icon_positive
        else -> R.color.bb_icon_negative
    }
    backgroundTintList = ColorStateList.valueOf(context.getColor(containerColor))
}

fun BigBrotherProvider.addNetworkPage(customName: String = "Network") {
    addPage(customName) { NetworkFragment() }
}

fun BigBrother.addNetworkPage(customName: String = "Network") {
    addPage(customName) { NetworkFragment() }
}

fun OkHttpClient.Builder.bigBrotherIntercept(vararg blockList: String) =
    addInterceptor(BigBrotherInterceptor(*blockList))