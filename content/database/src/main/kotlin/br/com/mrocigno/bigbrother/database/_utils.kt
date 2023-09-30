package br.com.mrocigno.bigbrother.database

import br.com.mrocigno.bigbrother.core.BigBrotherProvider
import br.com.mrocigno.bigbrother.database.ui.DatabaseFragment

fun BigBrotherProvider.addDatabasePage(customName: String = "Database") {
    addPage(customName) { DatabaseFragment() }
}