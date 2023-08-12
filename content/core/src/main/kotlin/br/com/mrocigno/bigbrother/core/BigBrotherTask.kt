package br.com.mrocigno.bigbrother.core

import android.app.Activity
import android.content.ContentProvider
import android.content.ContentValues
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment

abstract class BigBrotherTask : ContentProvider() {

    protected open val priority: Int? = null

    open fun onActivityCreated(activity: Activity, bundle: Bundle?) = Unit

    open fun onActivityStarted(activity: Activity) = Unit

    open fun onActivityPaused(activity: Activity) = Unit

    open fun onActivityResume(activity: Activity) = Unit

    open fun onActivityStopped(activity: Activity) = Unit

    open fun onActivityDestroyed(activity: Activity) = Unit

    open fun onFragmentStarted(fragment: Fragment) = Unit

    open fun onFragmentStopped(fragment: Fragment) = Unit

    override fun onCreate(): Boolean {
        val priority = this.priority ?: BigBrother.tasks.size
        BigBrother.tasks.add(priority, this)
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ) = null

    override fun getType(uri: Uri) = null

    override fun insert(uri: Uri, values: ContentValues?) = null

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?) = 0

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ) = 0
 }