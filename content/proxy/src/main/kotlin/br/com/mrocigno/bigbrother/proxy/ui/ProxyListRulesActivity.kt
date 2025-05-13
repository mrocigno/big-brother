package br.com.mrocigno.bigbrother.proxy.ui

import android.os.Bundle
import androidx.activity.viewModels
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

    private val viewModel: ProxyListRulesViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val rules = rulesId
        when {
            rules == null || rules.isEmpty() -> finish()
            rules.size == 1 -> openRule()
            else -> setupFragment()
        }
    }

    private fun setupFragment() {
        toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
        supportFragmentManager.commit {
            add(R.id.proxy_list_rules_container, ProxyListRulesFragment.newInstance(rulesId), "proxy")
        }
    }

    private fun openRule() {
        viewModel.getRule(rulesId?.first() ?: return)
            ?.let { ProxyCreateRuleActivity.intent(this@ProxyListRulesActivity, it) }
            ?.run(::startActivity)
        finish()
    }
}