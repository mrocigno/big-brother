package br.com.mrocigno.bigbrother.core.utils

import android.graphics.PointF

private val dataLake = mutableMapOf<String, Any?>()

var lastClickPosition: PointF?
    get() = dataLake["lastClick"] as? PointF
    set(value) { dataLake["lastClick"] = value }

var bbSessionId: Long
    get() = dataLake["sessionId"] as? Long ?: -1
    set(value) { dataLake["sessionId"] = value }