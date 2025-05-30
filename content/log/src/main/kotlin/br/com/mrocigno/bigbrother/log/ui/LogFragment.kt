package br.com.mrocigno.bigbrother.log.ui

import android.content.Context
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.mrocigno.bigbrother.common.route.SESSION_ID_ARG
import br.com.mrocigno.bigbrother.common.utils.bbSessionId
import br.com.mrocigno.bigbrother.common.utils.disableChangeAnimation
import br.com.mrocigno.bigbrother.common.utils.getColorState
import br.com.mrocigno.bigbrother.core.OutOfDomain
import br.com.mrocigno.bigbrother.log.BBLog
import br.com.mrocigno.bigbrother.log.R
import br.com.mrocigno.bigbrother.log.model.LogEntryType
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import br.com.mrocigno.bigbrother.common.R as CommonR

@OutOfDomain
internal class LogFragment : Fragment(R.layout.bigbrother_fragment_log) {

    private val chipGroup: ChipGroup by lazy { requireView().findViewById(R.id.log_chip_group) }
    private val recycler: RecyclerView by lazy { requireView().findViewById(R.id.log_recycler) }
    private val searchViewLayout: TextInputLayout by lazy { requireView().findViewById(R.id.log_search_layout) }
    private val searchView: TextInputEditText by lazy { requireView().findViewById(R.id.log_search_view) }
    private val clear: AppCompatImageView by lazy { requireView().findViewById(R.id.log_clear) }
    private val emptyState: ViewGroup by lazy { requireView().findViewById(R.id.log_empty_state) }

    private val sessionId: Long by lazy { arguments?.getLong(SESSION_ID_ARG) ?: bbSessionId }
    private val adapter: LogEntryAdapter get() = recycler.adapter as LogEntryAdapter

    override fun onGetLayoutInflater(savedInstanceState: Bundle?): LayoutInflater {
        val inflater = super.onGetLayoutInflater(savedInstanceState)
        val context = ContextThemeWrapper(inflater.context, CommonR.style.BigBrotherTheme)
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

        lifecycleScope.launchWhenCreated {
            BBLog.getBySession(sessionId)?.collect {
                adapter.setList(it)
                recycler.scrollToPosition(it.size - 1)
                emptyState.isVisible = it.isEmpty()
            }
        }
    }

    private fun setupChipGroup() {
        LogEntryType.values().forEach {
            chipGroup.addView(Chip(context).apply {
                id = it.ordinal
                this.text = it.name
                isCheckable = true
                chipBackgroundColor = getColorState(CommonR.color.bb_background_tertiary)
            })
        }
        chipGroup.isSingleSelection = true
        chipGroup.setOnCheckedChangeListener { _, _ ->
            applyFilter(searchView.text.toString())
        }
    }

    private fun setupSearchView() {
        clear.setOnClickListener {
            BBLog.clear(sessionId)
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
        return ContextThemeWrapper(super.getContext(), CommonR.style.BigBrotherTheme)
    }

    companion object {

        fun newInstance(sessionId: Long = bbSessionId) = LogFragment().apply {
            arguments = bundleOf(SESSION_ID_ARG to sessionId)
        }
    }
}
