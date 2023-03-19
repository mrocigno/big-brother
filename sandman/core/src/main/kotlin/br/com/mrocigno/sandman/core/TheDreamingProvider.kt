package br.com.mrocigno.sandman.core

import android.app.Activity
import android.app.Application
import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.graphics.PointF
import android.net.Uri
import androidx.annotation.DrawableRes
import androidx.fragment.app.Fragment
import com.jakewharton.threetenabp.AndroidThreeTen
import kotlin.reflect.KClass
import br.com.mrocigno.sandman.common.R as CommonR

abstract class TheDreamingProvider : ContentProvider() {

    @DrawableRes
    protected open val iconRes = CommonR.drawable.ic_sandman
    protected open val initialLocation = PointF(0f, 200f)
    protected open val disabledAlpha = .5f
    protected open val size by lazy {
        context?.resources?.getDimensionPixelSize(CommonR.dimen.vortex_size) ?: 0
    }

    final override fun onCreate(): Boolean {
        AndroidThreeTen.init(context)
        TheDreaming.config = VortexConfig(
            initialLocation = initialLocation,
            size = size,
            disabledAlpha = disabledAlpha,
            iconRes = iconRes
        )
        setupTheDreaming()
        Sandman.init(context as Application)
        return true
    }

    abstract fun setupTheDreaming()

    fun addNightmare(name: String, creator: (VortexView) -> Fragment) =
        TheDreaming.addNightmare(name, creator)

    fun addLocation(location: KClass<out Activity>, wrapper: DreamWrapper.() -> Unit) =
        TheDreaming.addLocation(location, wrapper)

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