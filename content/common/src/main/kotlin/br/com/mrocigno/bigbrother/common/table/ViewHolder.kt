package br.com.mrocigno.bigbrother.common.table

import android.view.View

/**
 * A [ViewHolder] describes an item view and metadata about its place within the [AdaptiveTableLayout].
 */
open interface ViewHolder {
    val itemView: View
    /**
     * @return the type of the View
     */
    /**
     * @param itemType is the type of the View
     */
    var itemType: Int
    /**
     * @return the row index.
     */
    /**
     * @param rowIndex the row index.
     */
    var rowIndex: Int
    /**
     * @return the column index.
     */
    /**
     * @param columnIndex the column index.
     */
    var columnIndex: Int
    /**
     * @return dragging flag
     */
    /**
     * @param isDragging dragging param
     */
    var isDragging: Boolean
}
