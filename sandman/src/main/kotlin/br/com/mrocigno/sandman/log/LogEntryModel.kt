package br.com.mrocigno.sandman.log

import androidx.annotation.ColorRes
import androidx.recyclerview.widget.DiffUtil
import br.com.mrocigno.sandman.R
import br.com.mrocigno.sandman.report.ReportModel
import br.com.mrocigno.sandman.report.ReportModelType
import kotlinx.parcelize.Parcelize

@Parcelize
class LogEntryModel(
    val lvl: LogEntryType,
    val tag: String,
    val message: String? = null,
    val throwable: Throwable? = null
) : ReportModel(
    type = ReportModelType.LOG
) {

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
        R.color.icon_negative,
        android.R.color.white,
        R.color.icon_negative
    ),
    WARN(
        "W",
        R.color.text_highlight,
        android.R.color.black,
        R.color.text_highlight
    ),
    DEBUG(
        "D",
        R.color.boy_red,
        android.R.color.white,
        R.color.text_paragraph
    ),
    VERBOSE(
        "V",
        R.color.text_title,
        R.color.text_title_inverse,
        R.color.text_paragraph
    ),
    INFO(
        "I",
        R.color.text_hyperlink,
        android.R.color.white,
        R.color.text_title
    )
}