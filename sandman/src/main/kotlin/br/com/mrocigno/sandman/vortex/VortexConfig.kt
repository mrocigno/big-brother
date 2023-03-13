package br.com.mrocigno.sandman.vortex

import android.graphics.PointF
import android.widget.FrameLayout
import androidx.annotation.DrawableRes
import br.com.mrocigno.sandman.R

class VortexConfig(
    val initialLocation: PointF = PointF(0f, 200f),
    val size: Int = 0,
    val disabledAlpha: Float = .5f,
    @DrawableRes val iconRes: Int = R.drawable.ic_sandman,
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