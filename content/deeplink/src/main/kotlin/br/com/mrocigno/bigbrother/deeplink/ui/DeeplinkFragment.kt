package br.com.mrocigno.bigbrother.deeplink.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.PopupMenu
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.mrocigno.bigbrother.common.provider.id
import br.com.mrocigno.bigbrother.common.utils.copyToClipboard
import br.com.mrocigno.bigbrother.common.utils.disableChangeAnimation
import br.com.mrocigno.bigbrother.common.utils.getColorState
import br.com.mrocigno.bigbrother.core.OutOfDomain
import br.com.mrocigno.bigbrother.deeplink.R
import br.com.mrocigno.bigbrother.deeplink.model.DeeplinkEntry
import br.com.mrocigno.bigbrother.deeplink.model.DeeplinkType
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import br.com.mrocigno.bigbrother.common.R as CommonR

@OutOfDomain
internal class DeeplinkFragment : Fragment(R.layout.bigbrother_fragment_deeplink) {

    private val chipGroup: ChipGroup by id(R.id.deeplink_chip_group)
    private val recycler: RecyclerView by id(R.id.deeplink_recycler)
    private val searchViewLayout: TextInputLayout by id(R.id.deeplink_search_layout)
    private val searchView: TextInputEditText by id(R.id.deeplink_search_view)
    private val newLink: AppCompatImageView by id(R.id.deeplink_new)
    private val emptyState: ViewGroup by id(R.id.deeplink_empty_state)

    private val adapter: DeeplinkAdapter get() = recycler.adapter as DeeplinkAdapter
    private val viewModel: DeeplinkViewModel by activityViewModels()

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
        recycler.adapter = DeeplinkAdapter(::onItemClick, ::onItemLongClick, ::onClearClick)

        lifecycleScope.launch {
            viewModel.getRecentDeeplinks()?.collectLatest {
                val links = it + viewModel.getDeeplinks(requireContext().assets)
                emptyState.isVisible = links.isEmpty()
                adapter.setList(links)
            }
        }
    }

    private fun onItemClick(model: DeeplinkEntry, view: View) {
        if (model.id != 0) launchDeeplink(model)
        else showPopupMenu(model, view, false)
    }

    private fun onItemLongClick(model: DeeplinkEntry, view: View) {
        if (model.id == 0) return
        showPopupMenu(model, view, true)
    }

    private fun onClearClick() = viewModel.deleteAll()

    private fun showPopupMenu(model: DeeplinkEntry, view: View, shouldUpdate: Boolean) {
        PopupMenu(context, view).apply {
            inflate(R.menu.deeplink_menu)
            setForceShowIcon(true)
            menu.findItem(R.id.deeplink_menu_delete).isVisible = shouldUpdate
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.deeplink_menu_start -> launchDeeplink(model)
                    R.id.deeplink_menu_append -> launchAppendQueryModal(model, shouldUpdate)
                    R.id.deeplink_menu_copy -> copyPath(model)
                    R.id.deeplink_menu_delete -> viewModel.delete(model.toEntity())
                }
                true
            }
        }.show()
    }

    private fun launchDeeplink(model: DeeplinkEntry) {
        try {
            Intent(Intent.ACTION_VIEW)
                .setData(model.path.toUri())
                .run(::startActivity)
            viewModel.save(model.toEntity())
        } catch (e: Exception) {
            Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun launchAppendQueryModal(model: DeeplinkEntry, shouldUpdate: Boolean) {
        showAppendQueryDialog(model) { path ->
            launchDeeplink(model.copy(
                id = if (shouldUpdate) model.id else 0,
                path = path
            ))
        }
    }

    private fun copyPath(model: DeeplinkEntry) {
        requireActivity().copyToClipboard(
            text = model.path,
            toastFeedback = getString(R.string.bigbrother_deeplink_copied),
        )
    }

    private fun setupChipGroup() {
        DeeplinkType.values().forEach {
            chipGroup.addView(Chip(context).apply {
                id = it.ordinal
                text = it.name
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
        newLink.setOnClickListener {
            showNewLinkDialog(::launchDeeplink)
        }
        searchViewLayout.setEndIconOnClickListener {
            applyFilter(searchView.text.toString())
        }
        searchView.doOnTextChanged { text, _, _, _ ->
            applyFilter(text.toString())
        }
    }

    private fun applyFilter(query: String) {
        val type = runCatching { DeeplinkType.values()[chipGroup.checkedChipId] }.getOrNull()
        adapter.filter(query, type)
    }

    override fun getContext(): Context {
        return ContextThemeWrapper(super.getContext(), CommonR.style.BigBrotherTheme)
    }
}
