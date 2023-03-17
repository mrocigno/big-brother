package br.com.mrocigno.sandman.utils

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import br.com.mrocigno.sandman.Sandman
import br.com.mrocigno.sandman.log.SandmanLog.Companion.tag

internal val lastClickPosition: PointF? get() =
    Sandman.dataLake["lastClick"] as? PointF

internal fun Canvas.drawClick(paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
    color = Color.RED
    style = Paint.Style.STROKE
    strokeWidth = 10f
}) {
    lastClickPosition?.run { drawCircle(x, y, 50f, paint) }
        ?: Sandman.tag().w("Cannot get last click position - did you implement sandman.report?")
}