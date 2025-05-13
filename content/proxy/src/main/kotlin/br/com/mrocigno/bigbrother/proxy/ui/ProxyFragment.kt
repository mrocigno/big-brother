package br.com.mrocigno.bigbrother.proxy.ui

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.PopupMenu
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.mrocigno.bigbrother.common.db.BigBrotherDatabase.Companion.bbdb
import br.com.mrocigno.bigbrother.common.provider.id
import br.com.mrocigno.bigbrother.proxy.R
import br.com.mrocigno.bigbrother.proxy.model.ProxyRuleModel
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class ProxyFragment : Fragment(R.layout.bigbrother_fragment_proxy) {

    private val recycler: RecyclerView by id(R.id.proxy_recycler)
    private val searchViewLayout: TextInputLayout by id(R.id.proxy_search_layout)
    private val searchView: TextInputEditText by id(R.id.proxy_search_view)
    private val add: AppCompatImageView by id(R.id.proxy_add)
    private val emptyAdd: View by id(R.id.proxy_empty_add)
    private val emptyState: View by id(R.id.proxy_empty_state)
    private val toggleAll: SwitchCompat by id(R.id.proxy_toggle_all)

    private val adapter: ProxyRuleAdapter get() = recycler.adapter as ProxyRuleAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecycler()
        setupForm()
    }

    private fun setupForm() {
        searchView.doOnTextChanged { text, _, _, _ -> adapter.filter(text.toString()) }
        arrayOf(emptyAdd, add).forEach {
            it.setOnClickListener {
                ProxyActivity.intent(requireContext())
                    .run(::startActivity)
            }
        }
    }

    private fun setupRecycler() {
        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.itemAnimator = null
        recycler.adapter = ProxyRuleAdapter(
            onItemClicked = ::onItemClicked,
            onItemLongClicked = ::onItemLongClicked
        )

        lifecycleScope.launch {
            bbdb?.proxyDao()?.getAll()
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
        ProxyActivity.intent(requireContext(), rule).run(::startActivity)
    }

    private fun onItemLongClicked(view: View, rule: ProxyRuleModel) {
        val title = view.findViewById(R.id.proxy_rule_item_name) ?: view
        PopupMenu(requireContext(), title).apply {
            inflate(R.menu.bigbrother_proxy_menu)
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.menu_proxy_delete -> lifecycleScope.launch {
                        bbdb?.proxyDao()?.deleteRule(rule.id)
                    }
                }
                true
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) setForceShowIcon(true)
            show()
        }
    }

    private fun setupDisableAll(it: List<ProxyRuleModel>) {
        with(toggleAll) {
            setOnCheckedChangeListener(null)
            isChecked = it.all { it.enabled }
            setText(
                if (isChecked) R.string.proxy_list_rules_disable_all
                else R.string.proxy_list_rules_enable_all
            )
            setOnCheckedChangeListener { _, isChecked ->
                lifecycleScope.launch {
                    bbdb?.proxyDao()?.updateAllEnabled(enabled = isChecked)
                }
            }
        }
    }
}