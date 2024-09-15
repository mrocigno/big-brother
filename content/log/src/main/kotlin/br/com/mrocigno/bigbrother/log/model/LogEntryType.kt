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
        CommonR.color.icon_negative,
        android.R.color.white,
        CommonR.color.icon_negative
    ),
    WARN(
        "W",
        CommonR.color.text_highlight,
        android.R.color.black,
        CommonR.color.text_highlight
    ),
    DEBUG(
        "D",
        CommonR.color.boy_red,
        android.R.color.white,
        CommonR.color.text_paragraph
    ),
    VERBOSE(
        "V",
        CommonR.color.text_title,
        CommonR.color.text_title_inverse,
        CommonR.color.text_paragraph
    ),
    INFO(
        "I",
        CommonR.color.text_hyperlink,
        android.R.color.white,
        CommonR.color.text_title
    ),
    ASSERT(
        "WTF",
        CommonR.color.icon_negative,
        android.R.color.white,
        CommonR.color.icon_negative
    )
}