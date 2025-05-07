package br.com.mrocigno.bigbrother.network.ui

import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.mrocigno.bigbrother.common.utils.bbSessionId
import br.com.mrocigno.bigbrother.common.utils.disableChangeAnimation
import br.com.mrocigno.bigbrother.core.OutOfDomain
import br.com.mrocigno.bigbrother.network.BigBrotherNetworkHolder
import br.com.mrocigno.bigbrother.network.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import br.com.mrocigno.bigbrother.common.R as CommonR

@OutOfDomain
internal class NetworkFragment : Fragment(R.layout.bigbrother_fragment_network) {

    private val recycler: RecyclerView by lazy { requireView().findViewById(R.id.net_recycler) }
    private val searchViewLayout: TextInputLayout by lazy { requireView().findViewById(R.id.net_search_layout) }
    private val searchView: TextInputEditText by lazy { requireView().findViewById(R.id.net_search_view) }
    private val clear: AppCompatImageView by lazy { requireView().findViewById(R.id.net_clear) }
    private val emptyState: View by lazy { requireView().findViewById(R.id.net_empty_state) }

    private val sessionId: Long by lazy { arguments?.getLong(SESSION_ID_ARG) ?: bbSessionId }
    private val adapter: NetworkEntryAdapter get() = recycler.adapter as NetworkEntryAdapter

    override fun onGetLayoutInflater(savedInstanceState: Bundle?): LayoutInflater {
        val inflater = super.onGetLayoutInflater(savedInstanceState)
        val context = ContextThemeWrapper(inflater.context, CommonR.style.Theme_BigBrother)
        return inflater.cloneInContext(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSearchView()
        setupRecycler()
    }

    private fun setupRecycler() {
        recycler.disableChangeAnimation()
        recycler.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        recycler.adapter = NetworkEntryAdapter {
            startActivity(NetworkEntryDetailsActivity.intent(requireContext(), it.id))
        }

        lifecycleScope.launchWhenCreated {
            BigBrotherNetworkHolder.getBySessionId(sessionId)?.collect {
                emptyState.isVisible = it.isEmpty()
                adapter.setList(it.toList())
            }
        }
    }

    private fun setupSearchView() {
        clear.setOnClickListener {
            BigBrotherNetworkHolder.clear(sessionId)
        }
        searchViewLayout.setEndIconOnClickListener {
            adapter.filter(searchView.text.toString())
        }
        searchView.doOnTextChanged { text, _, _, _ ->
            adapter.filter(text.toString())
        }
    }

    companion object {

        private const val SESSION_ID_ARG = "BigBrother.SESSION_ID"

        fun newInstance(sessionId: Long = bbSessionId) = NetworkFragment().apply {
            arguments = Bundle().apply {
                putLong(SESSION_ID_ARG, sessionId)
            }
        }
    }
}