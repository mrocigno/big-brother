package br.com.mrocigno.bigbrother.ui_automator

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import androidx.fragment.app.Fragment
import br.com.mrocigno.bigbrother.common.utils.decorView
import br.com.mrocigno.bigbrother.common.utils.field
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
        val teste = UiAutomatorView.getOrCreate(activity)
        teste.run(activity.rootView::addView)
        BigBrotherTooltipView.getOrCreate(activity)
            .applyActions(activity)
            .run(activity.rootView::addView)
    }

    fun startRecording(fragment: Fragment) {
        isRecording = true
        UiAutomatorView.getOrCreate(fragment).run(fragment.decorView!!::addView)
        BigBrotherTooltipView.getOrCreate(fragment)
            ?.applyActions(fragment.requireActivity())
            .run(fragment.decorView!!::addView)
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
        addAction(
            BigBrotherTooltipAction(
                icon = CR.drawable.bigbrother_ic_arrow_back,
                action = {
                    UiAutomatorHolder.recordBackPressed(activity)
                    if (activity is AppCompatActivity) {
                        activity.onBackPressedDispatcher.onBackPressed()
                    } else {
                        activity.onBackPressed()
                    }
                }
            )
        )
    }

    fun checkForDialogs(context: Context) {
        val views = Class.forName("android.view.WindowManagerGlobal")
            ?.getMethod("getInstance")?.invoke(null)
            ?.field<ArrayList<View>>("mViews")
            ?.ifEmpty { null }
            ?: return

        val filtered = views
            .filter { it.toString().contains(context::class.simpleName.orEmpty()) }
            .ifEmpty { views }
            .mapNotNull { it as? ViewGroup }
            .filter { it.children.toList().none { child -> child is UiAutomatorView } }

        filtered.forEach { decor ->
            val tea = UiAutomatorView(context)
            decor.addView(tea)
        }
    }
}