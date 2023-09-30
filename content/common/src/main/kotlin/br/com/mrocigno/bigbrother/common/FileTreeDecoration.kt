package br.com.mrocigno.bigbrother.common

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import androidx.annotation.ColorInt
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView

class FileTreeDecoration(
    @ColorInt color: Int = Color.BLACK
) : RecyclerView.ItemDecoration() {

    private val paint = Paint().apply {
        this.color = color
        this.strokeWidth = 4f
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)
        parent.children.forEach { view ->
            val centerY = ((view.bottom - view.y) / 2) + view.y
            val x = view.x - 10
            c.drawLine(x, view.y - 100, x, centerY, paint)
            c.drawLine(x, centerY, x + 30, centerY, paint)
        }
    }
}