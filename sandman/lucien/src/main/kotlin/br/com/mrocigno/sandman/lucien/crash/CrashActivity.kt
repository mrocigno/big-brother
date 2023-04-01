package br.com.mrocigno.sandman.lucien.crash

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
import br.com.mrocigno.sandman.common.utils.getSerializableExtraCompat
import br.com.mrocigno.sandman.common.utils.highlightStacktrace
import br.com.mrocigno.sandman.common.utils.statusBarHeight
import br.com.mrocigno.sandman.core.OutOfDomain
import br.com.mrocigno.sandman.core.model.ReportModel
import br.com.mrocigno.sandman.lucien.R
import br.com.mrocigno.sandman.lucien.generateReport

@OutOfDomain
class CrashActivity : AppCompatActivity(R.layout.lucien_activity_crash) {

    private val statusBarGuideline: View by lazy { findViewById(R.id.crash_status_bar_guideline) }
    private val root: MotionLayout by lazy { findViewById(R.id.crash_root) }
    private val thumb: AppCompatImageView by lazy { findViewById(R.id.crash_thumb) }
    private val closeAnim: AppCompatImageView by lazy { findViewById(R.id.close_img_avd) }
    private val screenName: AppCompatTextView by lazy { findViewById(R.id.crash_screen_name) }

    private val btnStacktrace: AppCompatTextView by lazy { findViewById(R.id.crash_stacktrace_button) }
    private val stacktrace: AppCompatTextView by lazy { findViewById(R.id.crash_stacktrace) }
    private val btnTimeline: AppCompatTextView by lazy { findViewById(R.id.crash_timeline_button) }
    private val timeline: AppCompatTextView by lazy { findViewById(R.id.crash_timeline) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        statusBarGuideline.updateLayoutParams<MarginLayoutParams> { topMargin = statusBarHeight }

        val avd = closeAnim.drawable as AnimatedVectorDrawable
        avd.registerAnimationCallback(object : Animatable2.AnimationCallback() {

            override fun onAnimationEnd(drawable: Drawable?) {
                root.transitionToEnd()
            }
        })
        avd.start()

        val bitmap = runCatching {
            BitmapFactory.decodeStream(openFileInput("print_crash.png"))
        }.getOrNull()
        thumb.setImageBitmap(bitmap)
        thumb.clipToOutline = true

        screenName.text = intent.getStringExtra(SCREEN_NAME_ARG)
        btnStacktrace.setOnClickListener {
            stacktrace.isVisible = true
            timeline.isVisible = false
        }
        stacktrace.text = intent.getSerializableExtraCompat<Throwable>(THROWABLE_ARG)
            ?.stackTraceToString()
            ?.highlightStacktrace(this@CrashActivity)

        btnTimeline.setOnClickListener {
            timeline.isVisible = true
            stacktrace.isVisible = false
        }
        timeline.text = intent.getSerializableExtraCompat<ArrayList<ReportModel>>(GLOBAL_TRACKER_ARG)
            ?.generateReport()
            ?.generate(this)

        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (root.currentState == R.id.expanded_img) root.transitionToState(R.id.end)
                else finish()
            }
        })
    }

    companion object {

        private const val SCREEN_NAME_ARG = "sandman.SCREEN_NAME_ARG"
        private const val THROWABLE_ARG = "sandman.THROWABLE_ARG"
        private const val GLOBAL_TRACKER_ARG = "sandman.GLOBAL_TRACKER_ARG"

        fun intent(
            context: Context,
            screenName: String,
            list: ArrayList<ReportModel>,
            throwable: Throwable
        ) =
            Intent(context, CrashActivity::class.java)
                .putExtra(SCREEN_NAME_ARG, screenName)
                .putExtra(GLOBAL_TRACKER_ARG, list)
                .putExtra(THROWABLE_ARG, throwable)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
    }
}
