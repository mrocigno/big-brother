package br.com.mrocigno.bigbrother

import android.app.Application
import br.com.mrocigno.bigbrother.core.BigBrother

class SampleApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        BigBrother.watch(this, isBubbleEnabled = true)
    }
}
