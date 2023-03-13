package br.com.mrocigno.sandman

import br.com.mrocigno.sandman.log.LogFragment
import br.com.mrocigno.sandman.network.NetworkFragment
import br.com.mrocigno.sandman.vortex.TheDreaming
import br.com.mrocigno.sandman.vortex.TheDreamingProvider

class TheDreamingCustom : TheDreamingProvider() {

    override fun onCreate(): Boolean {
        TheDreaming.addNightmare("Teste") {
            NetworkFragment()
        }
        TheDreaming.addNightmare("Teste2") {
            LogFragment()
        }
        return super.onCreate()
    }
}