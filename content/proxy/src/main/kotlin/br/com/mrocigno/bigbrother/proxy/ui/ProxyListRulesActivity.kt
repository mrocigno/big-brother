package br.com.mrocigno.bigbrother.proxy.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.commit
import br.com.mrocigno.bigbrother.common.provider.id
import br.com.mrocigno.bigbrother.common.route.RULES_ARG
import br.com.mrocigno.bigbrother.core.OutOfDomain
import br.com.mrocigno.bigbrother.proxy.R

@OutOfDomain
internal class ProxyListRulesActivity : AppCompatActivity(R.layout.bigbrother_activity_list_rules) {

    private val toolbar: Toolbar by id(R.id.proxy_list_rules_toolbar)
    private val rulesId: LongArray? by lazy { intent.getLongArrayExtra(RULES_ARG) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupFragment()
    }

    private fun setupFragment() {
        toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
        supportFragmentManager.commit {
            add(R.id.proxy_list_rules_container, ProxyListRulesFragment.newInstance(rulesId), "proxy")
        }
    }
}