package br.com.mrocigno.sandman.log

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
import br.com.mrocigno.sandman.R
import br.com.mrocigno.sandman.utils.getColorState
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class LogFragment : Fragment(R.layout.fragment_log) {

    private val chipGroup: ChipGroup by lazy { requireView().findViewById(R.id.log_chip_group) }
    private val recycler: RecyclerView by lazy { requireView().findViewById(R.id.log_recycler) }
    private val searchViewLayout: TextInputLayout by lazy { requireView().findViewById(R.id.log_search_layout) }
    private val searchView: TextInputEditText by lazy { requireView().findViewById(R.id.log_search_view) }
    private val clear: AppCompatImageView by lazy { requireView().findViewById(R.id.log_clear) }

    private val adapter: LogEntryAdapter get() = recycler.adapter as LogEntryAdapter

    override fun onGetLayoutInflater(savedInstanceState: Bundle?): LayoutInflater {
        val inflater = super.onGetLayoutInflater(savedInstanceState)
        val context = ContextThemeWrapper(inflater.context, R.style.Theme_Sandman)
        return inflater.cloneInContext(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSearchView()
        setupChipGroup()
        setupRecycler()
    }

    private fun setupRecycler() {
        recycler.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        recycler.adapter = LogEntryAdapter()

        SandmanLog.logEntries.observe(viewLifecycleOwner) {
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
                chipBackgroundColor = getColorState(R.color.the_dreaming_background_tertiary)
            })
        }
        chipGroup.isSingleSelection = true
        chipGroup.setOnCheckedChangeListener { _, _ ->
            applyFilter(searchView.text.toString())
        }
    }

    private fun setupSearchView() {
        clear.setOnClickListener {
            SandmanLog.clear()
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
        return ContextThemeWrapper(super.getContext(), R.style.Theme_Sandman)
    }
}