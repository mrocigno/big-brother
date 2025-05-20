package br.com.mrocigno.bigbrother.ui_automator.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.FrameLayout.LayoutParams.MATCH_PARENT
import androidx.core.view.isInvisible
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import br.com.mrocigno.bigbrother.common.utils.ifFalse
import br.com.mrocigno.bigbrother.common.utils.rootView
import br.com.mrocigno.bigbrother.common.utils.statusBarHeight
import br.com.mrocigno.bigbrother.common.utils.visible
import br.com.mrocigno.bigbrother.core.utils.getBigBrotherTask
import br.com.mrocigno.bigbrother.ui_automator.R
import br.com.mrocigno.bigbrother.ui_automator.UiAutomatorTask
import br.com.mrocigno.bigbrother.ui_automator.finder.ViewFinder
import kotlin.math.roundToInt

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
    private val overlayPaint = Paint().apply {
        color = Color.argb(180, 0, 0, 0)
        style = Paint.Style.FILL
    }
    private val clearPaint = Paint().apply {
        xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    }

    internal var clickActive = getBigBrotherTask(UiAutomatorTask::class)?.isDeepInspectActive ?: true
    private val location = IntArray(2)
    private val activity: Activity? get() = context as? Activity
    private val tooltip = UiAutomatorTooltipView(context)
    private var lastClickedView: ViewFinder? = null
        set(value) {
            field = value
            updateTooltip()
            invalidate()
        }

    init {
        layoutParams = LayoutParams(MATCH_PARENT, MATCH_PARENT)
        id = R.id.bigbrother_ui_automator
        setWillNotDraw(false)

        setupTooltip()

        setOnTouchListener { _, event ->
            if (!clickActive) return@setOnTouchListener false
            when (event.action) {
                MotionEvent.ACTION_UP -> {
                    lastClickedView =
                        if (lastClickedView != null) null
                        else ViewFinder.fromCoordinates(event.rawX, event.rawY, context.rootView!!)
                }
            }
            true
        }
    }

    private fun setupTooltip() {
        tooltip.setOnPerformClick {
            lastClickedView?.click()
            lastClickedView = null
        }
        tooltip.setOnPerformLongClick {
            lastClickedView?.longClick()
            lastClickedView = null
        }
        addView(tooltip)
    }

    fun setClickActive(active: Boolean) {
        clickActive = active.ifFalse {
            lastClickedView = null
        }
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
        val isArrowUp = y + tooltipHeight < height

        if (x < 0) x = 0
        if (x + tooltipWidth > width) x = width - tooltipWidth
        if (y + tooltipHeight > height) y = rect.top - tooltipHeight - yDiff
        if (y < activity!!.statusBarHeight) y = activity!!.statusBarHeight

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
            canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), overlayPaint)
            lastClickedView?.let {
                it.rect.offset(-location[0], -location[1])
                canvas.drawRect(it.rect, clearPaint)
                canvas.drawRect(it.rect, borderPaint)
            }
            canvas.restoreToCount(saved)
        } else {
            tooltip.visibility = View.INVISIBLE
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
