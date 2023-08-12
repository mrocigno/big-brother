package br.com.mrocigno.bigbrother.log

import androidx.annotation.ColorRes
import androidx.recyclerview.widget.DiffUtil
import br.com.mrocigno.bigbrother.report.bbTrack
import br.com.mrocigno.bigbrother.report.model.ReportType
import br.com.mrocigno.bigbrother.common.R as CommonR

class LogEntryModel(
    val lvl: LogEntryType,
    val tag: String,
    val message: String? = null,
    val throwable: Throwable? = null
) {

    fun track() = runCatching {
        bbTrack(ReportType.LOG) {
            "> LOG - ${lvl.initial} - ${message ?: throwable?.message ?: "empty"}"
        }
    }

    override fun toString() = StringBuilder()
        .appendLine(tag)
        .appendLine(message)
        .appendLine(throwable?.message.toString())
        .toString()

    class Differ : DiffUtil.ItemCallback<LogEntryModel>() {
        override fun areItemsTheSame(oldItem: LogEntryModel, newItem: LogEntryModel) =
            newItem.tag == oldItem.tag
                && newItem.message == oldItem.message

        override fun areContentsTheSame(oldItem: LogEntryModel, newItem: LogEntryModel) = false
    }
}

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
    )
}