package br.com.mrocigno.sandman.matthew

import android.content.Context
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.mrocigno.sandman.common.utils.disableChangeAnimation
import br.com.mrocigno.sandman.common.utils.getColorState
import br.com.mrocigno.sandman.core.OutOfDomain
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import br.com.mrocigno.sandman.common.R as CommonR

@OutOfDomain
class LogFragment : Fragment(R.layout.matthew_fragment_log) {

    private val chipGroup: ChipGroup by lazy { requireView().findViewById(R.id.log_chip_group) }
    private val recycler: RecyclerView by lazy { requireView().findViewById(R.id.log_recycler) }
    private val searchViewLayout: TextInputLayout by lazy { requireView().findViewById(R.id.log_search_layout) }
    private val searchView: TextInputEditText by lazy { requireView().findViewById(R.id.log_search_view) }
    private val clear: AppCompatImageView by lazy { requireView().findViewById(R.id.log_clear) }

    private val adapter: LogEntryAdapter get() = recycler.adapter as LogEntryAdapter

    override fun onGetLayoutInflater(savedInstanceState: Bundle?): LayoutInflater {
        val inflater = super.onGetLayoutInflater(savedInstanceState)
        val context = ContextThemeWrapper(inflater.context, CommonR.style.Theme_Sandman)
        return inflater.cloneInContext(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSearchView()
        setupChipGroup()
        setupRecycler()
    }

    private fun setupRecycler() {
        recycler.disableChangeAnimation()
        recycler.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        recycler.adapter = LogEntryAdapter()

        Matthew.logEntries.observe(viewLifecycleOwner) {
            adapter.setList(it)
            recycler.scrollToPosition(it.size - 1)
        }
    }

    private fun setupChipGroup() {
        LogEntryType.values().forEach {
            chipGroup.addView(Chip(context).apply {
                id = it.ordinal
                this.text = it.name
                isCheckable = true
                chipBackgroundColor = getColorState(CommonR.color.the_dreaming_background_tertiary)
            })
        }
        chipGroup.isSingleSelection = true
        chipGroup.setOnCheckedChangeListener { _, _ ->
            applyFilter(searchView.text.toString())
        }
    }

    private fun setupSearchView() {
        clear.setOnClickListener {
            Matthew.clear()
        }
        searchViewLayout.setEndIconOnClickListener {
            applyFilter(searchView.text.toString())
        }
        searchView.doOnTextChanged { text, _, _, _ ->
            applyFilter(text.toString())
        }
    }

    private fun applyFilter(query: String) {
        val type = runCatching { LogEntryType.values()[chipGroup.checkedChipId] }.getOrNull()
        adapter.filter(query, type)
    }

    override fun getContext(): Context {
        return ContextThemeWrapper(super.getContext(), CommonR.style.Theme_Sandman)
    }
}