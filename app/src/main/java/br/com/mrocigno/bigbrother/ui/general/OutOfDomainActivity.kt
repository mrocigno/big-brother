package br.com.mrocigno.bigbrother.ui.general

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import br.com.mrocigno.bigbrother.R
import br.com.mrocigno.bigbrother.core.OutOfDomain

@OutOfDomain
class OutOfDomainActivity : AppCompatActivity(R.layout.out_of_domain_activity) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        findViewById<Toolbar>(R.id.toolbar).setNavigationOnClickListener { finish() }
    }
}