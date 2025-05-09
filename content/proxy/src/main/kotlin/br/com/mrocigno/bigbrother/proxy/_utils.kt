package br.com.mrocigno.bigbrother.proxy

import android.content.Context
import br.com.mrocigno.bigbrother.core.BigBrother
import br.com.mrocigno.bigbrother.core.BigBrotherProvider
import br.com.mrocigno.bigbrother.proxy.ui.ProxyFragment

fun BigBrotherProvider.addProxyPage(name: String = "Proxy") {
    addInterceptor(ProxyInterceptor())
    addPage(name) { ProxyFragment() }
}

fun BigBrother.addProxyPage(name: String = "Proxy") {
    addInterceptor(ProxyInterceptor())
    addPage(name) { ProxyFragment() }
}

internal fun Context.randomName(): String =
    resources.getStringArray(R.array.bigbrother_names).random()
