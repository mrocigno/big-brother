package br.com.mrocigno.bigbrother.proxy.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.view.View
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import br.com.mrocigno.bigbrother.common.utils.getBounds
import br.com.mrocigno.bigbrother.proxy.R
import br.com.mrocigno.bigbrother.common.R as CR

class ProxyActionItemDecorator(context: Context) : ItemDecoration() {

    private val line = Paint().apply {
        color = context.getColor(CR.color.bb_text_title)
        style = Paint.Style.STROKE
        strokeWidth = 2f
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        if ((parent.adapter?.itemCount ?: 0) < 1) return
        parent.children.forEach {
            val isFirstView = parent.getChildAdapterPosition(it) == 0
            val isLastView = parent.getChildAdapterPosition(it) == (parent.adapter?.itemCount ?: 0) - 1
            val icon = it.findViewById<View>(R.id.proxy_item_action_icon)
            val viewBounds = it.getBounds()
            val iconBound = icon.getBounds()
            iconBound.offset(viewBounds.left, viewBounds.top)
            c.drawLine(
                iconBound.centerX(),
                if (isFirstView || isLastView) iconBound.centerY()
                else viewBounds.top,
                iconBound.centerX(),
                if (isLastView) viewBounds.top
                else viewBounds.bottom,
                line
            )
        }
    }
}