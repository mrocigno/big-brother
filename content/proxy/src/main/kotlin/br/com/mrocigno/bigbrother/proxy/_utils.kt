package br.com.mrocigno.bigbrother.proxy

import android.content.Context
import br.com.mrocigno.bigbrother.core.BigBrother
import br.com.mrocigno.bigbrother.core.BigBrotherProvider
import br.com.mrocigno.bigbrother.proxy.ui.ProxyListRulesFragment

fun BigBrotherProvider.addProxyPage(name: String = "Proxy") {
    addInterceptor { ProxyInterceptor() }
    addPage(name) { ProxyListRulesFragment() }
}

fun BigBrother.addProxyPage(name: String = "Proxy") {
    addInterceptor { ProxyInterceptor() }
    addPage(name) { ProxyListRulesFragment() }
}

internal fun Context.randomName(): String =
    resources.getStringArray(R.array.bigbrother_names).random()
