package br.com.mrocigno.bigbrother.database.ui

import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.mrocigno.bigbrother.common.FileTreeDecoration
import br.com.mrocigno.bigbrother.core.utils.getTask
import br.com.mrocigno.bigbrother.database.DatabaseTask
import br.com.mrocigno.bigbrother.database.R
import br.com.mrocigno.bigbrother.database.model.DatabaseListItem
import br.com.mrocigno.bigbrother.database.ui.table.TableInspectorActivity
import br.com.mrocigno.bigbrother.common.R as CR

class DatabaseFragment : Fragment(R.layout.bigbrother_fragment_database) {

    private val recyclerView: RecyclerView by lazy { requireView().findViewById(R.id.db_recycler) }
    private val emptyState: ViewGroup by lazy { requireView().findViewById(R.id.db_empty_state) }
    private val refresh: View by lazy { requireView().findViewById(R.id.db_refresh) }

    private val adapter: DatabaseAdapter get() = recyclerView.adapter as DatabaseAdapter

    override fun onGetLayoutInflater(savedInstanceState: Bundle?): LayoutInflater {
        val inflater = super.onGetLayoutInflater(savedInstanceState)
        val context = ContextThemeWrapper(inflater.context, CR.style.Theme_BigBrother)
        return inflater.cloneInContext(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecycler()
        refresh.setOnClickListener {
            getTask(DatabaseTask::class)?.listDefaultDatabases()
            setupRecycler()
        }
    }

    private fun setupRecycler() {
        recyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        recyclerView.addItemDecoration(FileTreeDecoration(requireContext()))
        recyclerView.adapter = DatabaseAdapter(::onItemClick)
        emptyState.isVisible = adapter.list.isEmpty()
    }

    private fun onItemClick(model: DatabaseListItem) {
        when (model.type) {
            DatabaseListItem.DATABASE -> Unit
            DatabaseListItem.TABLE -> {
                TableInspectorActivity
                    .intent(requireContext(), model.databaseHelper.name, model.title)
                    .run(::startActivity)
            }
        }
    }
}