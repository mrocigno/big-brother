package br.com.mrocigno.bigbrother.crash

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
import br.com.mrocigno.bigbrother.common.db.BigBrotherDatabase.Companion.bbdb
import br.com.mrocigno.bigbrother.common.entity.CrashEntity
import br.com.mrocigno.bigbrother.common.route.ANIMATE_ARG
import br.com.mrocigno.bigbrother.common.route.SESSION_ID_ARG
import br.com.mrocigno.bigbrother.common.utils.highlightStacktrace
import br.com.mrocigno.bigbrother.common.utils.statusBarHeight
import br.com.mrocigno.bigbrother.core.OutOfDomain
import br.com.mrocigno.bigbrother.report.BigBrotherReport
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.runBlocking
import kotlin.system.exitProcess

@OutOfDomain
internal class CrashActivity : AppCompatActivity(R.layout.bigbrother_activity_crash) {

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
    private val animate: Boolean by lazy { intent.getBooleanExtra(ANIMATE_ARG, true) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (animate) BigBrotherReport.deleteCurrentSession()

        setupWindow()
        startAnimation()

        runBlocking {
            val entity = bbdb?.crashDao()?.getBySessionId(sessionId) ?: run {
                finish()
                return@runBlocking
            }

            setupThumb(entity)
            setupStackTrace(entity)
            setupTimeline()
        }

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

    private fun setupThumb(entity: CrashEntity) {
        screenName.text = entity.activityName
        val bitmap = runCatching {
            BitmapFactory.decodeStream(openFileInput("print_crash_session_$sessionId.png"))
        }.getOrNull()
        thumb.setImageBitmap(bitmap)
        thumb.clipToOutline = true
    }

    override fun finish() {
        super.finish()
        if (animate) exitProcess(0)
    }

    private fun setupStackTrace(entity: CrashEntity) {
        btnStacktrace.setOnClickListener {
            stacktrace.isVisible = true
            timeline.isVisible = false
        }
        stacktrace.text = entity.stackTrace
            ?.highlightStacktrace(this@CrashActivity)
    }

    private fun setupTimeline() {
        runCatching {
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
        if (!animate) {
            root.setState(R.id.end, 0, 0)
        } else {
            avd.registerAnimationCallback(object : Animatable2.AnimationCallback() {
                override fun onAnimationEnd(drawable: Drawable?) {
                    root.transitionToEnd()
                }
            })
        }
        avd.start()
        closeAnim.setOnClickListener { finish() }
    }
}
