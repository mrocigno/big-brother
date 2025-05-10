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
    val pathCondition: String = "",
    val headerCondition: String = "",
    val actions: List<ProxyActionModel> = emptyList(),
    val enabled: Boolean = true
) : Parcelable {

    constructor(entity: ProxyRuleWithActions) : this(
        id = entity.rule.id,
        ruleName = entity.rule.ruleName,
        pathCondition = entity.rule.pathCondition,
        headerCondition = entity.rule.headerCondition,
        actions = entity.actions.map(::ProxyActionModel),
        enabled = entity.rule.enabled
    )

    fun matches(request: Request): Boolean {
        val sanityRegex = pathCondition
            .replace(".", "\\.")
            .replace("*", ".*")

        val regex = Regex(sanityRegex)
        return regex.matches(request.url.toString())
    }

    class Differ : ItemCallback<ProxyRuleModel>() {

        override fun areItemsTheSame(oldItem: ProxyRuleModel, newItem: ProxyRuleModel) =
            oldItem.ruleName == newItem.ruleName

        override fun areContentsTheSame(oldItem: ProxyRuleModel, newItem: ProxyRuleModel) =
            oldItem == newItem
    }
}
