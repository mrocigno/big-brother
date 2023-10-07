package br.com.mrocigno.bigbrother.common.table

/**
 * Layout state holder.
 */
internal class AdaptiveTableState() {
    /**
     * Current scroll position.
     */
    var scrollX: Int = 0
    var scrollY: Int = 0

    /**
     * Dragging row flag
     */
    var isRowDragging: Boolean = false
        private set

    /**
     * Dragging column flag
     */
    var isColumnDragging: Boolean = false
        private set

    /**
     * Dragging column index
     */
    var columnDraggingIndex: Int = 0
        private set

    /**
     * Dragging row index
     */
    var rowDraggingIndex: Int = 0
        private set

    fun setRowDragging(rowDragging: Boolean, rowIndex: Int) {
        isRowDragging = rowDragging
        rowDraggingIndex = rowIndex
    }

    fun setColumnDragging(columnDragging: Boolean, columnIndex: Int) {
        isColumnDragging = columnDragging
        columnDraggingIndex = columnIndex
    }

    val isDragging: Boolean
        get() {
            return isColumnDragging || isRowDragging
        }

    companion object {
        val NO_DRAGGING_POSITION: Int = -1
    }
}
