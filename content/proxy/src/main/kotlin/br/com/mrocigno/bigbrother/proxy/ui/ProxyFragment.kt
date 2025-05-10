package br.com.mrocigno.bigbrother.proxy.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.isVisible
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

    private val adapter: ProxyRuleAdapter get() = recycler.adapter as ProxyRuleAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecycler()
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
        recycler.adapter = ProxyRuleAdapter {
            ProxyActivity.intent(requireContext(), it)
                .run(::startActivity)
        }

        lifecycleScope.launch {
            bbdb?.proxyDao()?.getAll()
                ?.map { it.map(::ProxyRuleModel) }
                ?.collect {
                    emptyState.isVisible = it.isEmpty()
                    adapter.items = it
                }
        }
    }
}