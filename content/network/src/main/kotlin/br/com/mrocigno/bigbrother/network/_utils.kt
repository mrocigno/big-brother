package br.com.mrocigno.bigbrother.network

import android.content.res.ColorStateList
import android.util.Base64
import android.view.View
import br.com.mrocigno.bigbrother.common.R
import br.com.mrocigno.bigbrother.core.BigBrother
import br.com.mrocigno.bigbrother.core.BigBrotherProvider
import br.com.mrocigno.bigbrother.core.utils.addBigBrotherInterceptor
import br.com.mrocigno.bigbrother.network.ui.NetworkFragment
import okhttp3.OkHttpClient
import okio.Buffer

internal fun View.byStatusCode(statusCode: Int?) {
    val containerColor = when (statusCode) {
        null -> R.color.bb_text_highlight
        in 200..399 -> R.color.bb_icon_positive
        else -> R.color.bb_icon_negative
    }
    backgroundTintList = ColorStateList.valueOf(context.getColor(containerColor))
}

internal fun Buffer.toBase64(): String =
    Base64.encodeToString(readByteArray(), Base64.NO_WRAP)

fun BigBrotherProvider.addNetworkPage(customName: String = "Network") {
    addInterceptor(NetworkEntryInterceptor())
    addPage(customName) { NetworkFragment() }
}

fun BigBrother.addNetworkPage(customName: String = "Network") {
    addInterceptor(NetworkEntryInterceptor())
    addPage(customName) { NetworkFragment() }
}

@Deprecated("Use addBigBrotherInterceptor instead")
fun OkHttpClient.Builder.bigBrotherIntercept(vararg blockList: String) =
    addBigBrotherInterceptor(*blockList)