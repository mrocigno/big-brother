package br.com.mrocigno.bigbrother.crash

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.drawable.Animatable2
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.view.WindowCompat
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.lifecycleScope
import br.com.mrocigno.bigbrother.common.utils.getSerializableExtraCompat
import br.com.mrocigno.bigbrother.common.utils.highlightStacktrace
import br.com.mrocigno.bigbrother.common.utils.statusBarHeight
import br.com.mrocigno.bigbrother.core.OutOfDomain
import br.com.mrocigno.bigbrother.report.BigBrotherReport
import kotlinx.coroutines.flow.collectLatest

@OutOfDomain
class CrashActivity : AppCompatActivity(R.layout.bigbrother_activity_crash) {

    private val statusBarGuideline: View by lazy { findViewById(R.id.crash_status_bar_guideline) }
    private val root: MotionLayout by lazy { findViewById(R.id.crash_root) }
    private val thumb: AppCompatImageView by lazy { findViewById(R.id.crash_thumb) }
    private val closeAnim: AppCompatImageView by lazy { findViewById(R.id.close_img_avd) }
    private val screenName: AppCompatTextView by lazy { findViewById(R.id.crash_screen_name) }

    private val btnStacktrace: AppCompatTextView by lazy { findViewById(R.id.crash_stacktrace_button) }
    private val stacktrace: AppCompatTextView by lazy { findViewById(R.id.crash_stacktrace) }
    private val btnTimeline: AppCompatTextView by lazy { findViewById(R.id.crash_timeline_button) }
    private val timeline: AppCompatTextView by lazy { findViewById(R.id.crash_timeline) }

    private val sessionId: Long by lazy { intent.getLongExtra(SESSION_ID_ARG, -1) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupWindow()
        setupThumb()
        setupStackTrace()
        setupTimeline()

        startAnimation()

        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (root.currentState == R.id.expanded_img) root.transitionToState(R.id.end)
                else finish()
            }
        })
    }

    private fun setupWindow() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        statusBarGuideline.updateLayoutParams<MarginLayoutParams> { topMargin = statusBarHeight }
    }

    private fun setupThumb() {
        screenName.text = intent.getStringExtra(SCREEN_NAME_ARG)
        val bitmap = runCatching {
            BitmapFactory.decodeStream(openFileInput("print_crash_session_$sessionId.png"))
        }.getOrNull()
        thumb.setImageBitmap(bitmap)
        thumb.clipToOutline = true
    }

    private fun setupStackTrace() {
        btnStacktrace.setOnClickListener {
            stacktrace.isVisible = true
            timeline.isVisible = false
        }
        stacktrace.text = intent.getSerializableExtraCompat<Throwable>(THROWABLE_ARG)
            ?.stackTraceToString()
            ?.highlightStacktrace(this@CrashActivity)
    }

    private fun setupTimeline() {
        runCatching {
            BigBrotherReport.deleteCurrentSession()

            btnTimeline.setOnClickListener {
                timeline.isVisible = true
                stacktrace.isVisible = false
            }

            lifecycleScope.launchWhenCreated {
                BigBrotherReport
                    .getSessionTimeline(sessionId)
                    .collectLatest {
                        timeline.text = it
                    }
            }
        }.onFailure {
            btnTimeline.isVisible = false
        }
    }

    private fun startAnimation() {
        val avd = closeAnim.drawable as AnimatedVectorDrawable
        avd.registerAnimationCallback(object : Animatable2.AnimationCallback() {
            override fun onAnimationEnd(drawable: Drawable?) {
                root.transitionToEnd()
            }
        })
        avd.start()
    }

    companion object {

        private const val SCREEN_NAME_ARG = "bigbrother.SCREEN_NAME_ARG"
        private const val THROWABLE_ARG = "bigbrother.THROWABLE_ARG"
        private const val SESSION_ID_ARG = "bigbrother.SESSION_ID_ARG"

        fun intent(
            context: Context,
            screenName: String,
            sessionId: Long,
            throwable: Throwable
        ) =
            Intent(context, CrashActivity::class.java)
                .putExtra(SCREEN_NAME_ARG, screenName)
                .putExtra(THROWABLE_ARG, throwable)
                .putExtra(SESSION_ID_ARG, sessionId)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
    }
}
