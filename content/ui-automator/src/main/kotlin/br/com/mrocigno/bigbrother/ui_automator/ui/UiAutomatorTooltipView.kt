package br.com.mrocigno.bigbrother.ui_automator.ui

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
import androidx.core.view.isVisible
import br.com.mrocigno.bigbrother.common.provider.id
import br.com.mrocigno.bigbrother.common.utils.cast
import br.com.mrocigno.bigbrother.ui_automator.R
import br.com.mrocigno.bigbrother.ui_automator.finder.ViewFinder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import br.com.mrocigno.bigbrother.common.R as CR

class UiAutomatorTooltipView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val container: ViewGroup by id(R.id.automator_tooltip_container)
    private val title: AppCompatTextView by id(R.id.automator_tooltip_title)
    private val performClickButton: AppCompatButton by id(R.id.automator_tooltip_perform_click)
    private val performLongClickButton: AppCompatButton by id(R.id.automator_tooltip_perform_long_click)
    private val performScrollButton: AppCompatButton by id(R.id.automator_tooltip_perform_scroll)
    private val setTextLayout: TextInputLayout by id(R.id.automator_tooltip_set_text_layout)
    private val setText: TextInputEditText by id(R.id.automator_tooltip_set_text)

    private val diff = resources.getDimensionPixelOffset(CR.dimen.bb_size_s)

    init {
        inflate(context, R.layout.bigbrother_view_automator_tooltip, this)
        visibility = View.INVISIBLE
        layoutParams = LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
    }

    fun setOnPerformClick(listener: OnClickListener) {
        performClickButton.setOnClickListener(listener)
    }

    fun setOnPerformLongClick(listener: OnClickListener) {
        performLongClickButton.setOnClickListener(listener)
    }

    fun setOnPerformScroll(listener: OnClickListener) {
        performScrollButton.setOnClickListener(listener)
    }

    fun setOnSetText(listener: (String) -> Unit) {
        setTextLayout.setEndIconOnClickListener {
            listener(setText.text.toString())
        }
    }

    fun updateBackground(upArrow: Boolean = true, block: LayerDrawable.() -> Unit) {
        if (upArrow) container.setPadding(diff, diff * 2, diff, diff)
        else container.setPadding(diff, diff, diff, diff * 2)

        val res =
            if (upArrow) CR.drawable.bigbrother_content_background_dinamic_arrow_up
            else CR.drawable.bigbrother_content_background_dinamic_arrow_down
        container.background = ContextCompat.getDrawable(context, res)
            ?.cast(LayerDrawable::class)?.apply(block)
    }

    fun setup(viewFinder: ViewFinder) {
        title.text = viewFinder.name.orEmpty()
        performClickButton.isVisible = viewFinder.hasClickAction
        performLongClickButton.isVisible = viewFinder.hasLongClickAction
        setTextLayout.isVisible = viewFinder.isTextField
        performScrollButton.isVisible = viewFinder.isScrollable
    }
}