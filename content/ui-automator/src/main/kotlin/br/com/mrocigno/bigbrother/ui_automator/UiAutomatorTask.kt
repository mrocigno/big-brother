package br.com.mrocigno.bigbrother.ui_automator

import android.app.Activity
import androidx.activity.OnBackPressedCallback
import br.com.mrocigno.bigbrother.common.utils.rootView
import br.com.mrocigno.bigbrother.core.BigBrotherTask
import br.com.mrocigno.bigbrother.core.BigBrotherTooltipView
import br.com.mrocigno.bigbrother.core.model.BigBrotherTooltipAction
import br.com.mrocigno.bigbrother.ui_automator.ui.UiAutomatorView
import br.com.mrocigno.bigbrother.common.R as CR
import com.google.android.material.R as MR

class UiAutomatorTask : BigBrotherTask() {

    internal var isRecording: Boolean = true
    internal var isDeepInspectActive: Boolean = true

    override val priority: Int = 9

    private var onBackPressedCallback: OnBackPressedCallback? = null

    override fun onActivityResume(activity: Activity) {
        val automatorView = UiAutomatorView.get(activity)
        val tooltipView = BigBrotherTooltipView.get(activity)

        automatorView?.setClickActive(isDeepInspectActive)
        tooltipView?.applyActions(activity)

        if (isRecording && automatorView == null && tooltipView == null) {
            startRecording(activity)
        } else if (!isRecording && automatorView != null && tooltipView != null) {
            stopRecording(activity)
        }
    }

    fun startRecording(activity: Activity) {
        isRecording = true
        UiAutomatorView.getOrCreate(activity).run(activity.rootView::addView)
        BigBrotherTooltipView.getOrCreate(activity)
            .applyActions(activity)
            .run(activity.rootView::addView)
    }

    fun stopRecording(activity: Activity) {
        isRecording = false
        isDeepInspectActive = true
        onBackPressedCallback?.remove()
        onBackPressedCallback = null
        UiAutomatorView.get(activity)?.run(activity.rootView::removeView)
        BigBrotherTooltipView.get(activity)?.run(activity.rootView::removeView)
    }

    private fun BigBrotherTooltipView.applyActions(activity: Activity) = apply {
        removeAllViews()
        addAction(
            BigBrotherTooltipAction(
                icon = CR.drawable.bigbrother_ic_deep_inspect,
                tint = null,
                isSelected = isDeepInspectActive,
                action = {
                    val view = UiAutomatorView.get(activity) ?: return@BigBrotherTooltipAction
                    isDeepInspectActive = !isDeepInspectActive
                    view.setClickActive(isDeepInspectActive)
                    it.isSelected = isDeepInspectActive
                }
            )
        )
        addAction(
            BigBrotherTooltipAction(
                icon = CR.drawable.bigbrother_ic_stop,
                tint = activity.getColor(MR.color.design_default_color_error),
                action = {
                    stopRecording(activity)
                }
            )
        )
    }
}