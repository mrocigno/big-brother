package br.com.mrocigno.bigbrother.database.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.mrocigno.bigbrother.common.FileTreeDecoration
import br.com.mrocigno.bigbrother.common.utils.getColor
import br.com.mrocigno.bigbrother.database.R
import br.com.mrocigno.bigbrother.common.R as CR

class DatabaseFragment : Fragment(R.layout.bigbrother_fragment_database) {

    private val recyclerView: RecyclerView get() = view as RecyclerView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        recyclerView.adapter = DatabaseAdapter()
        recyclerView.addItemDecoration(FileTreeDecoration(getColor(CR.color.text_title)))
    }
}