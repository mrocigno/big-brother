package br.com.mrocigno.sandman

import br.com.mrocigno.sandman.network.NetworkFragment

class TheDreamingCustom : TheDreamingProvider() {

    override fun onCreate(): Boolean {
        TheDreaming.addNightmare("Teste") {
            NetworkFragment()
        }
        return super.onCreate()
    }
}