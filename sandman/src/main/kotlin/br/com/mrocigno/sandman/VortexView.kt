package br.com.mrocigno.sandman

import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Context
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import android.widget.FrameLayout
import androidx.core.view.contains
import androidx.core.view.updateLayoutParams
import androidx.dynamicanimation.animation.DynamicAnimation
import androidx.dynamicanimation.animation.FlingAnimation
import java.lang.Float.max
import java.lang.Float.min
import kotlin.math.roundToLong

class VortexView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    private val config: VortexConfig = TheDreaming.config
) : FrameLayout(context, attrs, defStyleAttr) {

    val theDreamingView = TheDreamingView(this)
    val moveDuration = 200L

    private val flingX = FlingAnimation(this, DynamicAnimation.X)
    private val flingY = FlingAnimation(this, DynamicAnimation.Y)
    private var velocity: VelocityTracker? = null
    private var downMillis: Long = 0L

    private val statusBarHeight = (context as Activity).statusBarHeight.toFloat()
    private val navigationBarHeight = (context as Activity).getNavigationBarHeight().toFloat()
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
        val padding = context.resources.getDimensionPixelSize(R.dimen.spacing_s)
        FrameLayout(context).apply {
            setBackgroundResource(R.drawable.remove_area_background)
            layoutParams = LayoutParams(config.size, config.size).apply {
                x = area.width() / 2
                y = area.bottom - padding
            }
        }
    }

    init {
        config.initial(this)

        setOnTouchListener { _, event ->
            removableArea(event)
            moveBubble(event)

            if (event.action == MotionEvent.ACTION_UP && !move) performClick()
            true
        }

        setOnClickListener {
            fadeAnimation.cancel()
            if (theDreamingView.isExpanded) {
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
                velocity = velocity ?: VelocityTracker.obtain()
                velocity?.addMovement(event)
                alpha = 1f
            }
            MotionEvent.ACTION_MOVE -> {
                if (!move) return
                if (isInRemovableArea(event)) {
                    updateLayoutParams<LayoutParams> {
                        x = removableView.x
                        y = removableView.y
                    }
                } else {
                    velocity?.apply {
                        addMovement(event)
                        computeCurrentVelocity(5000)

                        val pointerId = event.getPointerId(event.actionIndex)
                        flingX.setStartVelocity(getXVelocity(pointerId))
                        flingY.setStartVelocity(getYVelocity(pointerId))
                    }
                    updateLayoutParams<LayoutParams> {
                        x = max(area.left, min(event.rawX - width / 2, area.right))
                        y = max(area.top, min(event.rawY - height / 2, area.bottom))

                        flingX.setStartValue(x)
                        flingY.setStartValue(y)
                    }
                }
                if (parentVG.contains(theDreamingView)) theDreamingView.collapse()
            }
            MotionEvent.ACTION_UP -> {
                fadeAnimation.start()
                if (!move) return
                if (isInRemovableArea(event)) {
                     Morpheus.killVortex(this)
                } else {
                    flingX.setMinValue(area.left)
                    flingX.setMaxValue(area.right)
                    flingX.start()

                    flingY.setMinValue(area.top)
                    flingY.setMaxValue(area.bottom)
                    flingY.start()
                }
                velocity?.recycle()
                velocity = null
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
        if (parentVG.contains(theDreamingView)) return
        animate()
            .x(parentVG.width / 2f - width / 2)
            .y(statusBarHeight)
            .setInterpolator(OvershootInterpolator())
            .setDuration(moveDuration)
            .withEndAction {
                parentVG.addView(theDreamingView)
                theDreamingView.expand()
            }
            .start()
    }

    fun animateBack() {
        if (!parentVG.contains(theDreamingView)) return
        theDreamingView.collapse()
        animate()
            .x(config.initialLocation.x)
            .y(config.initialLocation.y)
            .alpha(config.disabledAlpha)
            .setInterpolator(OvershootInterpolator())
            .setStartDelay((moveDuration * 1.25).roundToLong())
            .setDuration(moveDuration)
            .start()
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
}