package br.com.mrocigno.bigbrother.ui_automator

import android.app.Activity
import android.os.Bundle
import br.com.mrocigno.bigbrother.common.utils.rootView
import br.com.mrocigno.bigbrother.core.BigBrotherTask

class UiAutomatorTask : BigBrotherTask() {

    override fun onActivityCreated(activity: Activity, bundle: Bundle?) {
        UiAutomatorView.getOrCreate(activity).run(activity.rootView::addView)
    }
}