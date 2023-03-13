package br.com.mrocigno.sandman.morpheus

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment

abstract class MorpheusTask {

    open fun onActivityCreated(activity: Activity, bundle: Bundle?) = Unit

    open fun onActivityStarted(activity: Activity) = Unit

    open fun onActivityPaused(activity: Activity) = Unit

    open fun onActivityDestroyed(activity: Activity) = Unit

    open fun onFragmentStarted(fragment: Fragment) = Unit

    open fun onFragmentStopped(fragment: Fragment) = Unit
}