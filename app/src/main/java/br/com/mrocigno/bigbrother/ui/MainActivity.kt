package br.com.mrocigno.bigbrother.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import br.com.mrocigno.bigbrother.R
import br.com.mrocigno.bigbrother.ui.network.NetworkActivity

class MainActivity : AppCompatActivity(R.layout.main_activity) {

    private val networkButton: AppCompatButton by lazy { findViewById(R.id.open_network_button) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        networkButton.setOnClickListener {
            startActivity(Intent(this, NetworkActivity::class.java))
        }
    }
}