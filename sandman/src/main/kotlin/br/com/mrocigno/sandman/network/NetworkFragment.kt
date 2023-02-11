package br.com.mrocigno.sandman.network

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import br.com.mrocigno.sandman.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class NetworkFragment : Fragment(R.layout.fragment_network) {

    private val recycler: RecyclerView by lazy { requireView().findViewById(R.id.net_recycler) }
    private val searchViewLayout: TextInputLayout by lazy { requireView().findViewById(R.id.net_search_layout) }
    private val searchView: TextInputEditText by lazy { requireView().findViewById(R.id.net_search_view) }
    private val clear: AppCompatImageView by lazy { requireView().findViewById(R.id.net_clear) }

    private val adapter: NetworkEntryAdapter get() = recycler.adapter as NetworkEntryAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSearchView()
        setupRecycler()
    }

    private fun setupRecycler() {
        (recycler.itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations = false
        recycler.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        recycler.adapter = NetworkEntryAdapter {
            startActivity(NetworkEntryDetailsActivity.intent(requireContext(), it))
        }

        NetworkHolder.networkEntries.observe(viewLifecycleOwner) {
            adapter.setList(it.toList())
        }
    }

    private fun setupSearchView() {
        clear.setOnClickListener { NetworkHolder.clear() }
        searchViewLayout.setEndIconOnClickListener {
            adapter.filter(searchView.text.toString())
        }
        searchView.doOnTextChanged { text, _, _, _ ->
            adapter.filter(text.toString())
        }
    }
}