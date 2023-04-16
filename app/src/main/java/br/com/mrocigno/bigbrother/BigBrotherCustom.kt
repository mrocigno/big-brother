package br.com.mrocigno.bigbrother

import br.com.mrocigno.bigbrother.core.BigBrotherProvider
import br.com.mrocigno.bigbrother.log.addLogPage
import br.com.mrocigno.bigbrother.network.addNetworkPage
import br.com.mrocigno.bigbrother.ui.general.CustomPageActivity

class BigBrotherCustom : BigBrotherProvider() {

    override val isEnabled = true

    override fun setupPages() {
        addNetworkPage()
        addLogPage()
        addPage(CustomPageActivity::class) {
            page("custom name") {
                ToolsFragment()
            }
        }
    }
}