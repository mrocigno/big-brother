package br.com.mrocigno.bigbrother.core.utils

import android.graphics.PointF
import kotlin.random.Random

private val dataLake = mutableMapOf<String, Any?>()
private val randomSession by lazy {
    Random(System.currentTimeMillis()).nextLong(10000,99999)
}

var lastClickPosition: PointF?
    get() = dataLake["lastClick"] as? PointF
    set(value) { dataLake["lastClick"] = value }

var bbSessionId: Long
    get() = dataLake["sessionId"] as? Long ?: randomSession
    set(value) { dataLake["sessionId"] = value.takeIf { it != -1L } }