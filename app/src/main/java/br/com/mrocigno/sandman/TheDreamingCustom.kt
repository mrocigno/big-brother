package br.com.mrocigno.sandman

import br.com.mrocigno.sandman.network.NetworkFragment

class TheDreamingCustom : TheDreamingProvider() {

    override val themeRes = R.style.CustomTheDreaming

    override fun onCreate(): Boolean {
        TheDreaming.addNightmare("Teste") {
            NetworkFragment()
        }
        return super.onCreate()
    }
}