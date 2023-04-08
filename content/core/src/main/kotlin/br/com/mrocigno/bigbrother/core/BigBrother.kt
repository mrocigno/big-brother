package br.com.mrocigno.bigbrother.core

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen

object BigBrother {

    internal val tasks = mutableListOf<BigBrotherTask>()

    fun watch(context: Application, isBubbleEnabled: Boolean = true) {
        if (!isBubbleEnabled) tasks.removeAll { it is BigBrotherWatchTask }
        AndroidThreeTen.init(context)
        context.registerActivityLifecycleCallbacks(BigBrotherObserver())
    }
}