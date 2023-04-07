package br.com.mrocigno.bigbrother.core

import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Context
import android.graphics.RectF
import android.os.Build
import android.util.AttributeSet
import android.view.ContextThemeWrapper
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.graphics.toRect
import androidx.core.view.contains
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import br.com.mrocigno.bigbrother.common.utils.getNavigationBarHeight
import br.com.mrocigno.bigbrother.common.utils.statusBarHeight
import java.lang.Float.max
import java.lang.Float.min
import kotlin.math.roundToLong
import br.com.mrocigno.bigbrother.common.R as CommonR

class BigBrotherView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    private val config: BigBrotherConfig = Oceania.config
) : FrameLayout(
    ContextThemeWrapper(context, CommonR.style.Theme_BigBrother),
    attrs,
    defStyleAttr
) {

    val isExpanded: Boolean
        get() = oceaniaView.isExpanded

    private val activity get() = (context as ContextThemeWrapper).baseContext as FragmentActivity

    private val oceaniaView = OceaniaView(this)
    private val moveDuration = 200L

    private var downMillis: Long = 0L
    private var onVortexKilled: (() -> Unit)? = null

    private val statusBarHeight = activity.statusBarHeight.toFloat()
    private val navigationBarHeight = activity.getNavigationBarHeight().toFloat()
    private val parentVG get() = parent as ViewGroup
    private val move: Boolean get() = (System.currentTimeMillis() - downMillis) >= 100L
    private val fadeAnimation =
        ObjectAnimator.ofFloat(this, "alpha", 1f, config.disabledAlpha).apply {
            startDelay = 2000L
        }

    private val area by lazy {
        RectF(
            0f,
            statusBarHeight,
            (parentVG.width - width).toFloat(),
            parentVG.height - height - navigationBarHeight
        )
    }

    private val removableView by lazy {
        val padding = context.resources.getDimensionPixelSize(CommonR.dimen.spacing_s)
        FrameLayout(context).apply {
            setBackgroundResource(CommonR.drawable.bigbrother_remove_area_background)
            layoutParams = LayoutParams(config.size, config.size).apply {
                x = area.width() / 2
                y = area.bottom - padding
            }
        }
    }

    init {
        id = R.id.bigbrother
        config.initial(this)

        setOnTouchListener { _, event ->
            removableArea(event)
            moveBubble(event)

            if (event.action == MotionEvent.ACTION_UP && !move) performClick()
            true
        }

        setOnClickListener {
            fadeAnimation.cancel()
            if (oceaniaView.isExpanded) {
                animateBack()
            } else {
                animateToCenter()
            }
        }
    }

    private fun moveBubble(event: MotionEvent) {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                downMillis = System.currentTimeMillis()
                alpha = 1f
            }
            MotionEvent.ACTION_MOVE -> {
                if (!move) return
                if (isInRemovableArea(event)) {
                    x = removableView.x
                    y = removableView.y
                } else {
                    x = max(area.left, min(event.rawX - width / 2, area.right))
                    y = max(area.top, min(event.rawY - height / 2, area.bottom))
                }
                if (parentVG.contains(oceaniaView)) oceaniaView.collapse()
            }
            MotionEvent.ACTION_UP -> {
                fadeAnimation.start()
                if (!move) return
                if (isInRemovableArea(event)) onVortexKilled?.invoke()
            }
        }
    }

    private fun removableArea(event: MotionEvent) {
        if (!move) return
        when (event.action) {
            MotionEvent.ACTION_MOVE -> {
                if (!parentVG.contains(removableView)) {
                    parentVG.addView(removableView)
                }
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
        if (parentVG.contains(oceaniaView)) return
        animate()
            .x(parentVG.width / 2f - width / 2)
            .y(statusBarHeight)
            .setDuration(moveDuration)
            .withEndAction {
                parentVG.addView(oceaniaView)
                oceaniaView.expand()
            }
            .start()
    }

    fun animateBack() {
        if (!parentVG.contains(oceaniaView)) return
        oceaniaView.collapse()
        animate()
            .x(config.initialLocation.x)
            .y(config.initialLocation.y)
            .alpha(config.disabledAlpha)
            .setStartDelay((moveDuration * 1.25).roundToLong())
            .setDuration(moveDuration)
            .start()
    }

    fun setOnVortexKilled(action: () -> Unit) {
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

    private val View.bounds get() = RectF(x, y, x + width, y + height)

    private fun RectF.intersects(bounds: RectF): Boolean {
        return intersects(bounds.left, bounds.top, bounds.right, bounds.bottom)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            systemGestureExclusionRects = listOf(this.bounds.toRect())
        }
    }

    companion object {

        fun getOrCreate(activity: Activity, onVortexKilled: () -> Unit) =
            get(activity) ?: BigBrotherView(activity).apply {
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