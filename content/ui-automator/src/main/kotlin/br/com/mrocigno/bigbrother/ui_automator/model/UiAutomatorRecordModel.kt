package br.com.mrocigno.bigbrother.ui_automator.model

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
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
            RecordedAction.BACK_PRESSED -> {
                val dispatcher = (activity as? AppCompatActivity)?.onBackPressedDispatcher
                dispatcher?.onBackPressed() ?: activity.onBackPressed()
            }
            RecordedAction.SCROLL_Y -> finder.scroll(0f, value as Float, true)
        }
    }
}

enum class RecordedAction {
    CLICK,
    LONG_CLICK,
    SET_TEXT,
    BACK_PRESSED,
    SCROLL_Y
}
