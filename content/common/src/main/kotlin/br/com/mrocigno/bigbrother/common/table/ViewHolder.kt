/*
* A copy/past from https://github.com/Cleveroad/AdaptiveTableLayout
*/

package br.com.mrocigno.bigbrother.common.table

import android.view.View

/**
 * {@inheritDoc}
 */
abstract class ViewHolder(val itemView: View) {

    /**
     * ViewHolder's table row index
     */
    var rowIndex: Int = 0

    /**
     * ViewHolder's table column index
     */
    var columnIndex: Int = 0

    /**
     * ViewHolder's table item type param
     */
    var itemType: Int = 0

    /**
     * ViewHolder's dragging flag
     */
    var isDragging: Boolean = false

    override fun hashCode(): Int {
        var result: Int = itemView.hashCode()
        result = 31 * result + rowIndex
        result = 31 * result + columnIndex
        result = 31 * result + itemType
        result = 31 * result + (if (isDragging) 1 else 0)
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (other !is ViewHolder) return false
        val vh: ViewHolder = other
        return vh.columnIndex == columnIndex && vh.rowIndex == rowIndex
    }
}
