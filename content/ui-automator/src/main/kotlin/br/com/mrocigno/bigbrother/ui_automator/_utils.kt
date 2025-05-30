package br.com.mrocigno.bigbrother.ui_automator

import br.com.mrocigno.bigbrother.core.BigBrother
import br.com.mrocigno.bigbrother.core.BigBrotherProvider
import br.com.mrocigno.bigbrother.ui_automator.ui.UiAutomatorFragment

fun BigBrotherProvider.addUiAutomatorPage(customName: String = "UiAutomator") {
    addPage(customName) { UiAutomatorFragment() }
}

fun BigBrother.addUiAutomatorPage(customName: String = "UiAutomator") {
    addPage(customName) { UiAutomatorFragment() }
}

