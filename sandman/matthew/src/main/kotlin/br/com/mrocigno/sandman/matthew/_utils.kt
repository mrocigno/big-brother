package br.com.mrocigno.sandman.matthew

import br.com.mrocigno.sandman.core.TheDreamingProvider

fun TheDreamingProvider.addMatthew(customName: String = "Matthew") {
    addNightmare(customName) { LogFragment() }
}