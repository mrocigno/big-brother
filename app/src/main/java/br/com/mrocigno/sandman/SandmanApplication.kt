package br.com.mrocigno.sandman

import android.app.Application
import com.google.firebase.FirebaseApp

class SandmanApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        FirebaseApp.initializeApp(this)

    }
}