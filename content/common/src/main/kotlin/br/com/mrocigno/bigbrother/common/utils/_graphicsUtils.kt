package br.com.mrocigno.bigbrother.common.utils

import android.graphics.Paint
import android.graphics.Point
import android.graphics.PointF
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.RectF
import android.view.MotionEvent

fun Paint.cleaner() = apply {
    xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
}

fun RectF.centerAsPoint() =
    PointF(centerX(), centerY())

fun Rect.centerAsPoint() =
    Point(centerX(), centerY())

fun MotionEvent.point() =
    PointF(x, y)