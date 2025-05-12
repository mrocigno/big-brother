package br.com.mrocigno.bigbrother.report.ui

data class RecordActionModel(
    val resourceId: String,
    val order: Int,
    val viewAction: ViewAction
)

enum class ViewAction {
    CLICK
}
