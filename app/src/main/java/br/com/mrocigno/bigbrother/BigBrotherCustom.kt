package br.com.mrocigno.bigbrother

import br.com.mrocigno.bigbrother.core.BigBrotherProvider
import br.com.mrocigno.bigbrother.database.addDatabasePage
import br.com.mrocigno.bigbrother.deeplink.addDeeplinkPage
import br.com.mrocigno.bigbrother.log.addLogPage
import br.com.mrocigno.bigbrother.network.addNetworkPage
import br.com.mrocigno.bigbrother.proxy.addProxyPage
import br.com.mrocigno.bigbrother.report.addSessionPage
import br.com.mrocigno.bigbrother.ui.general.CustomPageActivity
import br.com.mrocigno.bigbrother.ui_automator.addUiAutomatorPage

class BigBrotherCustom : BigBrotherProvider() {

    override val isEnabled = true

    override fun setupPages() {
        addUiAutomatorPage()
        addNetworkPage()
        addProxyPage()
        addDeeplinkPage()
        addDatabasePage()
        addLogPage()
        addSessionPage()
        addPage(CustomPageActivity::class) {
            page("custom name") {
                ToolsFragment()
            }
        }
    }
}

