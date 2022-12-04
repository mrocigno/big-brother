package br.com.mrocigno.sandman

class TheDreamingCustom : TheDreamingProvider() {

    override val themeRes = R.style.CustomTheDreaming

    override fun onCreate(): Boolean {
        TheDreaming.addNightmare("Teste") {
            ToolsFragment()
        }
        return super.onCreate()
    }
}