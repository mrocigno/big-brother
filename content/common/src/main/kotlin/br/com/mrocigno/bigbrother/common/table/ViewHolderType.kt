/*
* A copy/past from https://github.com/Cleveroad/AdaptiveTableLayout
*/

package br.com.mrocigno.bigbrother.common.table

/**
 * Type of adapter's ViewHolders
 */
@Retention(AnnotationRetention.SOURCE)
internal annotation class ViewHolderType() {
    companion object {
        /**
         * Top left header
         */
        val FIRST_HEADER: Int = 0

        /**
         * Vertical header
         */
        val ROW_HEADER: Int = 1

        /**
         * Horizontal header
         */
        val COLUMN_HEADER: Int = 2

        /**
         * Normal scrollable item
         */
        val ITEM: Int = 3
    }
}
