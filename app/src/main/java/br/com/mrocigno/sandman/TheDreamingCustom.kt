package br.com.mrocigno.sandman

import br.com.mrocigno.sandman.core.TheDreamingProvider
import br.com.mrocigno.sandman.corinthian.addCorinthian
import br.com.mrocigno.sandman.matthew.addMatthew

class TheDreamingCustom : TheDreamingProvider() {

    override val isEnabled = true

    override fun setupTheDreaming() {
        addCorinthian()
        addMatthew()
    }
}