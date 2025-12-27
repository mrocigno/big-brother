package br.com.mrocigno.bigbrother.proxy.ui

import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.SwitchCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.mrocigno.bigbrother.common.db.BigBrotherDatabase.Companion.bbdb
import br.com.mrocigno.bigbrother.common.provider.id
import br.com.mrocigno.bigbrother.common.route.RULES_ARG
import br.com.mrocigno.bigbrother.proxy.R
import br.com.mrocigno.bigbrother.proxy.model.ProxyRuleModel
import br.com.mrocigno.bigbrother.proxy.ui.dialog.showExportRulesDialog
import br.com.mrocigno.bigbrother.proxy.ui.dialog.showImportRulesDialog
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import br.com.mrocigno.bigbrother.common.R as CR

internal class ProxyListRulesFragment : Fragment(R.layout.bigbrother_fragment_list_rules) {

    private val recycler: RecyclerView by id(R.id.proxy_recycler)
    private val searchView: TextInputEditText by id(R.id.proxy_search_view)
    private val add: AppCompatImageView by id(R.id.proxy_add)
    private val emptyAdd: View by id(R.id.proxy_empty_add)
    private val emptyState: View by id(R.id.proxy_empty_state)
    private val toggleAll: SwitchCompat by id(R.id.proxy_toggle_all)

    private val viewModel: ProxyListRulesViewModel by activityViewModels()
    private val adapter: ProxyRuleAdapter get() = recycler.adapter as ProxyRuleAdapter
    private val rulesId: LongArray? by lazy { arguments?.getLongArray(RULES_ARG) }
    private val serializer = Json {
        prettyPrint = true
    }

    override fun onGetLayoutInflater(savedInstanceState: Bundle?): LayoutInflater {
        val inflater = super.onGetLayoutInflater(savedInstanceState)
        val context = ContextThemeWrapper(inflater.context, CR.style.BigBrotherTheme)
        return inflater.cloneInContext(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecycler()
        setupForm()
    }

    private fun setupForm() {
        searchView.doOnTextChanged { text, _, _, _ -> adapter.filter(text.toString()) }
        arrayOf(emptyAdd, add).forEach {
            it.setOnClickListener(::createRulePopup)
        }
    }

    private fun createRulePopup(parent: View) {
        PopupMenu(requireContext(), parent).apply {
            inflate(R.menu.bigbrother_proxy_add_rule)
            menu.findItem(R.id.proxy_menu_export_rule).isVisible = adapter.itemCount > 0
            setForceShowIcon(true)
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.proxy_menu_create_rule -> openCreateRuleActivity()
                    R.id.proxy_menu_import_rule -> showImportDialog()
                    R.id.proxy_menu_export_rule -> showExportDialog()
                }
                true
            }
        }.show()
    }

    private fun openCreateRuleActivity() {
        ProxyCreateRuleActivity.intent(requireContext()).run(::startActivity)
    }

    private fun showImportDialog() {
        showImportRulesDialog {
            viewModel.saveAll(it)
        }
    }

    private fun showExportDialog() {
        val json = serializer.encodeToString(adapter.items)
        showExportRulesDialog(json)
    }

    private fun setupRecycler() {
        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.itemAnimator = null
        recycler.adapter = ProxyRuleAdapter(
            onItemClicked = ::onItemClicked,
            onItemLongClicked = ::onItemLongClicked
        )

        lifecycleScope.launch {
            viewModel.listRules(rulesId)
                ?.map { it.map(::ProxyRuleModel) }
                ?.collect {
                    emptyState.isVisible = it.isEmpty()
                    toggleAll.isVisible = it.isNotEmpty()
                    adapter.items = it
                    setupDisableAll(it)
                }
        }
    }

    private fun onItemClicked(rule: ProxyRuleModel) {
        ProxyCreateRuleActivity.intent(requireContext(), rule).run(::startActivity)
    }

    private fun onItemLongClicked(view: View, rule: ProxyRuleModel) {
        val title = view.findViewById(R.id.proxy_rule_item_name) ?: view
        PopupMenu(requireContext(), title).apply {
            inflate(R.menu.bigbrother_proxy_menu)
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.menu_proxy_delete -> lifecycleScope.launch {
                        bbdb?.proxyDao()?.deleteRuleById(rule.id)
                    }
                }
                true
            }
            setForceShowIcon(true)
            show()
        }
    }

    private fun setupDisableAll(it: List<ProxyRuleModel>) {
        with(toggleAll) {
            setOnCheckedChangeListener(null)
            isChecked = it.all { it.enabled }
            setText(
                if (isChecked) R.string.bigbrother_proxy_list_rules_disable_all
                else R.string.bigbrother_proxy_list_rules_enable_all
            )
            setOnCheckedChangeListener { _, isChecked ->
                lifecycleScope.launch {
                    bbdb?.proxyDao()?.updateAllEnabled(enabled = isChecked)
                }
            }
        }
    }

    companion object {

        fun newInstance(rulesId: LongArray?) =
            ProxyListRulesFragment().apply {
                arguments = bundleOf(RULES_ARG to rulesId)
            }
    }
}