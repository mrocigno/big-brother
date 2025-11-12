package br.com.mrocigno.bigbrother.core

import android.app.Activity
import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import androidx.fragment.app.Fragment
import kotlin.reflect.KClass

abstract class BigBrotherProvider : ContentProvider() {

    protected abstract val isEnabled: Boolean

    final override fun onCreate(): Boolean {
        if (!isEnabled) return false
        setupPages()
        return true
    }

    abstract fun setupPages()

    fun addPage(name: String, creator: (BigBrotherView) -> Fragment) =
        BigBrother.addPage(name, creator)

    fun addPage(location: KClass<out Activity>, wrapper: ActivityPageWrapper.() -> Unit) =
        BigBrother.addPage(location, wrapper)

    fun addInterceptor(interceptorCreator: () -> BBInterceptor) =
        BigBrother.addInterceptor(interceptorCreator)

    final override fun query(
        p0: Uri,
        p1: Array<out String>?,
        p2: String?,
        p3: Array<out String>?,
        p4: String?
    ): Cursor? = null

    final override fun getType(p0: Uri): String? = null

    final override fun insert(p0: Uri, p1: ContentValues?): Uri? = null

    final override fun delete(p0: Uri, p1: String?, p2: Array<out String>?) = 0

    final override fun update(p0: Uri, p1: ContentValues?, p2: String?, p3: Array<out String>?) = 0

}