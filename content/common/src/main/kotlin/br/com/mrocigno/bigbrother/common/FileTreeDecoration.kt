package br.com.mrocigno.bigbrother.common

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView

class FileTreeDecoration(context: Context) : RecyclerView.ItemDecoration() {

    private val spacingXl = context.resources.getDimension(R.dimen.bb_spacing_xl)
    private val spacingS = context.resources.getDimension(R.dimen.bb_spacing_s)
    private val paint = Paint().apply {
        this.color = context.getColor(R.color.bb_text_title)
        this.strokeWidth = 4f
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)
        parent.children.forEach { view ->
            val centerY = ((view.bottom - view.y) / 2) + view.y
            val x = view.x - spacingS
            c.drawLine(x, view.y - spacingXl, x, centerY, paint)
            c.drawLine(x - spacingXl, view.y - spacingXl, x - spacingXl, centerY, paint)
            c.drawLine(x, centerY, x + spacingS, centerY, paint)
        }
    }
}