package br.com.mrocigno.bigbrother.log

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import br.com.mrocigno.bigbrother.common.route.SESSION_ID_ARG
import br.com.mrocigno.bigbrother.core.OutOfDomain
import br.com.mrocigno.bigbrother.core.utils.bbSessionId

@OutOfDomain
class LogListActivity : AppCompatActivity(R.layout.bigbrother_activity_log_list) {

    private val toolbar: Toolbar by lazy { findViewById(R.id.log_list_toolbar) }
    private val sessionId: Long by lazy { intent.getLongExtra(SESSION_ID_ARG, bbSessionId) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
        toolbar.title = getString(R.string.log_list_title, sessionId)
        supportFragmentManager.beginTransaction()
            .add(R.id.log_list_container, LogFragment.newInstance(sessionId), "logs")
            .commit()
    }

}