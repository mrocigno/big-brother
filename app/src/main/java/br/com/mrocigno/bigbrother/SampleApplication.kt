package br.com.mrocigno.bigbrother

import android.app.Application
import br.com.mrocigno.bigbrother.core.BigBrother
import br.com.mrocigno.bigbrother.log.BigBrotherLogTree
import timber.log.Timber
import timber.log.Timber.DebugTree

class SampleApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        BigBrother.config {
            isClickRecorderEnabled = true
        }.watch(this, isBubbleEnabled = true)

        Timber.plant(DebugTree())
        Timber.plant(BigBrotherLogTree())
    }
}
