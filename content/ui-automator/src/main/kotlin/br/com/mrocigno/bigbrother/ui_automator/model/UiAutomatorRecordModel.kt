package br.com.mrocigno.bigbrother.ui_automator.model

import android.app.Activity
import br.com.mrocigno.bigbrother.ui_automator.finder.ViewFinder

data class UiAutomatorRecordModel(
    val context: String = "",
    val action: RecordedAction,
    val identifier: String = "",
    val value: Any? = null
) {

    fun executeAction(activity: Activity, finder: ViewFinder) {
        when (action) {
            RecordedAction.CLICK -> finder.click()
            RecordedAction.LONG_CLICK -> finder.longClick()
            RecordedAction.SET_TEXT -> finder.setText(value.toString())
        }
    }
}

enum class RecordedAction {
    CLICK,
    LONG_CLICK,
    SET_TEXT,
}
