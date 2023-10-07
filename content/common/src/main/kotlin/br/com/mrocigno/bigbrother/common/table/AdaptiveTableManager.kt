package br.com.mrocigno.bigbrother.common.table

/**
 * Manage row's heights and column's widths.
 * Work flow:
 * 1) Create object AdaptiveTableManager
 * 2) Call [.init]
 * 3) Put data using [.putRowHeight] and [.putColumnWidth]
 * 4) Call invalidate
 *
 *
 * In case changing full width or count of rows or columns, you need to re-init manager.(steps 2 - 4)
 */
internal open class AdaptiveTableManager() {
    /**
     * Contains full width (columns widths)
     */
    private var mFullWidth: Long = 0

    /**
     * Contains full height (rows heights)
     */
    private var mFullHeight: Long = 0

    /**
     * Array with column's widths
     */
    var columnWidths: IntArray? = null
        private set

    /**
     * Array with row's heights
     */
    var rowHeights: IntArray? = null
        private set

    /**
     * Column's header height
     */
    private var mHeaderColumnHeight: Int = 0

    /**
     * Column's row width
     */
    private var mHeaderRowWidth: Int = 0
    private var mIsInited: Boolean = false
    fun clear() {
        // clear objects
        rowHeights = IntArray(0)
        columnWidths = IntArray(0)
        mFullWidth = 0
        mFullHeight = 0
        mHeaderColumnHeight = 0
        mHeaderRowWidth = 0
        mIsInited = false
    }

    fun init(rowCount: Int, columnCount: Int) {
        // create objects
        rowHeights = IntArray(rowCount)
        columnWidths = IntArray(columnCount)
        mIsInited = true
    }

    fun checkForInit() {
        if (!mIsInited) {
            throw IllegalStateException("You need to init matrix before work!")
        }
    }

    fun invalidate() {
        checkForInit()
        // calculate widths
        mFullWidth = 0
        for (itemWidth: Int in columnWidths!!) {
            mFullWidth += itemWidth.toLong()
        }

        // calculate heights
        mFullHeight = 0
        for (itemHeight: Int in rowHeights!!) {
            mFullHeight += itemHeight.toLong()
        }
    }

    /**
     * @param column column index
     * @return column's width
     */
    fun getColumnWidth(column: Int): Int {
        checkForInit()
        return columnWidths!!.get(column)
    }

    /**
     * Put column width to the array. Call [.invalidate] after all changes.
     *
     * @param column column index
     * @param width  column's width
     */
    fun putColumnWidth(column: Int, width: Int) {
        checkForInit()
        columnWidths!![column] = width
    }

    /**
     * @param from from column index
     * @param to   to column index
     * @return columns width
     */
    fun getColumnsWidth(from: Int, to: Int): Int {
        checkForInit()
        var width: Int = 0
        var i: Int = from
        while (i < to && columnWidths != null) {
            width += columnWidths!!.get(i)
            i++
        }
        return width
    }

    val columnCount: Int
        /**
         * @return columns count
         */
        get() {
            checkForInit()
            if (columnWidths != null) {
                return columnWidths!!.size
            }
            return 0
        }

    /**
     * @param row row index
     * @return row's height
     */
    fun getRowHeight(row: Int): Int {
        checkForInit()
        return rowHeights!!.get(row)
    }

    /**
     * Put row height to the array. Call [.invalidate] after all changes.
     *
     * @param row    row index
     * @param height row's height
     */
    fun putRowHeight(row: Int, height: Int) {
        checkForInit()
        rowHeights!![row] = height
    }

    /**
     * @param from from row index
     * @param to   to row index
     * @return rows height
     */
    fun getRowsHeight(from: Int, to: Int): Int {
        checkForInit()
        var height: Int = 0
        var i: Int = from
        while (i < to && rowHeights != null) {
            height += rowHeights!!.get(i)
            i++
        }
        return height
    }

    val rowCount: Int
        /**
         * @return rows count
         */
        get() {
            checkForInit()
            if (rowHeights != null) {
                return rowHeights!!.size
            }
            return 0
        }
    val fullWidth: Long
        /**
         * @return columns width with row's header width
         */
        get() {
            checkForInit()
            return mFullWidth + mHeaderRowWidth
        }
    val fullHeight: Long
        /**
         * @return rows height with column's header height
         */
        get() {
            checkForInit()
            return mFullHeight + mHeaderColumnHeight
        }

    /**
     * Return column number which bounds contains X
     *
     * @param x coordinate
     * @return column number
     */
    fun getColumnByX(x: Int): Int {
        checkForInit()
        var sum: Int = 0
        // header offset
        val tempX: Int = x - mHeaderRowWidth
        if (tempX <= sum) {
            return 0
        }
        val count: Int = columnWidths!!.size
        var i: Int = 0
        while (i < count) {
            val nextSum: Int = sum + columnWidths!!.get(i)
            if (tempX > sum && tempX < nextSum) {
                return i
            } else if (tempX < nextSum) {
                return i - 1
            }
            sum = nextSum
            i++
        }
        return columnWidths!!.size - 1
    }

    /**
     * Return column number which bounds contains X
     *
     * @param x coordinate
     * @return column number
     */
    open fun getColumnByXWithShift(x: Int, shiftEveryStep: Int): Int {
        checkForInit()
        var sum: Int = 0
        // header offset
        val tempX: Int = x - mHeaderRowWidth
        if (tempX <= sum) {
            return 0
        }
        val count: Int = columnWidths!!.size
        var i: Int = 0
        while (i < count) {
            val nextSum: Int = sum + columnWidths!!.get(i) + shiftEveryStep
            if (tempX > sum && tempX < nextSum) {
                return i
            } else if (tempX < nextSum) {
                return i - 1
            }
            sum = nextSum
            i++
        }
        return columnWidths!!.size - 1
    }

    /**
     * Return row number which bounds contains Y
     *
     * @param y coordinate
     * @return row number
     */
    fun getRowByY(y: Int): Int {
        checkForInit()
        var sum: Int = 0
        // header offset
        val tempY: Int = y - mHeaderColumnHeight
        if (tempY <= sum) {
            return 0
        }
        val count: Int = rowHeights!!.size
        var i: Int = 0
        while (i < count) {
            val nextSum: Int = sum + rowHeights!!.get(i)
            if (tempY > sum && tempY < nextSum) {
                return i
            } else if (tempY < nextSum) {
                return i - 1
            }
            sum = nextSum
            i++
        }
        return rowHeights!!.size - 1
    }

    /**
     * Return row number which bounds contains Y
     *
     * @param y coordinate
     * @return row number
     */
    fun getRowByYWithShift(y: Int, shiftEveryStep: Int): Int {
        checkForInit()
        var sum: Int = 0
        // header offset
        val tempY: Int = y - mHeaderColumnHeight
        if (tempY <= sum) {
            return 0
        }
        val count: Int = rowHeights!!.size
        var i: Int = 0
        while (i < count) {
            val nextSum: Int = sum + rowHeights!!.get(i) + shiftEveryStep
            if (tempY > sum && tempY < nextSum) {
                return i
            } else if (tempY < nextSum) {
                return i - 1
            }
            sum = nextSum
            i++
        }
        return rowHeights!!.size - 1
    }

    var headerColumnHeight: Int
        /**
         * @return column's header height
         */
        get() {
            checkForInit()
            return mHeaderColumnHeight
        }
        /**
         * Set column's header height.
         *
         * @param headerColumnHeight column's header height.
         */
        set(headerColumnHeight) {
            checkForInit()
            mHeaderColumnHeight = headerColumnHeight
        }
    var headerRowWidth: Int
        /**
         * @return row's header width
         */
        get() {
            checkForInit()
            return mHeaderRowWidth
        }
        /**
         * Set row's header width.
         *
         * @param headerRowWidth row's header width.
         */
        set(headerRowWidth) {
            checkForInit()
            mHeaderRowWidth = headerRowWidth
        }

    /**
     * Switch 2 items in array with columns data
     *
     * @param columnIndex   from column index
     * @param columnToIndex to column index
     */
    fun switchTwoColumns(columnIndex: Int, columnToIndex: Int) {
        checkForInit()
        val cellData: Int = columnWidths!![columnToIndex]
        columnWidths!![columnToIndex] = columnWidths!![columnIndex]
        columnWidths!![columnIndex] = cellData
    }

    /**
     * Switch 2 items in array with rows data
     *
     * @param rowIndex   from row index
     * @param rowToIndex to row index
     */
    fun switchTwoRows(rowIndex: Int, rowToIndex: Int) {
        checkForInit()
        val cellData: Int = rowHeights!![rowToIndex]
        rowHeights!![rowToIndex] = rowHeights!![rowIndex]
        rowHeights!![rowIndex] = cellData
    }
}
