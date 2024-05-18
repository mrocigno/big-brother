package br.com.mrocigno.bigbrother.log

import br.com.mrocigno.bigbrother.core.BigBrother
import br.com.mrocigno.bigbrother.core.BigBrotherProvider

fun BigBrotherProvider.addLogPage(customName: String = "Log") {
    addPage(customName) { LogFragment() }
}

fun BigBrother.addLogPage(customName: String = "Log") {
    addPage(customName) { LogFragment() }
}