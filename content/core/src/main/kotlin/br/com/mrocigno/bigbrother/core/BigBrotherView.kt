package br.com.mrocigno.bigbrother.core

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.graphics.RectF
import android.os.Build
import android.util.AttributeSet
import android.view.ContextThemeWrapper
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.contains
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import br.com.mrocigno.bigbrother.common.utils.getNavigationBarHeight
import br.com.mrocigno.bigbrother.common.utils.statusBarHeight
import java.lang.Float.max
import java.lang.Float.min
import kotlin.math.roundToLong
import br.com.mrocigno.bigbrother.common.R as CommonR

@SuppressLint("ClickableViewAccessibility")
class BigBrotherView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    private val config: BigBrotherConfig = BigBrother.config
) : FrameLayout(
    ContextThemeWrapper(context, CommonR.style.Theme_BigBrother),
    attrs,
    defStyleAttr
) {

    val isExpanded: Boolean get() = bigBrotherContainerView.isExpanded

    private val activity get() = (context as ContextThemeWrapper).baseContext as FragmentActivity
    private val parentVG get() = parent as ViewGroup
    private val bigBrotherContainerView by lazy { BigBrotherContainerView(this) }
    private val statusBarHeight by lazy { activity.statusBarHeight.toFloat() }
    private val navigationBarHeight by lazy { activity.getNavigationBarHeight().toFloat() }
    private val fadeAnimation =
        ObjectAnimator.ofFloat(this@BigBrotherView, "alpha", 1f, config.disabledAlpha)
            .apply { startDelay = 2000L }

    private var onVortexKilled: (() -> Unit)? = null
    private var onMoveCallback: ((BigBrotherView, Boolean) -> Unit)? = null
    internal val View.bounds get() = RectF(x, y, x + width, y + height)
    internal val area by lazy {
        val right = parentVG.width - width.toFloat()
        RectF(0f, statusBarHeight, right, parentVG.height - navigationBarHeight)
    }

    private val removableView by lazy {
        FrameLayout(context).apply {
            val padding = resources.getDimensionPixelSize(CommonR.dimen.bb_spacing_s)
            setBackgroundResource(CommonR.drawable.bigbrother_remove_area_background)
            layoutParams = LayoutParams(config.size, config.size).apply {
                x = area.width() / 2
                y = area.bottom - padding - height
            }
        }
    }

    init {
        id = R.id.bigbrother
        config.initial(this)
        z = 10f

        setOnLongClickListener {
            parentVG.addView(removableView)

            setOnTouchListener { _, event ->
                removableArea(event)
                moveBubble(event)
                true
            }

            true
        }

        setOnClickListener {
            fadeAnimation.cancel()
            if (bigBrotherContainerView.isExpanded) {
                animateBack()
            } else {
                animateToCenter()
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            systemGestureExclusionRects = listOf(Rect(0, 0, config.size, config.size))
        }
    }

    private fun moveBubble(event: MotionEvent) {
        when (event.action) {
            MotionEvent.ACTION_MOVE -> {
                alpha = 1f
                if (isInRemovableArea(event)) {
                    x = removableView.x
                    y = removableView.y
                    onMoveCallback?.invoke(this, false)
                } else {
                    x = max(area.left, min(event.rawX - width / 2, area.right))
                    y = max(area.top, min(event.rawY - height / 2, (area.bottom - height)))
                    onMoveCallback?.invoke(this, true)
                }
                if (parentVG.contains(bigBrotherContainerView)) bigBrotherContainerView.collapse()
            }
            MotionEvent.ACTION_UP -> {
                setOnTouchListener(null)
                fadeAnimation.start()
                if (isInRemovableArea(event)) onVortexKilled?.invoke()
            }
        }
    }

    private fun removableArea(event: MotionEvent) {
        when (event.action) {
            MotionEvent.ACTION_MOVE -> {
                if (isInRemovableArea(event)) {
                    removableView.scaleX = 1.2f
                    removableView.scaleY = 1.2f
                } else {
                    removableView.scaleX = 1f
                    removableView.scaleY = 1f
                }
            }
            MotionEvent.ACTION_UP -> {
                parentVG.removeView(removableView)
            }
        }
    }

    fun animateToCenter() {
        config.initialLocation.set(x, y)
        onMoveCallback?.invoke(this, false)
        if (parentVG.contains(bigBrotherContainerView)) return
        animate()
            .x(parentVG.width / 2f - width / 2)
            .y(statusBarHeight)
            .alpha(1f)
            .setDuration(config.animationDuration)
            .withEndAction {
                parentVG.addView(bigBrotherContainerView)
                bigBrotherContainerView.expand()
            }
            .start()
    }

    fun animateBack() {
        if (!parentVG.contains(bigBrotherContainerView)) return
        bigBrotherContainerView.collapse()
        animate()
            .x(config.initialLocation.x)
            .y(config.initialLocation.y)
            .alpha(config.disabledAlpha)
            .setStartDelay((config.animationDuration * 1.25).roundToLong())
            .setDuration(config.animationDuration)
            .withEndAction {
                onMoveCallback?.invoke(this, true)
            }
            .start()
    }

    fun setOnMoveCallback(action: ((BigBrotherView, Boolean) -> Unit)?) {
        onMoveCallback = action
    }

    fun setOnVortexKilled(action: (() -> Unit)?) {
        onVortexKilled = action
    }

    private fun isInRemovableArea(event: MotionEvent) = event.bounds.intersects(removableView.bounds)

    private val MotionEvent.bounds: RectF get() {
        val half = config.size / 2
        return RectF(
            rawX - half,
            rawY - half,
            rawX + half,
            rawY + half
        )
    }

    private fun RectF.intersects(bounds: RectF): Boolean {
        return intersects(bounds.left, bounds.top, bounds.right, bounds.bottom)
    }

    companion object {

        fun getOrCreate(activity: Activity, onVortexKilled: () -> Unit) =
            get(activity) ?:
            BigBrotherView(activity).apply {
                setOnVortexKilled(onVortexKilled)
            }

        fun getOrCreate(fragment: Fragment, onVortexKilled: () -> Unit) =
            get(fragment) ?: fragment.activity?.let { BigBrotherView(it).apply {
                setOnVortexKilled(onVortexKilled)
            } }

        fun get(activity: Activity) =
            activity.findViewById<BigBrotherView>(R.id.bigbrother)

        fun get(fragment: Fragment) =
            fragment.view?.findViewById<BigBrotherView>(R.id.bigbrother)
    }
}