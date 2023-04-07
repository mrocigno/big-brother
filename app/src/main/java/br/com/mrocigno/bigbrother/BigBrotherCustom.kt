package br.com.mrocigno.bigbrother

import br.com.mrocigno.bigbrother.core.BigBrotherProvider
import br.com.mrocigno.bigbrother.log.addLogPage
import br.com.mrocigno.bigbrother.network.addNetworkPage

class BigBrotherCustom : BigBrotherProvider() {

    override val isEnabled = true

    override fun setupPages() {
        addNetworkPage()
        addLogPage()
    }
}