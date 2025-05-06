package br.com.mrocigno.bigbrother.log.model

import androidx.annotation.ColorRes
import br.com.mrocigno.bigbrother.common.R as CommonR

enum class LogEntryType(
    val initial: String,
    @ColorRes val bgColor: Int,
    @ColorRes val fgColor: Int,
    @ColorRes val textColor: Int,
) {
    ERROR(
        "E",
        CommonR.color.bb_icon_negative,
        android.R.color.white,
        CommonR.color.bb_icon_negative
    ),
    WARN(
        "W",
        CommonR.color.bb_text_highlight,
        android.R.color.black,
        CommonR.color.bb_text_highlight
    ),
    DEBUG(
        "D",
        CommonR.color.bb_boy_red,
        android.R.color.white,
        CommonR.color.bb_text_paragraph
    ),
    VERBOSE(
        "V",
        CommonR.color.bb_text_title,
        CommonR.color.bb_text_title_inverse,
        CommonR.color.bb_text_paragraph
    ),
    INFO(
        "I",
        CommonR.color.bb_text_hyperlink,
        android.R.color.white,
        CommonR.color.bb_text_title
    ),
    ASSERT(
        "WTF",
        CommonR.color.bb_icon_negative,
        android.R.color.white,
        CommonR.color.bb_icon_negative
    )
}