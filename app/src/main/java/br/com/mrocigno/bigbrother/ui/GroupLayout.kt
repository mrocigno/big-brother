package br.com.mrocigno.bigbrother.ui

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.withStyledAttributes
import br.com.mrocigno.bigbrother.R

class GroupLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val container: ConstraintLayout

    init {
        inflate(context, R.layout.group_view, this)
        container = findViewById(R.id.group_container)

        context.withStyledAttributes(attrs, R.styleable.GroupLayout) {
            val title = getString(R.styleable.GroupLayout_android_text).orEmpty()
            findViewById<AppCompatTextView>(R.id.group_title)?.text = title
        }
    }

    override fun addView(child: View?) {
        if (child.isLayoutChild) super.addView(child)
        else container.addView(child)
    }

    override fun addView(child: View?, index: Int) {
        if (child.isLayoutChild) super.addView(child, index)
        else container.addView(child, index)
    }

    override fun addView(child: View?, params: ViewGroup.LayoutParams?) {
        if (child.isLayoutChild) super.addView(child, params)
        else container.addView(child, params)
    }

    override fun addView(child: View?, width: Int, height: Int) {
        if (child.isLayoutChild) super.addView(child, width, height)
        else container.addView(child, width, height)
    }

    override fun addView(child: View?, index: Int, params: ViewGroup.LayoutParams?) {
        if (child.isLayoutChild) super.addView(child, index, params)
        else container.addView(child, index, params)
    }

    private val View?.isLayoutChild get() =
        this?.id == R.id.group_container
                || this?.id == R.id.group_title
}