package br.com.mrocigno.bigbrother.report

import br.com.mrocigno.bigbrother.core.BigBrother
import br.com.mrocigno.bigbrother.core.BigBrotherProvider
import br.com.mrocigno.bigbrother.report.ui.SessionFragment

fun BigBrotherProvider.addSessionPage(customName: String = "Session") {
    addPage(customName) { SessionFragment() }
}

fun BigBrother.addSessionPage(customName: String = "Session") {
    addPage(customName) { SessionFragment() }
}