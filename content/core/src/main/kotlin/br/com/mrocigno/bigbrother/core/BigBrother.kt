package br.com.mrocigno.bigbrother.core

import android.app.Application

object BigBrother {

    internal val tasks = mutableListOf<BigBrotherTask>()

    fun watch(context: Application) {
        context.registerActivityLifecycleCallbacks(BigBrotherObserver())
    }
}