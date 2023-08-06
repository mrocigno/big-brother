package br.com.mrocigno.bigbrother.report.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.mrocigno.bigbrother.report.BigBrotherReport
import br.com.mrocigno.bigbrother.report.R
import br.com.mrocigno.bigbrother.report.entity.SessionEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SessionFragment : Fragment(R.layout.bigbrother_fragment_session) {

    private val recycler: RecyclerView by lazy { requireView().findViewById(R.id.session_recycler) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        CoroutineScope(Dispatchers.Main).launch {
            BigBrotherReport.listSessions().collectLatest {
                recycler.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
                recycler.adapter = SessionAdapter(it, ::onViewClick)
            }
        }
    }

    private fun onViewClick(session: SessionEntity) {
        startActivity(SessionDetailsActivity.intent(requireContext(), session.id))
    }
}