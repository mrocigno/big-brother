package br.com.mrocigno.bigbrother.ui_automator.ui

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import android.widget.FrameLayout.LayoutParams.MATCH_PARENT
import androidx.core.content.ContextCompat
import androidx.core.graphics.toRectF
import androidx.core.graphics.withTranslation
import androidx.core.view.isInvisible
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import br.com.mrocigno.bigbrother.common.utils.gone
import br.com.mrocigno.bigbrother.common.utils.ifFalse
import br.com.mrocigno.bigbrother.common.utils.rootView
import br.com.mrocigno.bigbrother.common.utils.statusBarHeight
import br.com.mrocigno.bigbrother.common.utils.visible
import br.com.mrocigno.bigbrother.core.utils.getBigBrotherTask
import br.com.mrocigno.bigbrother.ui_automator.R
import br.com.mrocigno.bigbrother.ui_automator.UiAutomatorHolder
import br.com.mrocigno.bigbrother.ui_automator.UiAutomatorTask
import br.com.mrocigno.bigbrother.ui_automator.finder.ViewFinder
import kotlin.math.roundToInt
import br.com.mrocigno.bigbrother.common.R as CR

@SuppressLint("ClickableViewAccessibility")
internal class UiAutomatorView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val borderPaint = Paint().apply {
        color = Color.RED
        style = Paint.Style.STROKE
        strokeWidth = 6f
    }
    private val darkOverlayPaint = Paint().apply {
        color = Color.argb(180, 0, 0, 0)
        style = Paint.Style.FILL
    }
    private val lightOverlayPaint = Paint().apply {
        color = Color.argb(100, 255, 255, 255)
        style = Paint.Style.FILL
    }
    private val clearPaint = Paint().apply {
        xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    }

    private val task = getBigBrotherTask(UiAutomatorTask::class) ?: error("UiAutomatorTask not found")
    private var clickActive = task.isDeepInspectActive
    private var isRecordingScroll: Boolean = false
    private val scrollOffset: PointF = PointF()
    private val location = IntArray(2)
    private val activity: Activity? get() = context as? Activity
    private val tooltip = UiAutomatorTooltipView(context)
    private var lastClickedView: ViewFinder? = null
        set(value) {
            field = value
            updateTooltip()
            invalidate()
        }

    private val swipeIcon by lazy {
        ContextCompat.getDrawable(context, CR.drawable.bigbrother_ic_swipe_vertical)?.apply {
            setBounds(0, 0, 100, 100)
            setTint(Color.BLACK)
        }
    }
    private val swipeAnimator = ValueAnimator.ofFloat(-100f, 100f).apply {
        interpolator = DecelerateInterpolator()
        duration = 1000L
        addUpdateListener { invalidate() }
        repeatMode = ValueAnimator.REVERSE
        repeatCount = ValueAnimator.INFINITE
    }

    init {
        layoutParams = LayoutParams(MATCH_PARENT, MATCH_PARENT)
        id = R.id.bigbrother_ui_automator
        setWillNotDraw(false)

        setupTooltip()

        var consumedY = -1f
        setOnTouchListener { _, event ->
            if (!clickActive) return@setOnTouchListener false
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    consumedY = event.rawY
                }
                MotionEvent.ACTION_MOVE -> {
                    if (!isRecordingScroll) return@setOnTouchListener false
                    lastClickedView?.scroll(scrollOffset.x, consumedY - event.rawY, false)
                    consumedY = event.rawY
                }
                MotionEvent.ACTION_UP -> {
                    val safeFinder = lastClickedView
                    if (isRecordingScroll && safeFinder != null) {
                        val scrollY = safeFinder.scrollY
                        val scrollX = safeFinder.scrollX
                        UiAutomatorHolder.recordScroll(activity!!, safeFinder, scrollY)
                        lastClickedView = null
                        isRecordingScroll = false
                        swipeAnimator.cancel()
                    } else {
                        val clickedView = ViewFinder.fromCoordinates(event.rawX, event.rawY, context.rootView!!)
                        lastClickedView = when {
                            lastClickedView == clickedView -> lastClickedView?.parent
                            lastClickedView != null -> null
                            else -> clickedView
                        }
                    }
                }
            }
            true
        }
    }

    fun setClickActive(active: Boolean) {
        clickActive = active.ifFalse {
            lastClickedView = null
        }
    }

    private fun setupTooltip() {
        tooltip.setOnPerformClick {
            lastClickedView?.click()
            UiAutomatorHolder.recordClick(activity!!, lastClickedView!!)
            lastClickedView = null
        }

        tooltip.setOnPerformLongClick {
            lastClickedView?.longClick()
            UiAutomatorHolder.recordLongClick(activity!!, lastClickedView!!)
            lastClickedView = null
        }

        tooltip.setOnPerformScroll {
            lastClickedView = lastClickedView?.scrollableParent
            isRecordingScroll = true
            swipeAnimator.start()
        }

        tooltip.setOnSetText {
            lastClickedView?.setText(it)
            UiAutomatorHolder.recordSetText(activity!!, lastClickedView!!, it)
            lastClickedView = null
        }
        addView(tooltip)
    }

    private fun updateTooltip() {
        val safeView = lastClickedView ?: run {
            tooltip.isInvisible = true
            return
        }

        tooltip.setup(safeView)
        tooltip.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED)

        val tooltipWidth = tooltip.measuredWidth
        val tooltipHeight = tooltip.measuredHeight

        val rect = safeView.rect
        rect.offset(-location[0], -location[1])

        val yDiff = borderPaint.strokeWidth.roundToInt()
        var x = rect.centerX() - (tooltipWidth / 2)
        var y = rect.bottom + yDiff
        var isArrowUp = y + tooltipHeight < height

        if (x < 0) x = 0
        if (x + tooltipWidth > width) x = width - tooltipWidth
        if (y + tooltipHeight > height) y = rect.top - tooltipHeight - yDiff
        if (y < activity!!.statusBarHeight) {
            y = activity!!.statusBarHeight
            isArrowUp = true
        }

        tooltip.visible()
        tooltip.updateLayoutParams<LayoutParams> {
            leftMargin = x
            topMargin = y
        }
        val diff = context.resources.displayMetrics.density * 10
        tooltip.updateBackground(isArrowUp) {
            val inset = ((rect.centerX() - x) - diff).toInt()

            setLayerInsetLeft(0, inset)
            setLayerInsetLeft(2, inset + 1)
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        getLocationOnScreen(location)
    }

    override fun onDraw(canvas: Canvas) {
        if (lastClickedView != null) {
            val saved = canvas.saveLayer(null, null)
            canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), darkOverlayPaint)
            lastClickedView?.let {
                it.rect.offset(-location[0], -location[1])
                canvas.drawRect(it.rect, clearPaint)
                canvas.drawRect(it.rect, borderPaint)

                if (isRecordingScroll && swipeIcon != null) {
                    canvas.drawRect(it.rect, lightOverlayPaint)
                    canvas.withTranslation {
                        val rectF = it.rect.toRectF()
                        val iconRect = checkNotNull(swipeIcon).bounds.toRectF()
                        translate(
                            rectF.centerX() - (iconRect.width() / 2),
                            rectF.centerY() - (iconRect.height() / 2) - swipeAnimator.animatedValue as Float
                        )
                        checkNotNull(swipeIcon).draw(canvas)
                    }
                }
            }
            canvas.restoreToCount(saved)
        } else {
            tooltip.gone()
        }
        super.onDraw(canvas)
    }

    companion object {

        fun getOrCreate(activity: Activity) =
            (get(activity) ?: UiAutomatorView(activity))

        fun getOrCreate(fragment: Fragment) =
            (get(fragment) ?: fragment.activity?.let { UiAutomatorView(it) })

        fun get(activity: Activity) =
            (activity.findViewById<UiAutomatorView>(R.id.bigbrother_ui_automator))

        fun get(fragment: Fragment) =
            (fragment.view?.findViewById<UiAutomatorView>(R.id.bigbrother_ui_automator))
    }
}
