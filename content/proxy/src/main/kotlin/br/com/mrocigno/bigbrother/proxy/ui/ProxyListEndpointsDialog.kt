package br.com.mrocigno.bigbrother.proxy.ui

import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.mrocigno.bigbrother.common.db.BigBrotherDatabase.Companion.bbdb
import br.com.mrocigno.bigbrother.common.utils.showDialog
import br.com.mrocigno.bigbrother.proxy.R
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch
import br.com.mrocigno.bigbrother.common.R as CR

fun AppCompatActivity.proxyListEndpointsDialog(onSelect: (method: String, url: String) -> Unit) {
    var dialog: AlertDialog? = null
    dialog = showDialog(
        content = R.layout.bigbrother_dialog_list_endpoints,
        negativeButton = getString(CR.string.cancel) to { dismiss() },
        onView = {
            val search = findViewById<TextInputEditText>(R.id.proxy_endpoint_search_view)
            val list = findViewById<RecyclerView>(R.id.proxy_endpoint_recycler)
            val emptyState = findViewById<View>(R.id.proxy_endpoint_empty_state)
            val adapter = ProxyEndpointAdapter { method, url ->
                dialog?.dismiss()
                onSelect(method, url)
            }

            list.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            list.adapter = adapter

            search.doOnTextChanged { text, _, _, _ ->
                adapter.filter(text.toString())
            }

            lifecycleScope.launch {
                val eps = bbdb?.networkDao()?.listEndpoints().orEmpty()
                adapter.items = eps
                list.isVisible = eps.isNotEmpty()
                emptyState.isVisible = eps.isEmpty()
            }
        }
    )
    dialog.setOnDismissListener { dialog = null }
}
