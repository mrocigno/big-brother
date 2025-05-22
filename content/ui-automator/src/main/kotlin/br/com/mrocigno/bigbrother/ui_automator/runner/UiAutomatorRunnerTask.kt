package br.com.mrocigno.bigbrother.ui_automator.runner

import android.app.Activity
import br.com.mrocigno.bigbrother.core.BigBrotherTask
import br.com.mrocigno.bigbrother.ui_automator.findViewByXPath
import br.com.mrocigno.bigbrother.ui_automator.finder.ViewFinder
import br.com.mrocigno.bigbrother.ui_automator.model.UiAutomatorRecordModel
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference

class UiAutomatorRunnerTask(
    activity: Activity,
    private val steps: List<UiAutomatorRecordModel>
) : BigBrotherTask() {

    private val scope = CoroutineScope(Dispatchers.Main)
    private var currentContext = CompletableDeferred(activity.javaClass.name)
    private var currentActivity: WeakReference<Activity>? = WeakReference(activity)
    private var isPlaying: Boolean = false
    private var playIndex: Int = 0
    private val currentStep: UiAutomatorRecordModel get() = steps[playIndex]

    fun play() {
        // Add this task to big brother
        onCreate()

        // Ensure flags are in their initial state
        isPlaying = true
        playIndex = 0

        // Run
        scope.launch {
            run(playIndex)
        }
    }

    override fun onActivityResume(activity: Activity) {
        val contextName = activity.javaClass.name
        if (contextName == currentStep.context) {
            currentActivity = WeakReference(activity)
            currentContext.complete(contextName)
        }
    }

    private suspend fun run(index: Int) {
        if (!isPlaying || index >= steps.size) { removeMe(); return }

        with(currentStep) {
            while (context != currentContext.await()) {
                currentContext = CompletableDeferred()
            }
            val activity = currentActivity?.get()
            val view = activity?.findViewByXPath(identifier)

            if (activity != null && view != null) {
                val finder = ViewFinder.fromXPath(identifier, view)
                executeAction(activity, finder)
            }
            run(++playIndex)
        }
    }
}