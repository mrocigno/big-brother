/*
* A copy/past from https://github.com/Cleveroad/AdaptiveTableLayout
*/

package br.com.mrocigno.bigbrother.common.table

open interface OnItemClickListener {
    /**
     * Click item callback.
     *
     * @param row    clicked item row
     * @param column clicked item column
     */
    fun onItemClick(row: Int, column: Int)

    /**
     * Click row header item callback
     *
     * @param row clicked row header
     */
    fun onRowHeaderClick(row: Int)

    /**
     * Click column header item callback
     *
     * @param column clicked column header
     */
    fun onColumnHeaderClick(viewHolder: ViewHolder, column: Int)

    /**
     * Click left top item callback
     */
    fun onLeftTopHeaderClick()
}
