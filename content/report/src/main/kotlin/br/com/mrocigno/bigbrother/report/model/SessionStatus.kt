package br.com.mrocigno.bigbrother.report.model

import androidx.annotation.ColorRes
import br.com.mrocigno.bigbrother.common.R

enum class SessionStatus(@ColorRes val color: Int) {
    FINISHED(R.color.bb_text_paragraph),
    CRASHED(R.color.bb_icon_negative),
    RUNNING(R.color.bb_text_highlight)
}