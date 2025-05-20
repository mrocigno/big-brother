package br.com.mrocigno.bigbrother.core

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.LayerDrawable
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.view.doOnLayout
import androidx.core.view.isNotEmpty
import androidx.fragment.app.Fragment
import br.com.mrocigno.bigbrother.common.utils.cast
import br.com.mrocigno.bigbrother.common.utils.gone
import br.com.mrocigno.bigbrother.common.utils.setColor
import br.com.mrocigno.bigbrother.common.utils.visible
import br.com.mrocigno.bigbrother.core.model.BigBrotherTooltipAction
import br.com.mrocigno.bigbrother.common.R as CR

@SuppressLint("ViewConstructor")
class BigBrotherTooltipView(
    private val vortex: BigBrotherView
) : LinearLayout(vortex.context) {

    private val diff = resources.getDimensionPixelOffset(CR.dimen.bb_size_s)

    init {
        id = R.id.bigbrother_tooltip
        orientation = HORIZONTAL
        layoutParams = LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        vortex.setOnMoveCallback { bb, isVisible ->
            if (isVisible) moveTooltip(bb)
            else gone()
        }
        vortex.doOnLayout { moveTooltip(vortex) }
        alpha = 0.7f
    }

    fun addAction(action: BigBrotherTooltipAction) {
        addView(action.render(context, isNotEmpty()))
    }

    private fun moveTooltip(bb: BigBrotherView) {
        visible()
        setPadding(diff, 0, 0, 0)
        measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED)

        val tooltipWidth = measuredWidth
        val tooltipHeight = measuredHeight
        var left = bb.x + bb.width
        var top = bb.y + (bb.height/2) - (measuredHeight/2)
        val isLeftArrow = left + tooltipWidth < (bb.area.width() + bb.width)

        if (left + tooltipWidth > (bb.area.right + bb.width)) left = bb.x - tooltipWidth
        if (top + tooltipHeight > bb.area.bottom) top = bb.area.bottom - tooltipHeight
        if (top < bb.area.top) top = bb.area.top

        x = left
        y = top

        val rect = with(bb) { bb.bounds }
        updateBackground(isLeftArrow) {
            val inset = ((rect.centerY() - y) - diff).toInt()

            setLayerInsetTop(0, inset)
            setLayerInsetTop(2, inset + 1)
            findDrawableByLayerId(CR.id.arrow).setColor(Color.WHITE)
            findDrawableByLayerId(CR.id.content).setColor(Color.WHITE)
        }
    }

    private fun updateBackground(leftArrow: Boolean = true, block: LayerDrawable.() -> Unit) {
        if (!leftArrow) setPadding(0, 0, diff, 0)
        val res =
            if (leftArrow) CR.drawable.bigbrother_content_background_dinamic_arrow_left
            else CR.drawable.bigbrother_content_background_dinamic_arrow_right
        background = ContextCompat.getDrawable(context, res)
            ?.cast(LayerDrawable::class)?.apply(block)
    }

    companion object {

        fun getOrCreate(activity: Activity) =
            (get(activity) ?: BigBrotherTooltipView(BigBrotherView.get(activity)))

        fun getOrCreate(fragment: Fragment) =
            (get(fragment) ?: BigBrotherView.get(fragment)?.let(::BigBrotherTooltipView))

        fun get(activity: Activity) =
            (activity.findViewById<BigBrotherTooltipView>(R.id.bigbrother_tooltip))

        fun get(fragment: Fragment) =
            (fragment.view?.findViewById<BigBrotherTooltipView>(R.id.bigbrother_tooltip))
    }
}