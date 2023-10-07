package br.com.mrocigno.bigbrother.common.table

import android.view.View

/**
 * {@inheritDoc}
 */
abstract class ViewHolderImpl(override val itemView: View) : ViewHolder {

    /**
     * ViewHolder's table row index
     */
    override var rowIndex: Int = 0

    /**
     * ViewHolder's table column index
     */
    override var columnIndex: Int = 0

    /**
     * ViewHolder's table item type param
     */
    override var itemType: Int = 0

    /**
     * ViewHolder's dragging flag
     */
    override var isDragging: Boolean = false

    public override fun hashCode(): Int {
        var result: Int = itemView.hashCode()
        result = 31 * result + rowIndex
        result = 31 * result + columnIndex
        result = 31 * result + itemType
        result = 31 * result + (if (isDragging) 1 else 0)
        return result
    }

    public override fun equals(obj: Any?): Boolean {
        if (!(obj is ViewHolder)) {
            return false
        }
        val vh: ViewHolder = obj
        return vh.columnIndex == columnIndex && vh.rowIndex == rowIndex
    }
}
