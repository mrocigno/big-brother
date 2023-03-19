package br.com.mrocigno.sandman.core

import android.app.Application

object Sandman {

    internal val tasks = mutableListOf<MorpheusTask>()

    fun init(context: Application) {
        context.registerActivityLifecycleCallbacks(Morpheus())
    }
}