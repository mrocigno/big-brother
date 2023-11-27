/*
* A copy/past from https://github.com/Cleveroad/AdaptiveTableLayout
*/

package br.com.mrocigno.bigbrother.common.table

import android.os.Bundle

open interface DataAdaptiveTableLayoutAdapter<VH : ViewHolder> : AdaptiveTableAdapter<VH> {
    /**
     * Method calls when need to need to switch 2 columns with each other in the data matrix
     *
     * @param columnIndex   column from
     * @param columnToIndex column to
     */
    fun changeColumns(columnIndex: Int, columnToIndex: Int)

    /**
     * Method calls when need to need to switch 2 rows with each other in the data matrix
     *
     * @param rowIndex       row from
     * @param rowToIndex     row to
     * @param solidRowHeader fixed to the data or to the row number.
     */
    fun changeRows(rowIndex: Int, rowToIndex: Int, solidRowHeader: Boolean)
    fun onSaveInstanceState(bundle: Bundle)
    fun onRestoreInstanceState(bundle: Bundle)
}
