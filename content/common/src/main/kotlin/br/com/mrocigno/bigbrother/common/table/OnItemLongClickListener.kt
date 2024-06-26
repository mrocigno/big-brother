/*
* A copy/past from https://github.com/Cleveroad/AdaptiveTableLayout
*/

package br.com.mrocigno.bigbrother.common.table

interface OnItemLongClickListener {
    /**
     * Long click item callback.
     *
     * @param row    clicked item row
     * @param column clicked item column
     */
    fun onItemLongClick(row: Int, column: Int)

    /**
     * Long click left top item callback
     */
    fun onLeftTopHeaderLongClick()
}
