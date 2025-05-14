package br.com.mrocigno.bigbrother.network.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import br.com.mrocigno.bigbrother.common.route.SESSION_ID_ARG
import br.com.mrocigno.bigbrother.common.utils.bbSessionId
import br.com.mrocigno.bigbrother.core.OutOfDomain
import br.com.mrocigno.bigbrother.network.R

@OutOfDomain
internal class NetworkListActivity : AppCompatActivity(R.layout.bigbrother_activity_network_list) {

    private val toolbar: Toolbar by lazy { findViewById(R.id.net_list_toolbar) }
    private val sessionId: Long by lazy { intent.getLongExtra(SESSION_ID_ARG, bbSessionId) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
        toolbar.title = getString(R.string.network_list_title, sessionId)
        supportFragmentManager.beginTransaction()
            .add(R.id.net_list_container, NetworkFragment.newInstance(sessionId), "network")
            .commit()
    }
}