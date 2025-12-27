package br.com.mrocigno.bigbrother.ui_automator.runner

import android.app.Activity
import br.com.mrocigno.bigbrother.common.utils.contentView
import br.com.mrocigno.bigbrother.common.utils.toast
import br.com.mrocigno.bigbrother.core.BigBrotherTask
import br.com.mrocigno.bigbrother.ui_automator.finder.ViewFinder
import br.com.mrocigno.bigbrother.ui_automator.finder.findViewByXPath
import br.com.mrocigno.bigbrother.ui_automator.model.UiAutomatorRecordModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.yield
import java.lang.ref.WeakReference

class UiAutomatorRunnerTask(
    activity: Activity,
    private val steps: List<UiAutomatorRecordModel>
) : BigBrotherTask() {

    private val scope = CoroutineScope(Dispatchers.Main)
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
        }.invokeOnCompletion {
            currentActivity?.get()?.toast("canceled $it")
        }
    }

    override fun onActivityResume(activity: Activity) {
        val contextName = activity.javaClass.name
        if (contextName == currentStep.context) {
            currentActivity = WeakReference(activity)
        }
    }

    private suspend fun run(index: Int) {
        if (!isPlaying || index >= steps.size) { removeMe(); return }

        with(currentStep) {
            withTimeout(timeout) {
                while (currentActivity?.get()?.javaClass?.name != context) {
                    yield()
                    delay(100)
                }
            }

            val activity = currentActivity?.get()
            val view = activity?.contentView?.findViewByXPath(identifier, timeout)

            if (activity != null && view != null) {
                val finder = ViewFinder.fromView(view)
                if (finder.isReady(identifier, timeout)) executeAction(finder)
            }
            run(++playIndex)
        }
    }
}
