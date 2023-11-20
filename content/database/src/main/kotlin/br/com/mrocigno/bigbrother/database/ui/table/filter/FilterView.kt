package br.com.mrocigno.bigbrother.database.ui.table.filter

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
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
import br.com.mrocigno.bigbrother.common.utils.getBounds
import br.com.mrocigno.bigbrother.common.utils.onMeasured
import br.com.mrocigno.bigbrother.database.R
import br.com.mrocigno.bigbrother.database.model.TableDump
import br.com.mrocigno.bigbrother.common.R as CR

class FilterView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    refView: View = View(context),
    dump: TableDump = TableDump()
) : FrameLayout(context, attrs, defStyleAttr) {

    //region Views
    private val content: ConstraintLayout get() = findViewById(R.id.filter_view_content)
    private val sortAz: AppCompatTextView get() = findViewById(R.id.filter_view_sort_az)
    private val sortZa: AppCompatTextView get() = findViewById(R.id.filter_view_sort_za)
    private val confirm: AppCompatTextView get() = findViewById(R.id.filter_view_confirm)
    private val cancel: AppCompatTextView get() = findViewById(R.id.filter_view_cancel)
    //endregion

    //region Draw holders
    private val refBounds = refView.getBounds()
    private val cleaner = Paint().apply {
        xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    }
    //endregion

    //region Listeners
    private var onConfirm: ((String) -> Unit)? = null
    private var onCancel: (() -> Unit)? = null
    //endregion

    init {
        val padding = context.resources.getDimension(CR.dimen.spacing_s)
        val paddingOffset = context.resources.getDimensionPixelOffset(CR.dimen.spacing_s)

        setLayerType(LAYER_TYPE_SOFTWARE, null)
        inflate(context, R.layout.bigbrother_view_filter, this)
        setBackgroundColor(context.getColor(CR.color.themed_transparent))
        setPadding(paddingOffset, refView.height, paddingOffset, paddingOffset)
        content.onMeasured {
            val temp = refView.x + (refView.width / 2)
            val headerCenter = when {
                temp > width -> refView.x + padding
                temp < padding -> padding
                else -> temp
            }

            background.cast(LayerDrawable::class).apply {
                val inset = ((headerCenter - (width / 2)) * 2).toInt()
                setLayerInsetLeft(0, inset)
                setLayerInsetLeft(2, inset)
                findDrawableByLayerId(CR.id.arrow).fixColor()
                findDrawableByLayerId(CR.id.content).fixColor()
            }
        }

        onMeasured {
            TransitionManager.beginDelayedTransition(parent as ViewGroup, CircularRevealTransition(
                epicenter = refBounds.centerX()
            ))
            isInvisible = false
        }

        isInvisible = true
        setOnClickListener {
            val parent = this.parent as ViewGroup
            val transition = CircularRevealTransition(epicenter = refBounds.centerX())
            transition.doOnEnd { parent.removeView(this@FilterView) }
            TransitionManager.beginDelayedTransition(parent, transition)
            isInvisible = true
        }

        setupViews()
    }

    fun setOnConfirm(listener: (String) -> Unit) {
        onConfirm = listener
    }

    fun setOnCancel(listener: () -> Unit) {
        onCancel = listener
    }

    private fun setupViews() {
        confirm.setOnClickListener {
            onConfirm?.invoke("generated SQL string")
            performClick()
        }

        cancel.setOnClickListener {
            performClick()
        }
    }

    private fun Drawable.fixColor() {
        val color = context.getColor(CR.color.background_secondary)
        when (this) {
            is RotateDrawable -> drawable?.fixColor()
            is GradientDrawable -> setColor(color)
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawRect(refBounds, cleaner)
    }
}