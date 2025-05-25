package br.com.mrocigno.bigbrother.deeplink.ui

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import br.com.mrocigno.bigbrother.deeplink.R
import br.com.mrocigno.bigbrother.deeplink.model.DeeplinkAdapterItem
import br.com.mrocigno.bigbrother.deeplink.model.DeeplinkAdapterItem.Companion.ENTRY
import br.com.mrocigno.bigbrother.deeplink.model.DeeplinkAdapterItem.Companion.HEADER
import br.com.mrocigno.bigbrother.deeplink.model.DeeplinkEntry
import br.com.mrocigno.bigbrother.deeplink.model.DeeplinkHeader
import br.com.mrocigno.bigbrother.deeplink.model.DeeplinkType
import br.com.mrocigno.bigbrother.common.R as CR

internal class DeeplinkAdapter(
    private val onClick: (DeeplinkEntry, View) -> Unit,
    private val onLongClick: (DeeplinkEntry, View) -> Unit,
    private val onClearClick: () -> Unit,
) : Adapter<ViewHolder>() {

    private val differ = AsyncListDiffer(this, DeeplinkAdapterItem.Differ())
    private val items: List<DeeplinkAdapterItem> get() = differ.currentList
    private var query: String = ""
    private var typeFilter: DeeplinkType? = null
    private var allItemsHolder: List<DeeplinkAdapterItem> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when (viewType) {
        ENTRY -> DeeplinkEntryView(parent)
        HEADER -> DeeplinkHeaderView(parent)
        else -> throw IllegalArgumentException("Invalid view type: $viewType")
    }

    override fun getItemCount() = items.size

    override fun getItemViewType(position: Int) = items[position].viewType

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = when (holder) {
        is DeeplinkEntryView -> holder.bind(items[position], query, onClick, onLongClick)
        is DeeplinkHeaderView -> holder.bind(items[position], onClearClick)
        else -> throw IllegalArgumentException("Invalid view holder: $holder")
    }

    fun setList(list: List<DeeplinkEntry>) {
        allItemsHolder = list
        filter(query, typeFilter)
    }

    fun filter(query: String, type: DeeplinkType?) {
        this.query = query
        this.typeFilter = type
        val filtered = if (query.isNotBlank() || typeFilter != null) runCatching {
            allItemsHolder.filter {
                it is DeeplinkEntry
                    && it.toString().contains(query, true)
                    && typeFilter?.let { type -> type == it.type } ?: true
            }
        }.getOrElse { allItemsHolder } else allItemsHolder
        differ.submitList(filtered.applyHeaders())
    }

    private fun List<DeeplinkAdapterItem>.applyHeaders(): List<DeeplinkAdapterItem> {
        val numOfRecent = count { it is DeeplinkEntry && it.id != 0 }
        if (numOfRecent == 0) return this

        val newList = mutableListOf<DeeplinkAdapterItem>()

        newList.add(DeeplinkHeader(
            title = R.string.bigbrother_deeplink_recent_title,
            icon = CR.drawable.bigbrother_ic_clock,
            showClear = true
        ))
        newList.addAll(subList(0, numOfRecent))
        newList.add(numOfRecent + 1, DeeplinkHeader(
            title = R.string.bigbrother_deeplink_all,
            icon = CR.drawable.bigbrother_ic_link
        ))
        newList.addAll(subList(numOfRecent, size))
        return newList
    }
}
