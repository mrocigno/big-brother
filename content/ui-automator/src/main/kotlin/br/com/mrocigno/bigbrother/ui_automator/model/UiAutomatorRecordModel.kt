package br.com.mrocigno.bigbrother.ui_automator.model

import br.com.mrocigno.bigbrother.ui_automator.finder.ViewFinder

data class UiAutomatorRecordModel(
    val context: String = "",
    val action: RecordedAction,
    val identifier: String = "",
    val value: Any? = null,
    val timeout: Long = 15000
) {

    fun executeAction(finder: ViewFinder) {
        when (action) {
            RecordedAction.CLICK -> finder.click()
            RecordedAction.LONG_CLICK -> finder.longClick()
            RecordedAction.SET_TEXT -> finder.setText(value.toString())
            RecordedAction.SCROLL_Y -> finder.scroll(0f, value as Float, true)
        }
    }
}

enum class RecordedAction {
    CLICK,
    LONG_CLICK,
    SET_TEXT,
    SCROLL_Y
}
