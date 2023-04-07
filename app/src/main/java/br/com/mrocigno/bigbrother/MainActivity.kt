package br.com.mrocigno.bigbrother

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import br.com.mrocigno.bigbrother.core.BigBrother
import br.com.mrocigno.bigbrother.log.BBLog.Companion.tag
import br.com.mrocigno.bigbrother.network.GithubApi
import br.com.mrocigno.bigbrother.network.NetworkConfig.retrofit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(br.com.mrocigno.bigbrother.R.layout.main_activity) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        findViewById<AppCompatButton>(br.com.mrocigno.bigbrother.R.id.test).setOnClickListener {
            BigBrother.tag().d("Teste DEBUG")
            BigBrother.tag().i("Teste INFO")
            tag().w("Teste WARN")
            BigBrother.tag().e("Teste ERROR")
            BigBrother.tag().v("Teste VERBOSE")

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val teste = retrofit.create(GithubApi::class.java).getRepos(0)
                    val i = 1
                } catch (e: Exception) {
                    BigBrother.tag().e(e.stackTraceToString())
                }
            }

            startActivity(Intent(this, br.com.mrocigno.bigbrother.SecondActivity::class.java))
            finish()
        }
    }

}