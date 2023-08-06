package br.com.mrocigno.bigbrother.report.model

import androidx.annotation.ColorRes
import br.com.mrocigno.bigbrother.common.R

internal enum class SessionStatus(@ColorRes val color: Int) {
    FINISHED(R.color.text_paragraph),
    CRASHED(R.color.icon_negative),
    RUNNING(R.color.text_highlight)
}