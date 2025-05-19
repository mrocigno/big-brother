package br.com.mrocigno.bigbrother.ui_automator

import android.content.Context
import android.graphics.drawable.LayerDrawable
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import br.com.mrocigno.bigbrother.common.provider.id
import br.com.mrocigno.bigbrother.common.utils.cast
import br.com.mrocigno.bigbrother.common.R as CR

class UiAutomatorTooltipView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val container: ViewGroup by id(R.id.automator_tooltip_container)
    private val title: AppCompatTextView by id(R.id.automator_tooltip_title)
    private val performClickButton: AppCompatButton by id(R.id.automator_tooltip_perform_click)

    init {
        inflate(context, R.layout.bigbrother_view_tooltip, this)
        visibility = View.INVISIBLE
        layoutParams = LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
    }

    fun setOnPerformClickClick(onClickListener: OnClickListener) =
        performClickButton.setOnClickListener(onClickListener)

    fun setTitle(text: String) {
        title.text = text
    }

    fun updateBackground(upArrow: Boolean = true, block: LayerDrawable.() -> Unit) {
        val res =
            if (upArrow) CR.drawable.bigbrother_content_background_dinamic_arrow_up
            else CR.drawable.bigbrother_content_background_dinamic_arrow_down
        container.background = ContextCompat.getDrawable(context, res)
            ?.cast(LayerDrawable::class)?.apply(block)
    }
}