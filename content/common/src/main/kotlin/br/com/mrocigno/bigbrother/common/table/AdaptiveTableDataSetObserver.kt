package br.com.mrocigno.bigbrother.common.table

interface AdaptiveTableDataSetObserver {
    /**
     * Notify any registered observers that the data set has changed.
     * If you change the size of the data set, you must call [AdaptiveTableDataSetObserver.notifyLayoutChanged] instead
     */
    fun notifyDataSetChanged()

    /**
     * You must call this when something has changed which has invalidated the layout of this view
     * or the size of the data set was changed
     */
    fun notifyLayoutChanged()

    /**
     * Notify any registered observers that the item at
     * (`rowIndex`;`columnIndex`) has changed.
     *
     * @param rowIndex    the row index
     * @param columnIndex the column index
     */
    fun notifyItemChanged(rowIndex: Int, columnIndex: Int)

    /**
     * Notify any registered observers that the row with rowIndex has changed.
     *
     * @param rowIndex the row index
     */
    fun notifyRowChanged(rowIndex: Int)

    /**
     * Notify any registered observers that the column with columnIndex has changed.
     *
     * @param columnIndex the column index
     */
    fun notifyColumnChanged(columnIndex: Int)
}
