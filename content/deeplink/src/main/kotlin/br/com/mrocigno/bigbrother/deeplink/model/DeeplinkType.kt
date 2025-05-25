package br.com.mrocigno.bigbrother.deeplink.model

import androidx.annotation.ColorRes
import br.com.mrocigno.bigbrother.common.R as CR

internal enum class DeeplinkType(@ColorRes val color: Int) {
    INTERNAL(CR.color.bb_licorice),
    EXTERNAL(CR.color.bb_boy_red),
    UNKNOWN(CR.color.bb_divider)
}
