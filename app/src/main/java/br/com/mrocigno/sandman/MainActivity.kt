package br.com.mrocigno.sandman

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import br.com.mrocigno.sandman.core.Sandman
import br.com.mrocigno.sandman.matthew.Matthew.Companion.tag
import br.com.mrocigno.sandman.network.GithubApi
import br.com.mrocigno.sandman.network.NetworkConfig.retrofit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(R.layout.main_activity) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        findViewById<AppCompatButton>(R.id.test).setOnClickListener {
            Sandman.tag().d("Teste DEBUG")
            Sandman.tag().i("Teste INFO")
            tag().w("Teste WARN")
            Sandman.tag().e("Teste ERROR")
            Sandman.tag().v("Teste VERBOSE")

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val teste = retrofit.create(GithubApi::class.java).getRepos(0)
                    val i = 1
                } catch (e: Exception) {
                    Sandman.tag().e(e.stackTraceToString())
                }
            }

            startActivity(Intent(this, SecondActivity::class.java))
            finish()
        }
    }

}