package br.com.mrocigno.bigbrother.core

import android.graphics.PointF
import android.widget.FrameLayout
import androidx.annotation.DrawableRes
import br.com.mrocigno.bigbrother.common.R as CommonR

class BigBrotherConfig(
    val initialLocation: PointF = PointF(0f, 200f),
    val size: Int = 0,
    val disabledAlpha: Float = .5f,
    @DrawableRes val iconRes: Int = CommonR.drawable.bigbrother_ic_main,
) {

    fun initial(bigBrotherView: BigBrotherView) = bigBrotherView.apply {
        layoutParams = FrameLayout.LayoutParams(size, size).apply {
            x = initialLocation.x
            y = initialLocation.y
            alpha = disabledAlpha
        }
        setBackgroundResource(iconRes)
    }
}