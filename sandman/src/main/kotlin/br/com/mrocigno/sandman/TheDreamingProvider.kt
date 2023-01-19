package br.com.mrocigno.sandman

import android.app.Application
import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.graphics.PointF
import android.net.Uri
import androidx.annotation.DrawableRes
import androidx.annotation.StyleRes
import com.jakewharton.threetenabp.AndroidThreeTen

open class TheDreamingProvider : ContentProvider() {

    @DrawableRes
    protected open val iconRes = R.drawable.ic_sandman
    @StyleRes
    protected open val themeRes = R.style.TheDreaming
    protected open val initialLocation = PointF(0f, 200f)
    protected open val disabledAlpha = .5f
    protected open val size by lazy {
        context?.resources?.getDimensionPixelSize(R.dimen.vortex_size) ?: 0
    }

    override fun onCreate(): Boolean {
        AndroidThreeTen.init(context)
        TheDreaming.config = VortexConfig(
            initialLocation = initialLocation,
            size = size,
            disabledAlpha = disabledAlpha,
            iconRes = iconRes,
            themeRes = themeRes
        )
        Morpheus.init(context as Application)
        return true
    }

    override fun query(
        p0: Uri,
        p1: Array<out String>?,
        p2: String?,
        p3: Array<out String>?,
        p4: String?
    ): Cursor? = null

    override fun getType(p0: Uri): String? = null

    override fun insert(p0: Uri, p1: ContentValues?): Uri? = null

    override fun delete(p0: Uri, p1: String?, p2: Array<out String>?) = 0

    override fun update(p0: Uri, p1: ContentValues?, p2: String?, p3: Array<out String>?) = 0

}