package br.com.mrocigno.bigbrother.core

import android.graphics.PointF
import android.widget.FrameLayout
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import br.com.mrocigno.bigbrother.common.R as CommonR

/**
 * @param initialLocation initial position of the BigBrotherView
 * @param disabledAlpha alpha of the BigBrotherView when disabled
 * @param iconRes icon of the BigBrotherView
 * @param size size of the BigBrotherView
 * @param isClickRecorderEnabled enable/disable click recorder coordinates to indicate crash click location
 *          disabling it is useful when is needed to remove "bigbrother_click_observer" view, allowing to use Layout Inspector
 */
class BigBrotherConfig(
    var initialLocation: PointF = PointF(0f, 200f),
    var disabledAlpha: Float = .5f,
    @DrawableRes var iconRes: Int = CommonR.drawable.bigbrother_ic_main,
    @DimenRes private var sizeRes: Int = CommonR.dimen.bigbrother_size,
    var isClickRecorderEnabled: Boolean = true,
) {

    internal var size: Int = 0

    internal fun initial(bigBrotherView: BigBrotherView) = bigBrotherView.apply {
        size = resources.getDimensionPixelSize(sizeRes)
        layoutParams = FrameLayout.LayoutParams(size, size).apply {
            x = initialLocation.x
            y = initialLocation.y
            alpha = disabledAlpha
        }
        setBackgroundResource(iconRes)
    }
}