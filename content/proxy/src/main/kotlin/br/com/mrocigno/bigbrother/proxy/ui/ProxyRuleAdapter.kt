package br.com.mrocigno.bigbrother.proxy.ui

import android.text.SpannableStringBuilder
import android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
import android.text.style.BulletSpan
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import br.com.mrocigno.bigbrother.common.db.BigBrotherDatabase.Companion.bbdb
import br.com.mrocigno.bigbrother.common.provider.id
import br.com.mrocigno.bigbrother.common.utils.inflate
import br.com.mrocigno.bigbrother.proxy.R
import br.com.mrocigno.bigbrother.proxy.model.ProxyRuleModel
import br.com.mrocigno.bigbrother.common.R as CR

internal class ProxyRuleAdapter(
    private val onItemClicked: (ProxyRuleModel) -> Unit,
    private val onItemLongClicked: (View, ProxyRuleModel) -> Unit
) : Adapter<RuleViewHolder>() {

    var items: List<ProxyRuleModel>
        get() = differ.currentList
        set(value) {
            allItemsHolder = value
            differ.submitList(value)
        }

    private val differ = AsyncListDiffer(this, ProxyRuleModel.Differ())
    private var query: String = ""
    private var allItemsHolder: List<ProxyRuleModel> = emptyList()

    fun filter(query: String) {
        this.query = query
        val filtered = if (query.isNotBlank()) runCatching {
            allItemsHolder.filter {
                it.ruleName.contains(query, true)
            }
        }.getOrElse { allItemsHolder } else allItemsHolder
        differ.submitList(filtered)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        RuleViewHolder(parent)

    override fun onBindViewHolder(holder: RuleViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
        with(holder.itemView) {
            setOnClickListener { onItemClicked.invoke(item) }
            setOnLongClickListener { onItemLongClicked.invoke(it, item); true }
        }
    }

    override fun getItemCount() = items.size
}

internal class RuleViewHolder(parent: ViewGroup) : ViewHolder(parent.inflate(R.layout.bigbrother_item_proxy_rule)) {

    private val name: AppCompatTextView by id(R.id.proxy_rule_item_name)
    private val condition: AppCompatTextView by id(R.id.proxy_rule_item_condition)
    private val actions: AppCompatTextView by id(R.id.proxy_rule_item_actions)
    private val switch: SwitchCompat by id(R.id.proxy_rule_item_switch)

    private val context get() = itemView.context
    private val gap get() = context.resources.getDimensionPixelSize(CR.dimen.bb_spacing_xxs)

    fun bind(model: ProxyRuleModel) {
        name.text = model.ruleName
        condition.text = when (model.pathCondition == "*" && model.methodCondition == "*" && model.headerCondition.isBlank()) {
            true -> context.getString(R.string.bigbrother_proxy_actions_afects_all)
            false -> context.getString(R.string.bigbrother_proxy_actions_afects_few)
        }
        switch.setOnCheckedChangeListener(null)
        switch.isChecked = model.enabled
        switch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked == model.enabled) return@setOnCheckedChangeListener
            bbdb?.proxyDao()?.updateEnabledById(model.id, isChecked)
        }

        val builder = SpannableStringBuilder()
        model.actions.forEachIndexed { index, action ->
            if (index > 0) builder.appendLine()
            builder.append(action.getDescription(context), BulletSpan(gap), SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        actions.text = builder.ifEmpty {
            builder.append(context.getString(R.string.bigbrother_proxy_actions_do_nothing), BulletSpan(gap), SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }
}