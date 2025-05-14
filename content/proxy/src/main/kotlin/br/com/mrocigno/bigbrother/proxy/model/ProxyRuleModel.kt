package br.com.mrocigno.bigbrother.proxy.model

import android.os.Parcelable
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import br.com.mrocigno.bigbrother.common.entity.ProxyRuleWithActions
import kotlinx.parcelize.Parcelize
import okhttp3.Request

@Parcelize
internal data class ProxyRuleModel(
    val id: Long = 0,
    val ruleName: String = "",
    val methodCondition: String = "",
    val pathCondition: String = "",
    val headerCondition: String = "",
    val actions: List<ProxyActionModel> = emptyList(),
    val enabled: Boolean = true
) : Parcelable {

    constructor(entity: ProxyRuleWithActions) : this(
        id = entity.rule.id,
        ruleName = entity.rule.ruleName,
        methodCondition = entity.rule.methodCondition,
        pathCondition = entity.rule.pathCondition,
        headerCondition = entity.rule.headerCondition,
        actions = entity.actions.map(::ProxyActionModel),
        enabled = entity.rule.enabled
    )

    fun matches(request: Request): Boolean = when {
        !methodCondition.fixRegex()
            .matches(request.method) -> false
        !(headerCondition
            .split(";")
            .filter { it.contains("=") }
            .takeIf { it.isNotEmpty() }
            ?.any { condition ->
                val (name, value) = condition.split("=", limit = 2)
                value.fixRegex().matches(request.headers[name].orEmpty())
            }
            ?: true) -> false
        !pathCondition.fixRegex().matches(request.url.toString()) -> false
        else -> true
    }

    private fun String.fixRegex() =
        this.replace(".", "\\.")
            .replace("*", ".*")
            .toRegex()

    class Differ : ItemCallback<ProxyRuleModel>() {

        override fun areItemsTheSame(oldItem: ProxyRuleModel, newItem: ProxyRuleModel) =
            oldItem.ruleName == newItem.ruleName

        override fun areContentsTheSame(oldItem: ProxyRuleModel, newItem: ProxyRuleModel) =
            oldItem == newItem
    }
}
