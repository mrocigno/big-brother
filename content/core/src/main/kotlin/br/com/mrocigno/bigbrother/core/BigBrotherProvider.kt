package br.com.mrocigno.bigbrother.core

import android.app.Activity
import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.graphics.PointF
import android.net.Uri
import androidx.annotation.DrawableRes
import androidx.fragment.app.Fragment
import kotlin.reflect.KClass
import br.com.mrocigno.bigbrother.common.R as CommonR

abstract class BigBrotherProvider : ContentProvider() {

    protected abstract val isEnabled: Boolean
    @DrawableRes
    protected open val iconRes = CommonR.drawable.bigbrother_ic_main
    protected open val initialLocation = PointF(0f, 200f)
    protected open val disabledAlpha = .5f
    protected open val size get() =
        context?.resources?.getDimensionPixelSize(CommonR.dimen.bigbrother_size) ?: 0

    final override fun onCreate(): Boolean {
        if (!isEnabled) return false
        Oceania.config = BigBrotherConfig(
            initialLocation = initialLocation,
            size = size,
            disabledAlpha = disabledAlpha,
            iconRes = iconRes
        )
        setupPages()
        return true
    }

    abstract fun setupPages()

    fun addPage(name: String, creator: (BigBrotherView) -> Fragment) =
        Oceania.addPage(name, creator)

    fun addLocation(location: KClass<out Activity>, wrapper: CityWrapper.() -> Unit) =
        Oceania.addLocation(location, wrapper)

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