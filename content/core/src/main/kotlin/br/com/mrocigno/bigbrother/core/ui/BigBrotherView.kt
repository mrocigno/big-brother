package br.com.mrocigno.bigbrother.core.ui

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import android.graphics.RectF
import android.hardware.display.DisplayManager
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.Surface
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import androidx.activity.OnBackPressedCallback
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.graphics.toRect
import androidx.core.graphics.toRectF
import androidx.core.graphics.withSave
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.widget.ViewPager2
import br.com.mrocigno.bigbrother.common.utils.afterMeasure
import br.com.mrocigno.bigbrother.common.utils.centerAsPoint
import br.com.mrocigno.bigbrother.common.utils.cleaner
import br.com.mrocigno.bigbrother.common.utils.getNavigationBarHeight
import br.com.mrocigno.bigbrother.common.utils.gone
import br.com.mrocigno.bigbrother.common.utils.pointAnimator
import br.com.mrocigno.bigbrother.common.utils.statusBarHeight
import br.com.mrocigno.bigbrother.common.utils.visible
import br.com.mrocigno.bigbrother.core.BigBrother
import br.com.mrocigno.bigbrother.core.BigBrother.config
import br.com.mrocigno.bigbrother.core.BigBrotherWatchTask
import br.com.mrocigno.bigbrother.core.PageData
import br.com.mrocigno.bigbrother.core.R
import br.com.mrocigno.bigbrother.core.utils.getTask
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlin.math.roundToInt
import br.com.mrocigno.bigbrother.common.R as CR

@SuppressLint("ClickableViewAccessibility")
class BigBrotherView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    var isExpanded = false
    val pager: ViewPager2 get() = findViewById(R.id.td_dreams_pager)
    val tabHeader: TabLayout get() = findViewById(R.id.td_tab_layout)

    private var bubble = BigBrotherIconData(context)
    private val removeIcon = BigBrotherIconData(
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

    private val contentInsets: RectF = RectF()
    private val lastBubblePosition = PointF(bubble.position.x, bubble.position.y)
    private var isContentVisible = false
    private val pathSpace = 10f * density
    private val drawArea: RectF = RectF()
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
    private val onBackPressed = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            collapse()
            remove()
        }
    }

    init {
        id = R.id.bigbrother
        layoutParams = LayoutParams(MATCH_PARENT, MATCH_PARENT)
        setWillNotDraw(false)
        setLayerType(LAYER_TYPE_HARDWARE, null)

        setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_MOVE -> {
                    if (bubble.isMoving && isExpanded) collapse()
                }
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
            setupInsets()
            val strokeCompensation = strokePaint.strokeWidth / 2
            val left = 0f + contentInsets.left
            val top = statusBarHeight
            val right = width - contentInsets.right
            val bottom = height - contentInsets.bottom

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

            setupView()
        }
    }

    private fun setupView() {
        val padding = strokePaint.strokeWidth.roundToInt()
        val paddings = contentInsets.toRect()
        inflate(context, R.layout.bigbrother_content_layout, this)
        setPadding(
            paddings.left + padding,
            paddings.top,
            paddings.right + padding,
            paddings.bottom + padding
        )

        val list: MutableList<PageData> = mutableListOf()
        BigBrother.getPages(activity::class)?.let(list::addAll)
        list.addAll(BigBrother.getPages())

        pager.offscreenPageLimit = 1
        pager.adapter = BigBrotherFragmentAdapter(activity, list)
        TabLayoutMediator(tabHeader, pager) { tab, position ->
            tab.text = list[position].name
        }.attach()
    }

    private fun setupInsets() {
        val display = context
            .getSystemService(DisplayManager::class.java)
            .displays
            .firstOrNull() ?: return

        contentInsets.top = statusBarHeight + config.size + pathSpace
        val rotation =
            if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) 0
            else display.rotation

        when (rotation) {
            Surface.ROTATION_90 -> {
                contentInsets.left = 0f
                contentInsets.right = navigationBarHeight
                contentInsets.bottom = 0f
            }
            Surface.ROTATION_270 -> {
                contentInsets.left = navigationBarHeight
                contentInsets.right = 0f
                contentInsets.bottom = 0f
            }
            else -> {
                contentInsets.left = 0f
                contentInsets.right = 0f
                contentInsets.bottom = navigationBarHeight
            }
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
        if (isExpanded) collapse()
        else expand()
    }

    internal fun expand() {
        val set = AnimatorSet()
        val bubbleAnimation = bubbleAnimation(PointF(center.x - config.size / 2, drawArea.top))
        val contentAnimation = contentAnimation(false)

        bubbleAnimation.doOnEnd { bubble.setDrawable(context, CR.drawable.bigbrother_remove_area_background) }
        contentAnimation.doOnStart { tabHeader.visible(); pager.visible() }
        isExpanded = true
        lastBubblePosition.set(bubble.position)

        set.playSequentially(
            bubbleAnimation,
            contentAnimation
        )
        set.start()
        activity.onBackPressedDispatcher.addCallback(onBackPressed)
    }

    internal fun collapse() {
        val set = AnimatorSet()
        val contentAnimator = contentAnimation(true)

        contentAnimator.doOnEnd { tabHeader.gone(); pager.gone() }
        isExpanded = false

        set.doOnStart { bubble.setDrawable(context, config.iconRes) }
        set.playSequentially(
            contentAnimator,
            bubbleAnimation(lastBubblePosition)
        )
        set.start()
        onBackPressed.remove()
    }

    private fun bubbleAnimation(target: PointF): ValueAnimator {
        if (bubble.isMoving) return ValueAnimator.ofFloat(0f, 0f)
        val pointAnimator = pointAnimator(bubble.position, target)
        pointAnimator.addUpdateListener {
            val newPosition = it.animatedValue as PointF
            bubble.position.set(newPosition)
            invalidate()
        }
        return pointAnimator
    }

    private fun contentAnimation(collapse: Boolean): ValueAnimator {
        val animator =
            if (!collapse) ValueAnimator.ofFloat(contentInsets.top, drawArea.bottom)
            else ValueAnimator.ofFloat(drawArea.bottom, contentInsets.top)

        animator.doOnStart { isContentVisible = true }
        animator.addUpdateListener {
            cleanerPosition = it.animatedValue as Float
            invalidate()
        }
        animator.doOnEnd {
            cleanerPosition = -1f
            isContentVisible = !collapse
        }
        return animator
    }

    override fun dispatchDraw(canvas: Canvas?) {
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

        if (isContentVisible) contentPath?.run { canvas.drawPath(this, backgroundPaint) }
        super.dispatchDraw(canvas)
        if (isContentVisible) contentPath?.run { canvas.drawPath(this, strokePaint) }


        if (cleanerPosition != -1f) {
            canvas.drawLine(drawArea.left, cleanerPosition, drawArea.right, cleanerPosition, strokePaint)
            canvas.drawRect(0f, cleanerPosition, drawArea.right, drawArea.bottom, cleanerPaint)
        }
    }

    companion object {

        fun getOrCreate(activity: Activity) =
            get(activity) ?: BigBrotherView(activity)

        fun getOrCreate(fragment: Fragment) =
            get(fragment) ?: fragment.activity?.let { BigBrotherView(it) }

        fun get(activity: Activity) =
            activity.findViewById<BigBrotherView>(R.id.bigbrother)

        fun get(fragment: Fragment) =
            fragment.view?.findViewById<BigBrotherView>(R.id.bigbrother)
    }
}

