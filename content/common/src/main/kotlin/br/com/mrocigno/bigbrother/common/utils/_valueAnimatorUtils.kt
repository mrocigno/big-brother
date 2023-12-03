package br.com.mrocigno.bigbrother.common.utils

import android.animation.ValueAnimator
import android.graphics.PointF

fun pointAnimator(vararg pointF: PointF) =
    ValueAnimator.ofObject({ fraction, start, end ->
        val pointStart = start as PointF
        val pointEnd = end as PointF

        val x = (pointEnd.x - pointStart.x) * fraction
        val y = (pointEnd.y - pointStart.y) * fraction
        PointF(pointStart.x + x, pointStart.y + y)
    }, *pointF)