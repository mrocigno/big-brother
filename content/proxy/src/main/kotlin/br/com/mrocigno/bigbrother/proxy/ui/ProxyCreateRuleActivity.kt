package br.com.mrocigno.bigbrother.proxy.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.Group
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.mrocigno.bigbrother.common.helpers.SimpleSpinnerAdapter
import br.com.mrocigno.bigbrother.common.provider.id
import br.com.mrocigno.bigbrother.common.route.METHOD_ARG
import br.com.mrocigno.bigbrother.common.route.PATH_ARG
import br.com.mrocigno.bigbrother.common.utils.getParcelableExtraCompat
import br.com.mrocigno.bigbrother.common.utils.gone
import br.com.mrocigno.bigbrother.common.utils.visible
import br.com.mrocigno.bigbrother.core.OutOfDomain
import br.com.mrocigno.bigbrother.proxy.R
import br.com.mrocigno.bigbrother.proxy.model.ProxyRuleModel
import br.com.mrocigno.bigbrother.proxy.randomName
import br.com.mrocigno.bigbrother.proxy.ui.dialog.ProxyActionsAdapter
import br.com.mrocigno.bigbrother.proxy.ui.dialog.proxyAddActionDialog
import br.com.mrocigno.bigbrother.proxy.ui.dialog.proxyAddHeaderDialog
import br.com.mrocigno.bigbrother.proxy.ui.dialog.proxyListEndpointsDialog
import br.com.mrocigno.bigbrother.proxy.ui.dialog.proxyUpdateActionDialog
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import br.com.mrocigno.bigbrother.common.R as CR

@OutOfDomain
internal class ProxyCreateRuleActivity : AppCompatActivity(R.layout.bigbrother_activity_create_rule) {

    private val toolbar: Toolbar by id(R.id.proxy_rule_toolbar)
    private val ruleNameLayout: TextInputLayout by id(R.id.proxy_rule_name_layout)
    private val ruleName: TextInputEditText by id(R.id.proxy_rule_name)
    private val method: AutoCompleteTextView by id(R.id.proxy_rule_method)
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
    private val deleteButton: AppCompatButton by id(R.id.proxy_rule_delete)

    private val viewModel: ProxyCreateRuleViewModel by viewModels()
    private val proxyRuleModel: ProxyRuleModel? by lazy {
        intent.getParcelableExtraCompat(EXTRA_PROXY_RULE_MODEL) ?: run {
            val path = intent.getStringExtra(PATH_ARG)
            val method = intent.getStringExtra(METHOD_ARG)
            if (path != null && method != null) {
                ProxyRuleModel(
                    methodCondition = method,
                    pathCondition = path
                )
            } else null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.navigationBarColor = getColor(CR.color.bb_text_title)

        setupToolbar()
        setupFormFields()
        setupActions()
    }

    private fun setupToolbar() {
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupFormFields() {
        ruleName.setText(proxyRuleModel?.ruleName?.takeIf { it.isNotBlank() } ?: randomName())
        ruleNameLayout.setEndIconOnClickListener {
            ruleName.setText(randomName())
        }

        method.setText(proxyRuleModel?.methodCondition ?: "*")
        method.setAdapter(SimpleSpinnerAdapter(this, resources.getStringArray(CR.array.methods)))

        condition.setText(proxyRuleModel?.pathCondition ?: "*")
        conditionLayout.setEndIconOnClickListener {
            proxyListEndpointsDialog { method, url ->
                this.method.setText(method)
                this.condition.setText(url)
            }
        }

        headers.setText(proxyRuleModel?.headerCondition)
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

        proxyRuleModel?.actions?.run(viewModel::addActions)
        arrayOf(addActionButton, addActionIcon).forEach {
            it.setOnClickListener {
                proxyAddActionDialog(viewModel::addAction)
            }
        }

        saveButton.setOnClickListener {
            viewModel.save(
                currentRule = proxyRuleModel,
                ruleName = ruleName.text.toString().trim(),
                methodCondition = method.text.toString().trim().ifEmpty { "*" },
                pathCondition = condition.text.toString().trim().ifEmpty { "*" },
                headerCondition = headers.text.toString().trim()
            )
            finish()
        }

        val safeModel = proxyRuleModel ?: return
        if (safeModel.id > 0L) {
            deleteButton.visible()
            deleteButton.setOnClickListener {
                viewModel.delete(proxyRuleModel!!)
                finish()
            }
        }
    }

    private fun setupActions() {
        val adapter = ProxyActionsAdapter {
            proxyUpdateActionDialog(
                old = it,
                onDelete = viewModel::removeAction,
                onSave = viewModel::updateAction
            )
        }
        actions.itemAnimator = null
        actions.adapter = adapter
        actions.layoutManager = LinearLayoutManager(this)
        actions.addItemDecoration(ProxyActionItemDecorator(this))
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

        private const val EXTRA_PROXY_RULE_MODEL = "br.com.mrocigno.EXTRA_PROXY_RULE_MODEL"

        fun intent(context: Context, proxyRuleModel: ProxyRuleModel? = null) =
            Intent(context, ProxyCreateRuleActivity::class.java)
                .putExtra(EXTRA_PROXY_RULE_MODEL, proxyRuleModel)
    }
}