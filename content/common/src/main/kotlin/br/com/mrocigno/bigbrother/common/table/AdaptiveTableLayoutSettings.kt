/*
* A copy/past from https://github.com/Cleveroad/AdaptiveTableLayout
*/

package br.com.mrocigno.bigbrother.common.table

/**
 * Settings keeper class.
 */
internal class AdaptiveTableLayoutSettings() {
    /**
     * Layout width
     */
    var layoutWidth: Int = 0
        private set

    /**
     * Layout height
     */
    var layoutHeight: Int = 0
        private set
    var isHeaderFixed: Boolean = false
        private set
    var cellMargin: Int = 0
        private set

    /**
     * if true - value of row header fixed to the row. Fixed to the data
     * if false - fixed to the number of row. Fixed to the row' number from 0 to n.
     */
    var isSolidRowHeader: Boolean = false
        private set

    /**
     * If true, then the table can be edited, otherwise it is impossible
     */
    var isDragAndDropEnabled: Boolean = false
    fun setLayoutWidth(layoutWidth: Int): AdaptiveTableLayoutSettings {
        this.layoutWidth = layoutWidth
        return this
    }

    fun setLayoutHeight(layoutHeight: Int): AdaptiveTableLayoutSettings {
        this.layoutHeight = layoutHeight
        return this
    }

    fun setHeaderFixed(headerFixed: Boolean): AdaptiveTableLayoutSettings {
        isHeaderFixed = headerFixed
        return this
    }

    fun setCellMargin(cellMargin: Int): AdaptiveTableLayoutSettings {
        this.cellMargin = cellMargin
        return this
    }

    fun setSolidRowHeader(solidRowHeader: Boolean): AdaptiveTableLayoutSettings {
        isSolidRowHeader = solidRowHeader
        return this
    }
}
