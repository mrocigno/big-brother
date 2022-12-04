package br.com.mrocigno.sandman

import android.graphics.PointF
import android.widget.FrameLayout
import androidx.annotation.DrawableRes
import androidx.annotation.StyleRes

class VortexConfig(
    val initialLocation: PointF = PointF(0f, 200f),
    val size: Int = 0,
    val disabledAlpha: Float = .5f,
    @StyleRes val themeRes: Int = R.style.TheDreaming,
    @DrawableRes val iconRes: Int = 0,
) {

    fun initial(vortexView: VortexView) = vortexView.apply {
        layoutParams = FrameLayout.LayoutParams(size, size).apply {
            x = initialLocation.x
            y = initialLocation.y
            alpha = disabledAlpha
        }
        setBackgroundResource(iconRes)
    }
}