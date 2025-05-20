package br.com.mrocigno.bigbrother.core.model

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.View.OnClickListener
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import br.com.mrocigno.bigbrother.common.utils.applyIf
import br.com.mrocigno.bigbrother.common.R as CR

data class BigBrotherTooltipAction(
    @DrawableRes
    val icon: Int = CR.drawable.bigbrother_ic_bug,
    @ColorInt
    val tint: Int? = Color.BLACK,
    val isSelected: Boolean = true,
    val isVisible: Boolean = true,
    val action: OnClickListener = OnClickListener { _ -> }
) {

    fun render(context: Context, applyMargin: Boolean = false) = AppCompatImageView(context).apply {
        val padding = resources.getDimensionPixelOffset(CR.dimen.bb_size_s)
        setPadding(padding)
        isSelected = this@BigBrotherTooltipAction.isSelected
        foreground = ContextCompat.getDrawable(context, CR.drawable.bigbrother_ripple_dark)
        setImageResource(icon)
        imageTintList = tint?.run(ColorStateList::valueOf)
        setOnClickListener(action)
        layoutParams = LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT).applyIf(applyMargin) {
            marginStart = -padding
        }
    }
}
