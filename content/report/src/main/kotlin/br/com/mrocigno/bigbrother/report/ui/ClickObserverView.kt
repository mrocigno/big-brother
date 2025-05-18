package br.com.mrocigno.bigbrother.report.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.util.AttributeSet
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import android.widget.FrameLayout.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import br.com.mrocigno.bigbrother.core.BigBrother
import br.com.mrocigno.bigbrother.report.R
import br.com.mrocigno.bigbrother.common.R as CR

@SuppressLint("ClickableViewAccessibility")
internal class ClickObserverView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var lastClickedView: View? = null
    private var lastClickedResourceName: String? = null
    private val borderPaint = Paint().apply {
        color = Color.RED
        style = Paint.Style.STROKE
        strokeWidth = 6f
    }
    private val overlayPaint = Paint().apply {
        color = Color.argb(180, 0, 0, 0) // Preto translúcido
        style = Paint.Style.FILL
    }
    private val clearPaint = Paint().apply {
        xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    }
    private var clickActive = true
    private var pressedView: View? = null
    private var performClickButton: AppCompatButton
    private val tooltipBalloon: LinearLayout
    private val tooltipText: TextView
    private val recordClickButton: AppCompatButton
    private val recordedActions = mutableListOf<RecordActionModel>()
    private val executeActionsButton: AppCompatButton

    init {
        layoutParams = LayoutParams(MATCH_PARENT, MATCH_PARENT)
        id = R.id.bigbrother_click_observer
        setWillNotDraw(false)

        // Tooltip balloon
        tooltipBalloon = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            visibility = View.INVISIBLE
            background = ContextCompat.getDrawable(context, CR.drawable.bigbrother_content_background)
            elevation = 12f
            val spacingS = context.resources.getDimensionPixelSize(CR.dimen.bb_spacing_s)
            setPadding(32, spacingS + 24, 32, 24)
            layoutParams = LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        }
        tooltipText = TextView(context).apply {
            setTextColor(ContextCompat.getColor(context, CR.color.bb_text_title))
            gravity = Gravity.CENTER
            textSize = 16f
        }
        performClickButton = AppCompatButton(context).apply {
            text = "performClick"
            setTextColor(ContextCompat.getColor(context, CR.color.bb_text_title))
            setOnClickListener { lastClickedView?.performClick() }
        }
        recordClickButton = AppCompatButton(context).apply {
            text = "record click"
            setTextColor(ContextCompat.getColor(context, CR.color.bb_text_title))
            setOnClickListener {
                val resourceId = lastClickedResourceName ?: return@setOnClickListener
                val order = recordedActions.size + 1
                recordedActions.add(
                    RecordActionModel(
                        resourceId = resourceId,
                        order = order,
                        viewAction = ViewAction.CLICK
                    )
                )
            }
        }
        tooltipBalloon.addView(tooltipText)
        tooltipBalloon.addView(performClickButton)
        tooltipBalloon.addView(recordClickButton)
        addView(tooltipBalloon)

        executeActionsButton = AppCompatButton(context).apply {
            text = "execute actions"
            setTextColor(ContextCompat.getColor(context, CR.color.bb_text_title))
            layoutParams = LayoutParams(WRAP_CONTENT, WRAP_CONTENT).apply {
                gravity = Gravity.END or Gravity.TOP
                topMargin = context.resources.getDimensionPixelSize(CR.dimen.bb_spacing_s)
                marginEnd = context.resources.getDimensionPixelSize(CR.dimen.bb_spacing_s)
            }
            setOnClickListener {
                val sortedActions = recordedActions.sortedBy { it.order }
                sortedActions.forEach { action ->
                    val id = getResIdByName(action.resourceId)
                    val v = (context as Activity).window.decorView.findViewById<View>(id)
                    when (action.viewAction) {
                        ViewAction.CLICK -> v?.performClick()
                    }
                }
            }
        }
        addView(executeActionsButton)

        setOnTouchListener { _, event ->
            if (!clickActive) return@setOnTouchListener false
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    pressedView = findViewAtCoordinates(event.rawX, event.rawY, (context as Activity).window.decorView)
                }
                MotionEvent.ACTION_UP -> {
                    val releasedView = findViewAtCoordinates(event.rawX, event.rawY, (context as Activity).window.decorView)
                    if (releasedView != null && releasedView == pressedView) {
                        if (releasedView == lastClickedView) {
                            lastClickedView = null
                            lastClickedResourceName = null
                        } else {
                            val resourceIdName = runCatching { releasedView.id.run(context.resources::getResourceEntryName) }.getOrNull()
                            lastClickedView = releasedView
                            lastClickedResourceName = resourceIdName
                        }
                        updateTooltipBalloonPosition()
                        invalidate()
                    }
                    pressedView = null
                }
            }
            if (event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_DOWN) {
                updateTooltipBalloonPosition()
            }
            true
        }
    }

    private fun updateTooltipBalloonPosition() {
        if (lastClickedView == null || lastClickedResourceName.isNullOrEmpty()) {
            tooltipBalloon.visibility = View.INVISIBLE
            return
        }
        tooltipText.text = lastClickedResourceName
        tooltipBalloon.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED)
        val tooltipWidth = tooltipBalloon.measuredWidth
        val tooltipHeight = tooltipBalloon.measuredHeight
        val lp = tooltipBalloon.layoutParams as LayoutParams
        lp.width = WRAP_CONTENT
        lp.height = WRAP_CONTENT
        var left = (width - tooltipWidth) / 2
        val rect = Rect()
        lastClickedView?.getGlobalVisibleRect(rect)
        val location = IntArray(2)
        getLocationOnScreen(location)
        rect.offset(-location[0], -location[1])
        var top = rect.bottom + 16 // 16px abaixo da view
        // Ajuste para não sair para fora da tela
        if (left < 0) left = 0
        if (left + tooltipWidth > width) left = width - tooltipWidth
        if (top + tooltipHeight > height) top = height - tooltipHeight
        if (top < 0) top = 0
        lp.leftMargin = left
        lp.topMargin = top
        tooltipBalloon.layoutParams = lp
        tooltipBalloon.visibility = View.VISIBLE
    }

    override fun onDraw(canvas: Canvas) {
        if (lastClickedView != null) {
            val saved = canvas.saveLayer(null, null)
            canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), overlayPaint)
            lastClickedView?.let { view ->
                val rect = Rect()
                view.getGlobalVisibleRect(rect)
                val location = IntArray(2)
                getLocationOnScreen(location)
                rect.offset(-location[0], -location[1])
                canvas.drawRect(rect, clearPaint)
                canvas.drawRect(rect, borderPaint)
            }
            canvas.restoreToCount(saved)
        }
        if (lastClickedView == null) {
            tooltipBalloon.visibility = View.INVISIBLE
        }
        super.onDraw(canvas)
    }

    private fun getResIdByName(name: String): Int {
        val rIdClass = Class.forName("${context.packageName}.R\$id")
        return try {
            rIdClass.getDeclaredField(name).getInt(null)
        } catch (e: Exception) {
            0
        }
    }

    companion object {

        private fun findViewAtCoordinates(x: Float, y: Float, root: View): View? {
            if (root is ClickObserverView) return null
            val rect = Rect()
            if (!root.getGlobalVisibleRect(rect) || !rect.contains(x.toInt(), y.toInt())) return null
            if (root is ViewGroup) {
                for (i in 0 until root.childCount) {
                    val child = root.getChildAt(i)
                    val found = findViewAtCoordinates(x, y, child)
                    if (found != null) return found
                }
            }
            return root
        }

        fun getOrCreate(activity: Activity) =
            (get(activity) ?: ClickObserverView(activity))
                .takeIf { BigBrother.config.isClickRecorderEnabled }

        fun getOrCreate(fragment: Fragment) =
            (get(fragment) ?: fragment.activity?.let { ClickObserverView(it) })
                .takeIf { BigBrother.config.isClickRecorderEnabled }

        fun get(activity: Activity) =
            (activity.findViewById<ClickObserverView>(R.id.bigbrother_click_observer))
                .takeIf { BigBrother.config.isClickRecorderEnabled }

        fun get(fragment: Fragment) =
            (fragment.view?.findViewById<ClickObserverView>(R.id.bigbrother_click_observer))
                .takeIf { BigBrother.config.isClickRecorderEnabled }
    }
}
