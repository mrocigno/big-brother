package br.com.mrocigno.bigbrother.report.ui

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.view.WindowCompat
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.lifecycleScope
import br.com.mrocigno.bigbrother.common.utils.statusBarHeight
import br.com.mrocigno.bigbrother.core.OutOfDomain
import br.com.mrocigno.bigbrother.report.BigBrotherReport
import br.com.mrocigno.bigbrother.report.R
import br.com.mrocigno.bigbrother.common.R as CR

@OutOfDomain
internal class SessionDetailsActivity : AppCompatActivity(R.layout.bigbrother_activity_session_details) {

    private val root: MotionLayout by lazy { findViewById(R.id.session_details_root) }
    private val toolbar: Toolbar by lazy { findViewById(R.id.session_details_toolbar) }
    private val print: AppCompatImageView by lazy { findViewById(R.id.session_details_print) }
    private val timeline: AppCompatTextView by lazy { findViewById(R.id.session_details_timeline) }

    private val sessionId: Long by lazy { intent.getLongExtra(SESSION_ID_ARG, -1) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (sessionId == -1L) throw IllegalArgumentException("invalid session id")

        setupView()
        setupWindow()
        setupTimeline()

        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (root.currentState == R.id.print_expanded) root.transitionToState(R.id.with_print)
                else finish()
            }
        })
    }

    private fun setupWindow() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        toolbar.updateLayoutParams<ViewGroup.MarginLayoutParams> { topMargin = statusBarHeight }
    }

    private fun setupView() {
        toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
        toolbar.title = getString(R.string.bigbrother_report_session_session, sessionId)

        runCatching {
            BitmapFactory.decodeStream(openFileInput("print_crash_session_$sessionId.png"))
        }.getOrNull()
            ?.run(print::setImageBitmap)
            ?.run { setupWithPrint() }
            ?: setupWithoutPrint()
    }

    private fun setupWithPrint() {
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = false
        root.setState(R.id.with_print, root.width, root.height)
    }

    private fun setupWithoutPrint() {
        root.setState(R.id.without_print, root.width, root.height)
        toolbar.setNavigationIcon(CR.drawable.bigbrother_ic_arrow_back)
    }

    private fun setupTimeline() {
        lifecycleScope.launchWhenResumed {
            BigBrotherReport.getSessionTimeline(sessionId).collect {
                timeline.text = it
            }
        }
    }

    companion object {

        private const val SESSION_ID_ARG = "SessionDetailsActivity.SESSION_ID_ARG"

        fun intent(context: Context, sessionId: Long) =
            Intent(context, SessionDetailsActivity::class.java)
                .putExtra(SESSION_ID_ARG, sessionId)
    }
}