package br.com.mrocigno.bigbrother.report.ui

import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.mrocigno.bigbrother.common.route.checkIntent
import br.com.mrocigno.bigbrother.common.route.intentToCrash
import br.com.mrocigno.bigbrother.common.route.intentToLogList
import br.com.mrocigno.bigbrother.common.route.intentToNetworkList
import br.com.mrocigno.bigbrother.report.BigBrotherReport
import br.com.mrocigno.bigbrother.report.R
import br.com.mrocigno.bigbrother.report.model.SessionEntry
import br.com.mrocigno.bigbrother.report.model.SessionStatus
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import java.util.concurrent.CancellationException
import br.com.mrocigno.bigbrother.common.R as CR

internal class SessionFragment : Fragment(R.layout.bigbrother_fragment_session) {

    private val recycler: RecyclerView by lazy { requireView().findViewById(R.id.session_recycler) }
    private val searchLayout: TextInputLayout by lazy { requireView().findViewById(R.id.session_search_layout) }
    private val search: TextInputEditText by lazy { requireView().findViewById(R.id.session_search_view) }
    private val editDate: AppCompatImageView by lazy { requireView().findViewById(R.id.session_edit_date) }

    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.Main + job)
    private val dateFilter = MutableLiveData(LocalDate.now())
    private val adapter: SessionAdapter get() = recycler.adapter as SessionAdapter

    override fun onGetLayoutInflater(savedInstanceState: Bundle?): LayoutInflater {
        val inflater = super.onGetLayoutInflater(savedInstanceState)
        val context = ContextThemeWrapper(inflater.context, CR.style.BigBrotherTheme)
        return inflater.cloneInContext(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recycler.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        recycler.adapter = SessionAdapter(::onViewClick)

        setupSearch()
        dateFilter.observe(viewLifecycleOwner) {
            val text = it?.run { format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) }
            search.setText(text)
            scope.launch {
                BigBrotherReport.listSessions(it)?.collectLatest { sessions ->
                    adapter.list = sessions
                }
            }
        }
    }

    private fun setupSearch() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) return
        searchLayout.setEndIconOnClickListener { dateFilter.postValue(null) }
        editDate.setOnClickListener {
            DatePickerDialog(requireContext()).apply {
                setOnDateSetListener { _, year, month, dayOfMonth ->
                    dateFilter.postValue(LocalDate.of(year, month + 1, dayOfMonth))
                }
                val date = dateFilter.value ?: LocalDate.now()
                updateDate(date.year, date.monthValue.minus(1), date.dayOfMonth)
            }.show()
        }
    }

    private fun onViewClick(session: SessionEntry, view: View) {
        PopupMenu(requireContext(), view, Gravity.CENTER_HORIZONTAL).apply {
            inflate(R.menu.bigbrother_session_menu)
            setForceShowIcon(true)

            val sessionIntent = SessionDetailsActivity.intent(requireContext(), session.id)
            val networkSessionIntent = requireContext().intentToNetworkList(session.id)
            val logSessionIntent = requireContext().intentToLogList(session.id)
            val crashIntent = requireContext().intentToCrash(session.id,false)

            val isCrashed = session.status == SessionStatus.CRASHED
            val hasNetwork = requireContext().checkIntent(networkSessionIntent)
            val hasLog = requireContext().checkIntent(logSessionIntent)

            menu.findItem(R.id.menu_session_crash).isVisible = isCrashed
            menu.findItem(R.id.menu_session_network).isVisible = hasNetwork
            menu.findItem(R.id.menu_session_logs).isVisible = hasLog

            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.menu_session_crash -> if (requireContext().checkIntent(crashIntent)) {
                        startActivity(crashIntent)
                    } else {
                        startActivity(sessionIntent)
                    }
                    R.id.menu_session_report -> startActivity(sessionIntent)
                    R.id.menu_session_network -> startActivity(networkSessionIntent)
                    R.id.menu_session_logs -> startActivity(logSessionIntent)
                }
                true
            }
        }.show()
    }

    override fun onDestroy() {
        job.cancel(CancellationException("fragment destroyed"))
        super.onDestroy()
    }
}
