package br.com.mrocigno.sandman

import android.app.Application
import br.com.mrocigno.sandman.morpheus.Morpheus

object Sandman {

    fun init(context: Application) {
        context.registerActivityLifecycleCallbacks(Morpheus())
    }
}