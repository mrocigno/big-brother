package br.com.mrocigno.bigbrother.database.ui.table.filter

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.RotateDrawable
import android.transition.TransitionManager
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.transition.doOnEnd
import androidx.core.view.isInvisible
import br.com.mrocigno.bigbrother.common.CircularRevealTransition
import br.com.mrocigno.bigbrother.common.utils.cast
import br.com.mrocigno.bigbrother.common.utils.cleaner
import br.com.mrocigno.bigbrother.common.utils.getBounds
import br.com.mrocigno.bigbrother.common.utils.onMeasured
import br.com.mrocigno.bigbrother.database.R
import br.com.mrocigno.bigbrother.database.model.TableDump
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import br.com.mrocigno.bigbrother.common.R as CR

class FilterView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    refView: View = View(context),
    filterData: FilterData = FilterData(dump = TableDump())
) : FrameLayout(context, attrs, defStyleAttr) {

    //region Views
    private val content: ConstraintLayout get() = findViewById(R.id.filter_view_content)
    private val sortAz: AppCompatTextView get() = findViewById(R.id.filter_view_sort_az)
    private val sortZa: AppCompatTextView get() = findViewById(R.id.filter_view_sort_za)
    private val clear: AppCompatTextView get() = findViewById(R.id.filter_view_clear)
    private val confirm: AppCompatTextView get() = findViewById(R.id.filter_view_confirm)
    private val cancel: AppCompatTextView get() = findViewById(R.id.filter_view_cancel)
    private val searchLayout: TextInputLayout get() = findViewById(R.id.filter_view_search_layout)
    private val search: TextInputEditText get() = findViewById(R.id.filter_view_search)
    //endregion

    //region Draw holders
    private val refBounds = refView.getBounds()
    private val cleaner = Paint().cleaner()
    //endregion

    //region Listeners
    private var onConfirm: ((FilterData) -> Unit)? = null
    private var onCancel: (() -> Unit)? = null
    //endregion

    private var data: FilterData = filterData

    init {
        val paddingOffset = context.resources.getDimensionPixelOffset(CR.dimen.bb_spacing_s)

        setLayerType(LAYER_TYPE_SOFTWARE, null)
        inflate(context, R.layout.bigbrother_view_filter, this)
        setBackgroundColor(context.getColor(CR.color.bb_themed_transparent))
        setPadding(paddingOffset, refView.height, paddingOffset, paddingOffset)
        content.onMeasured {
            val headerCenter = refView.visibleCenter()

            background.cast(LayerDrawable::class).apply {
                val inset = ((headerCenter - (width / 2)) * 2).toInt()
                setLayerInsetLeft(0, inset)
                setLayerInsetLeft(2, inset)
                findDrawableByLayerId(CR.id.arrow).fixColor()
                findDrawableByLayerId(CR.id.content).fixColor()
            }
        }

        onMeasured {
            val transition = CircularRevealTransition(refView.visibleCenter())
            TransitionManager.beginDelayedTransition(parent as ViewGroup, transition)
            isInvisible = false
        }

        isInvisible = true
        setOnClickListener {
            val parent = this.parent as ViewGroup
            val transition = CircularRevealTransition(refView.visibleCenter())
            transition.doOnEnd { parent.removeView(this@FilterView) }
            TransitionManager.beginDelayedTransition(parent, transition)
            isInvisible = true
        }

        setupViews()
    }

    fun View.visibleCenter(): Float {
        val padding = context.resources.getDimension(CR.dimen.bb_spacing_s)
        val temp = x + (width / 2)
        return when {
            temp > content.width -> x + padding
            temp < padding -> padding
            else -> temp
        }
    }

    fun setOnConfirm(listener: (FilterData) -> Unit) {
        onConfirm = listener
    }

    fun setOnCancel(listener: () -> Unit) {
        onCancel = listener
    }

    private fun setupViews() {
        val selectedColor = context.getColor(CR.color.bb_background_tertiary)

        confirm.setOnClickListener {
            onConfirm()
        }

        cancel.setOnClickListener {
            onCancel()
        }

        clear.setOnClickListener {
            onConfirm?.invoke(FilterData(dump = data.dump))
            performClick()
        }

        if (data.sort == FilterSort.ASC) sortAz.setBackgroundColor(selectedColor)
        sortAz.setOnClickListener {
            data = data.copy(sort = FilterSort.ASC)
            onConfirm()
        }

        if (data.sort == FilterSort.DESC) sortZa.setBackgroundColor(selectedColor)
        sortZa.setOnClickListener {
            data = data.copy(sort = FilterSort.DESC)
            onConfirm()
        }

        search.setText(data.search)
        searchLayout.setEndIconOnClickListener {
            onConfirm()
        }
    }

    private fun onConfirm() {
        data = data.copy(search = search.text.toString().trim().takeIf { it.isNotBlank() })
        onConfirm?.invoke(data)
        performClick()
    }

    private fun onCancel() {
        performClick()
    }

    private fun Drawable.fixColor() {
        val color = context.getColor(CR.color.bb_background_secondary)
        when (this) {
            is RotateDrawable -> drawable?.fixColor()
            is GradientDrawable -> setColor(color)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawRect(refBounds, cleaner)
    }
}