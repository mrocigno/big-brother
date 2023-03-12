package br.com.mrocigno.sandman

import android.app.Application
import android.view.ViewGroup
import br.com.mrocigno.sandman.log.SandmanLog

object Sandman {

    private var onVortexKilled: (() -> Unit)? = null
    var isVortexAlive = true
    var isLoggable = true

    fun init(context: Application, onVortexKilled: (() -> Unit)? = null) {
        context.registerActivityLifecycleCallbacks(Morpheus())
        this.onVortexKilled = onVortexKilled
    }

    fun killVortex(vortex: VortexView?) {
        isVortexAlive = false
        (vortex?.parent as? ViewGroup)?.removeView(vortex)
        onVortexKilled?.invoke()
        onVortexKilled = null
    }

    fun tag(tag: String = "SANDMAN") = SandmanLog(tag)
}