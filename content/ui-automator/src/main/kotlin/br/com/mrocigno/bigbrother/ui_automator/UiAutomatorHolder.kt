package br.com.mrocigno.bigbrother.ui_automator

import android.app.Activity
import br.com.mrocigno.bigbrother.ui_automator.finder.ViewFinder
import br.com.mrocigno.bigbrother.ui_automator.model.RecordedAction
import br.com.mrocigno.bigbrother.ui_automator.model.UiAutomatorRecordModel
import br.com.mrocigno.bigbrother.ui_automator.runner.UiAutomatorRunnerTask

object UiAutomatorHolder {

    val recordedViews = mutableListOf<UiAutomatorRecordModel>()

    fun recordClick(activity: Activity, finder: ViewFinder) =
        record(activity, finder, RecordedAction.CLICK)

    fun recordLongClick(activity: Activity, finder: ViewFinder) =
        record(activity, finder, RecordedAction.LONG_CLICK)

    fun recordSetText(activity: Activity, finder: ViewFinder, text: String) =
        record(activity, finder, RecordedAction.SET_TEXT, text)

    fun play(activity: Activity) {
        UiAutomatorRunnerTask(activity, recordedViews).play()
    }

    private fun record(
        activity: Activity,
        finder: ViewFinder,
        action: RecordedAction,
        value: Any? = null
    ) {
        recordedViews.add(
            UiAutomatorRecordModel(
                context = activity.javaClass.name,
                identifier = finder.identifier,
                action = action,
                value = value
            )
        )
    }
}