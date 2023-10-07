package br.com.mrocigno.bigbrother.common.table

abstract class BaseDataAdaptiveTableLayoutAdapter<VH : ViewHolder>() :
    LinkedAdaptiveTableAdapter<VH>(), DataAdaptiveTableLayoutAdapter<VH> {
    protected abstract val items: Array<Array<Any?>>
    protected abstract val rowHeaders: Array<Any?>
    protected abstract val columnHeaders: Array<Any?>
    public override fun changeColumns(columnIndex: Int, columnToIndex: Int) {
        // switch data
        switchTwoColumns(columnIndex, columnToIndex)
        // switch headers
        switchTwoColumnHeaders(columnIndex, columnToIndex)
    }

    /**
     * Switch 2 columns with data
     *
     * @param columnIndex   column from
     * @param columnToIndex column to
     */
    fun switchTwoColumns(columnIndex: Int, columnToIndex: Int) {
        for (i in 0 until (rowCount - 1)) {
            val cellData: Any? = items[i][columnToIndex]
            items[i][columnToIndex] = items[i][columnIndex]
            items[i][columnIndex] = cellData
        }
    }

    /**
     * Switch 2 columns headers with data
     *
     * @param columnIndex   column header from
     * @param columnToIndex column header to
     */
    fun switchTwoColumnHeaders(columnIndex: Int, columnToIndex: Int) {
        val cellData: Any? = columnHeaders[columnToIndex]
        columnHeaders[columnToIndex] = columnHeaders[columnIndex]
        columnHeaders[columnIndex] = cellData
    }

    public override fun changeRows(rowIndex: Int, rowToIndex: Int, solidRowHeader: Boolean) {
        // switch data
        switchTwoRows(rowIndex, rowToIndex)
        if (solidRowHeader) {
            // switch headers
            switchTwoRowHeaders(rowIndex, rowToIndex)
        }
    }

    /**
     * Switch 2 rows with data
     *
     * @param rowIndex   row from
     * @param rowToIndex row to
     */
    fun switchTwoRows(rowIndex: Int, rowToIndex: Int) {
        for (i in items.indices) {
            val cellData: Any? = items[rowToIndex][i]
            items[rowToIndex][i] = items[rowIndex][i]
            items[rowIndex][i] = cellData
        }
    }

    /**
     * Switch 2 rows headers with data
     *
     * @param rowIndex   row header from
     * @param rowToIndex row header to
     */
    fun switchTwoRowHeaders(rowIndex: Int, rowToIndex: Int) {
        val cellData: Any? = rowHeaders[rowToIndex]
        rowHeaders[rowToIndex] = rowHeaders[rowIndex]
        rowHeaders[rowIndex] = cellData
    }
}
