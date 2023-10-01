package br.com.mrocigno.bigbrother.database.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.mrocigno.bigbrother.common.FileTreeDecoration
import br.com.mrocigno.bigbrother.database.R
import br.com.mrocigno.bigbrother.database.model.DatabaseListItem
import br.com.mrocigno.bigbrother.database.ui.table.TableInspectorActivity

class DatabaseFragment : Fragment(R.layout.bigbrother_fragment_database) {

    private val recyclerView: RecyclerView get() = view as RecyclerView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        recyclerView.addItemDecoration(FileTreeDecoration(requireContext()))
        recyclerView.adapter = DatabaseAdapter(::onItemClick)
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