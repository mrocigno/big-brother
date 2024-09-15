package br.com.mrocigno.bigbrother.database

import br.com.mrocigno.bigbrother.core.BigBrother
import br.com.mrocigno.bigbrother.core.BigBrotherProvider
import br.com.mrocigno.bigbrother.database.ui.DatabaseFragment

fun BigBrotherProvider.addDatabasePage(customName: String = "Database") {
    addPage(customName) { DatabaseFragment() }
}

fun BigBrother.addDatabasePage(customName: String = "Database") {
    addPage(customName) { DatabaseFragment() }
}