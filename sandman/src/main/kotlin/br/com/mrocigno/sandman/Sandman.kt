package br.com.mrocigno.sandman

import android.app.Application
import br.com.mrocigno.sandman.morpheus.Morpheus

object Sandman {

    internal val dataLake = mutableMapOf<String, Any>()

    fun init(context: Application) {
        context.registerActivityLifecycleCallbacks(Morpheus())
    }
}