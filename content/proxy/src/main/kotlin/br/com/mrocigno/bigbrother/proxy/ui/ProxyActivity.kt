package br.com.mrocigno.bigbrother.proxy.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.Group
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.mrocigno.bigbrother.common.provider.id
import br.com.mrocigno.bigbrother.common.utils.gone
import br.com.mrocigno.bigbrother.common.utils.visible
import br.com.mrocigno.bigbrother.core.OutOfDomain
import br.com.mrocigno.bigbrother.proxy.R
import br.com.mrocigno.bigbrother.proxy.randomName
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import br.com.mrocigno.bigbrother.common.R as CR

@OutOfDomain
class ProxyActivity : AppCompatActivity(R.layout.bigbrother_activity_proxy) {

    private val ruleNameLayout: TextInputLayout by id(R.id.proxy_rule_name_layout)
    private val ruleName: TextInputEditText by id(R.id.proxy_rule_name)
    private val conditionLayout: TextInputLayout by id(R.id.proxy_rule_condition_layout)
    private val condition: TextInputEditText by id(R.id.proxy_rule_condition)
    private val headersLayout: TextInputLayout by id(R.id.proxy_rule_condition_header_layout)
    private val headers: TextInputEditText by id(R.id.proxy_rule_condition_header)
    private val addActionIcon: AppCompatImageView by id(R.id.proxy_rule_add_action)
    private val addActionButton: AppCompatButton by id(R.id.proxy_rule_empty_add_action)
    private val actionsEmptyState: ViewGroup by id(R.id.proxy_rule_actions_empty_state)
    private val actionsGroup: Group by id(R.id.proxy_rule_actions_group)
    private val actions: RecyclerView by id(R.id.proxy_rule_actions_recycler)
    private val saveButton: AppCompatButton by id(R.id.proxy_rule_save)

    private val viewModel: ProxyViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.navigationBarColor = getColor(CR.color.bb_text_title_inverse)

        setupFormFields()
        setupActions()
    }

    private fun setupFormFields() {
        ruleName.setText(randomName())
        ruleNameLayout.setEndIconOnClickListener {
            ruleName.setText(randomName())
        }

        headersLayout.setEndIconOnClickListener {
            proxyAddHeaderDialog { newData ->
                headers.setText(
                    headers.text.toString().trim()
                        .takeIf { it.isNotBlank() }
                        ?.let { "$it;$newData" }
                        ?: newData
                )
            }
        }

        conditionLayout.setEndIconOnClickListener {
            proxyListEndpointsDialog {
                condition.setText(it)
            }
        }

        arrayOf(addActionButton, addActionIcon).forEach {
            it.setOnClickListener {
                proxyAddActionDialog(viewModel::addAction)
            }
        }

        saveButton.setOnClickListener {
            viewModel.save(
                ruleName = ruleName.text.toString(),
                pathCondition = condition.text.toString(),
                headerCondition = headers.text.toString()
            )
        }
    }

    private fun setupActions() {
        val adapter = ProxyActionsAdapter()
        actions.adapter = adapter
        actions.layoutManager = LinearLayoutManager(this)
        viewModel.actions.observe(this) {
            adapter.list = it
            if (it.isNotEmpty()) {
                actionsEmptyState.gone()
                actionsGroup.visible()
            } else {
                actionsEmptyState.visible()
                actionsGroup.gone()
            }
        }
    }

    companion object {

        fun intent(context: Context) =
            Intent(context, ProxyActivity::class.java)
    }
}