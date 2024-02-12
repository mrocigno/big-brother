package br.com.mrocigno.bigbrother.core.ui

import android.content.Context
import android.graphics.Color
import android.graphics.PointF
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.MotionEvent
import androidx.core.content.ContextCompat
import androidx.core.graphics.toPoint
import androidx.core.graphics.toRectF
import br.com.mrocigno.bigbrother.common.utils.centerAsPoint
import br.com.mrocigno.bigbrother.common.utils.point
import br.com.mrocigno.bigbrother.core.BigBrother.config
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt

class BigBrotherIconData(
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
    private lateinit var drawable: Drawable

    init { setDrawable(context, iconRes) }

    fun setDrawable(context: Context, iconRes: Int) {
        drawable = ContextCompat.getDrawable(context, iconRes) ?: ColorDrawable(Color.RED)
    }

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