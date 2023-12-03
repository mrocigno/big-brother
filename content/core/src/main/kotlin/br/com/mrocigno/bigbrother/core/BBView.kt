package br.com.mrocigno.bigbrother.core

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.content.ContextCompat
import androidx.core.graphics.toPoint
import androidx.core.graphics.toRectF
import androidx.core.graphics.withSave
import androidx.fragment.app.FragmentActivity
import br.com.mrocigno.bigbrother.common.utils.afterMeasure
import br.com.mrocigno.bigbrother.common.utils.centerAsPoint
import br.com.mrocigno.bigbrother.common.utils.cleaner
import br.com.mrocigno.bigbrother.common.utils.getNavigationBarHeight
import br.com.mrocigno.bigbrother.common.utils.point
import br.com.mrocigno.bigbrother.common.utils.pointAnimator
import br.com.mrocigno.bigbrother.common.utils.statusBarHeight
import br.com.mrocigno.bigbrother.core.BigBrother.config
import br.com.mrocigno.bigbrother.core.utils.getTask
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt
import br.com.mrocigno.bigbrother.common.R as CR

@SuppressLint("ClickableViewAccessibility")
class BBView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val bubble = BBIconData(context)
    private val removeIcon = BBIconData(
        context,
        CR.drawable.bigbrother_remove_area_background,
        PointF()
    )

    private val density = context.resources.displayMetrics.density
    private val activity get() = context as FragmentActivity
    private val statusBarHeight = activity.statusBarHeight.toFloat()
    private val navigationBarHeight = activity.getNavigationBarHeight().toFloat()
    private val center: PointF = PointF()
    private val isBubbleOnRemoveArea
        get() = removeIcon.isOnThreshold(bubble.iconBounds.toRectF().centerAsPoint())

    private var isContentVisible = false
    private val pathSpace = 10f * density
    private val drawArea = RectF()
    private var contentPath: Path? = null
    private var cleanerPosition = -1f
    private val cleanerPaint = Paint().cleaner()
    private val strokePaint = Paint().apply {
        color = Color.BLACK
        style = Paint.Style.STROKE
        strokeWidth = 2f * density
    }
    private val backgroundPaint = Paint().apply {
        color = context.getColor(CR.color.background)
        style = Paint.Style.FILL
    }

    init {
        id = R.id.bigbrother
        layoutParams = LayoutParams(MATCH_PARENT, MATCH_PARENT)
        setWillNotDraw(false)

        setLayerType(LAYER_TYPE_HARDWARE, null)

        setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_UP -> {
                    if (!bubble.isMoving) onIconClick()
                    if (isBubbleOnRemoveArea) onRemoveBubble()
                    invalidate()
                }
            }

            bubble.computeTouch(event) {
                invalidate()
            }
        }

        afterMeasure {
            val strokeCompensation = strokePaint.strokeWidth / 2
            val left = 0f
            val top = statusBarHeight
            val right = width.toFloat()
            val bottom = height - navigationBarHeight

            drawArea.set(left, top, right, bottom)
            bubble.drawArea.set(drawArea)
            center.set(drawArea.centerAsPoint())
            removeIcon.position.set(center.x - config.size / 2f, drawArea.bottom - (removeIcon.totalThreshold * 2))
            removeIcon.threshold = config.size.toFloat()
            contentPath = createPathBackground(
                left + strokeCompensation,
                top + strokeCompensation,
                right - strokeCompensation,
                bottom - strokeCompensation
            )
        }
    }

    private fun createPathBackground(left: Float, top: Float, right: Float, bottom: Float) = Path().apply {
        val startY = top + config.size
        val startX = center.x

        moveTo(startX, startY)
        lineTo(startX - pathSpace, startY + pathSpace)

        // left-top corner
        lineTo(left + pathSpace, startY + pathSpace)
        arcTo(
            left, startY + pathSpace,
            left + pathSpace * 2f, startY + pathSpace * 3f,
            270f, -90f, false
        )

        // left-bottom corner
        lineTo(left, bottom - pathSpace)
        arcTo(
            left, bottom - pathSpace * 2,
            left + pathSpace * 2, bottom,
            180f, -90f, false
        )

        // right-bottom corner
        lineTo(right - pathSpace, bottom)
        arcTo(
            right - pathSpace * 2, bottom - pathSpace * 2,
            right, bottom,
            90f, -90f, false
        )

        // right-top corner
        lineTo(right, startY + pathSpace * 2)
        arcTo(
            right - pathSpace * 2, startY + pathSpace,
            right, startY + pathSpace * 3,
            0f, -90f, false
        )

        lineTo(startX + pathSpace, startY + pathSpace)
        lineTo(startX, startY)
        close()
    }

    private fun onRemoveBubble() {
        val parentViewGroup = parent as ViewGroup
        getTask(BigBrotherWatchTask::class)?.kill(parentViewGroup)
    }

    private fun onIconClick() {
        val set = AnimatorSet()
        set.playSequentially(
            bubbleAnimation(),
            contentAnimation()
        )
        set.start()
    }

    private fun bubbleAnimation(): ValueAnimator {
        val centerTop = PointF(center.x - config.size / 2, drawArea.top)
        val pointAnimator = pointAnimator(bubble.position, centerTop)
        pointAnimator.addUpdateListener {
            val newPosition = it.animatedValue as PointF
            bubble.position.set(newPosition)
            invalidate()
        }
        return pointAnimator
    }

    private fun contentAnimation(): ValueAnimator {
        val init = drawArea.top + config.size + pathSpace
        val animator = ValueAnimator.ofFloat(init, drawArea.bottom)
        animator.doOnStart { isContentVisible = true }
        animator.addUpdateListener {
            cleanerPosition = it.animatedValue as Float
            invalidate()
        }
        animator.doOnEnd { cleanerPosition = -1f }
        return animator
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas ?: return

        canvas.withSave {
            removeIcon.icon.run {
                val point = removeIcon.iconBounds.toRectF().centerAsPoint()
                if (isBubbleOnRemoveArea) scale(1.3f, 1.3f, point.x, point.y)
                if (bubble.isMoving) draw(canvas)
            }
        }

        bubble.icon.run {
            if (isBubbleOnRemoveArea) bounds = removeIcon.iconBounds
            draw(canvas)
        }

        if (isContentVisible) {
            contentPath?.run {
                canvas.drawPath(this, backgroundPaint)
                canvas.drawPath(this, strokePaint)
            }
        }

        if (cleanerPosition != -1f) {
            canvas.drawRect(0f, cleanerPosition, drawArea.right, drawArea.bottom, cleanerPaint)
        }
    }
}

class BBIconData(
    context: Context,
    iconRes: Int = config.iconRes,
    initialPosition: PointF = config.initialLocation
) {

    private var downMillis: Long = 0
    var isMoving: Boolean = false
    val position = initialPosition
    val drawArea: RectF = RectF()
    val iconBounds: Rect get() {
        val point = position.toPoint()
        return Rect(
            point.x,
            point.y,
            point.x + config.size,
            point.y + config.size
        )
    }
    val icon: Drawable get() = drawable.apply {
        bounds = iconBounds
    }

    // To increase click area
    var threshold: Float = 20f
    val totalThreshold: Float get() = config.size / 2f + threshold
    private val drawable = ContextCompat.getDrawable(context, iconRes)
        ?: ColorDrawable(Color.RED)

    fun isOnThreshold(event: PointF): Boolean {
        val point = iconBounds.toRectF().centerAsPoint()
        val distance = sqrt(
            (event.x - point.x).toDouble().pow(2) + (event.y - point.y).toDouble().pow(2)
        ).toFloat()
        return distance <= totalThreshold
    }

    fun computeTouch(event: MotionEvent, onMove: () -> Unit): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (!isOnThreshold(event.point())) return false
                downMillis = System.currentTimeMillis()
            }
            MotionEvent.ACTION_MOVE -> {
                isMoving = downMillis != 0L && (System.currentTimeMillis() - downMillis) >= 100L
                if (isMoving) {
                    position.x = max(drawArea.left, min(event.rawX - config.size / 2, drawArea.right))
                    position.y = max(drawArea.top, min(event.rawY - config.size / 2, drawArea.bottom - config.size))
                    onMove()
                }
            }
            MotionEvent.ACTION_UP -> {
                downMillis = 0L
                isMoving = false
            }
        }
        return downMillis != 0L
    }
}