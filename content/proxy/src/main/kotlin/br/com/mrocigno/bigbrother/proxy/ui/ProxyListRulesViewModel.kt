package br.com.mrocigno.bigbrother.proxy.ui

import androidx.lifecycle.ViewModel
import br.com.mrocigno.bigbrother.common.db.BigBrotherDatabase.Companion.bbdb
import br.com.mrocigno.bigbrother.proxy.model.ProxyRuleModel

internal class ProxyListRulesViewModel : ViewModel() {

    private val proxyDao = bbdb?.proxyDao()

    fun listRules(rulesId: LongArray?) =
        rulesId?.let { proxyDao?.getRules(it) } ?: proxyDao?.getAll()

    fun getRule(ruleId: Long) =
        proxyDao?.getRuleById(ruleId)?.let(::ProxyRuleModel)
}