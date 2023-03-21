package br.com.mrocigno.sandman.cain

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
import androidx.core.view.updateLayoutParams
import br.com.mrocigno.sandman.common.utils.getSerializableExtraCompat
import br.com.mrocigno.sandman.common.utils.highlightStacktrace
import br.com.mrocigno.sandman.common.utils.statusBarHeight
import br.com.mrocigno.sandman.core.OutOfDomain

@OutOfDomain
class CrashActivity : AppCompatActivity(R.layout.cain_activity_crash) {

    private val statusBarGuideline: View by lazy { findViewById(R.id.crash_status_bar_guideline) }
    private val root: MotionLayout by lazy { findViewById(R.id.crash_root) }
    private val thumb: AppCompatImageView by lazy { findViewById(R.id.crash_thumb) }
    private val closeAnim: AppCompatImageView by lazy { findViewById(R.id.close_img_avd) }
    private val screenName: AppCompatTextView by lazy { findViewById(R.id.crash_screen_name) }
    private val stacktrace: AppCompatTextView by lazy { findViewById(R.id.crash_stacktrace) }

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
        stacktrace.text = intent.getSerializableExtraCompat<Throwable>(EXCEPTION_ARG)
            ?.stackTraceToString()
            ?.highlightStacktrace(this@CrashActivity)

        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (root.currentState == R.id.expanded_img) root.transitionToState(R.id.end)
                else finish()
            }
        })
    }

    companion object {

        private const val SCREEN_NAME_ARG = "sandman.SCREEN_NAME_ARG"
        private const val EXCEPTION_ARG = "sandman.EXCEPTION_ARG"

        fun intent(context: Context, screenName: String, throwable: Throwable) =
            Intent(context, CrashActivity::class.java)
                .putExtra(SCREEN_NAME_ARG, screenName)
                .putExtra(EXCEPTION_ARG, throwable)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
    }
}
