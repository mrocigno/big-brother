package br.com.mrocigno.bigbrother.ui.report

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.lifecycle.lifecycleScope
import br.com.mrocigno.bigbrother.R
import br.com.mrocigno.bigbrother.core.utils.bbSessionId
import br.com.mrocigno.bigbrother.report.BigBrotherReport
import kotlinx.coroutines.flow.collectLatest

class ReportActivity : AppCompatActivity(R.layout.report_activity) {

    private val root: MotionLayout by lazy { findViewById(R.id.root) }
    private val toolbar: Toolbar by lazy { findViewById(R.id.toolbar) }
    private val reportView: AppCompatTextView by lazy { findViewById(R.id.report_view) }
    private val makeReport: AppCompatButton by lazy { findViewById(R.id.make_report) }
    private val forceCrash: AppCompatButton by lazy { findViewById(R.id.crash_button) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        toolbar.setNavigationOnClickListener {
            root.transitionToStart()
        }

        makeReport.setOnClickListener {
            lifecycleScope.launchWhenCreated {
                BigBrotherReport.getSessionTimeline(bbSessionId).collectLatest {
                    reportView.text = it
                }
            }
        }

        forceCrash.setOnClickListener {
            throw Exception("generic crash")
        }
    }
}