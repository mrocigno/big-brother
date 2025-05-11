package br.com.mrocigno.bigbrother.proxy.ui

import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.mrocigno.bigbrother.common.db.BigBrotherDatabase.Companion.bbdb
import br.com.mrocigno.bigbrother.common.helpers.StringDiffer
import br.com.mrocigno.bigbrother.common.utils.inflate
import br.com.mrocigno.bigbrother.common.utils.showDialog
import br.com.mrocigno.bigbrother.proxy.R
import kotlinx.coroutines.launch
import br.com.mrocigno.bigbrother.common.R as CR

fun AppCompatActivity.proxyListEndpointsDialog(onSelect: (String) -> Unit) {
    var dialog: AlertDialog? = null
    dialog = showDialog(
        content = R.layout.bigbrother_dialog_list_endpoints,
        negativeButton = getString(CR.string.cancel) to { dismiss() },
        onView = {
            val list = findViewById<RecyclerView>(R.id.proxy_endpoint_recycler)
            val emptyState = findViewById<View>(R.id.proxy_endpoint_empty_state)
            val adapter = EndpointsAdapter {
                dialog?.dismiss()
                onSelect(it)
            }

            list.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            list.adapter = adapter

            lifecycleScope.launch {
                bbdb?.networkDao()?.listEndpoints()?.collect { eps ->
                    adapter.items = eps
                    list.isVisible = eps.isNotEmpty()
                    emptyState.isVisible = eps.isEmpty()
                }
            }
        }
    )
    dialog.setOnDismissListener { dialog = null }
}

private class EndpointsAdapter(
    private val onSelect: (String) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var items: List<String>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    private val differ = AsyncListDiffer(this, StringDiffer())

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder = object : RecyclerView.ViewHolder(parent.inflate(R.layout.bigbrother_item_endpoint)) {}

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        (holder.itemView as AppCompatTextView).apply {
            text = items[position]
            setOnClickListener {
                onSelect(items[position])
            }
        }
    }

    override fun getItemCount(): Int = items.size

}