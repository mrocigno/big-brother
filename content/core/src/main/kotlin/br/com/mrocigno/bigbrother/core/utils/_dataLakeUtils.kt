package br.com.mrocigno.bigbrother.core.utils

/**
 * Exposes common variables to third party developer
 */

import android.graphics.PointF
import br.com.mrocigno.bigbrother.common.utils.bbSessionId as commonBbSessionId
import br.com.mrocigno.bigbrother.common.utils.lastClickPosition as commonLastClickPosition

var lastClickPosition: PointF?
    get() = commonLastClickPosition
    set(value) { commonLastClickPosition = value }

var bbSessionId: Long
    get() = commonBbSessionId
    set(value) { commonBbSessionId = value.takeIf { it != -1L } ?: 0L }