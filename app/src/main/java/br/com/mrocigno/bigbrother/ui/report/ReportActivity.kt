package br.com.mrocigno.bigbrother.ui.report

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.motion.widget.MotionLayout
import br.com.mrocigno.bigbrother.R

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
//            reportView.text = globalTracker.generateReport().generate(this)
        }

        forceCrash.setOnClickListener {
            throw Exception("generic crash")
        }
    }
}