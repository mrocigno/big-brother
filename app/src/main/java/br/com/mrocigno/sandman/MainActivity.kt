package br.com.mrocigno.sandman

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.lifecycleScope
import br.com.mrocigno.sandman.network.GithubApi
import br.com.mrocigno.sandman.network.NetworkConfig.retrofit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MainActivity : AppCompatActivity(R.layout.main_activity) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        findViewById<AppCompatButton>(R.id.test).setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {

                try {
                    val teste = retrofit.create(GithubApi::class.java).getRepos(0)
                    val i = 1
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

}