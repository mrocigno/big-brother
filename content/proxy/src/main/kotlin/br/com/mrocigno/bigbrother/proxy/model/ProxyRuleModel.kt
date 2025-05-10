package br.com.mrocigno.bigbrother.proxy.model

import br.com.mrocigno.bigbrother.common.entity.ProxyRuleWithActions
import okhttp3.Request

internal data class ProxyRuleModel(
    val ruleName: String = "",
    val pathCondition: String = "",
    val headerCondition: String = "",
    val actions: List<ProxyActionModel> = emptyList()
) {

    constructor(entity: ProxyRuleWithActions) : this(
        ruleName = entity.rule.ruleName,
        pathCondition = entity.rule.pathCondition,
        headerCondition = entity.rule.headerCondition,
        actions = entity.actions.map(::ProxyActionModel)
    )

    fun matches(request: Request): Boolean {
        val sanityRegex = pathCondition
            .replace(".", "\\.")
            .replace("*", ".*")

        val regex = Regex(sanityRegex)
        return regex.matches(request.url.toString())
    }

}
