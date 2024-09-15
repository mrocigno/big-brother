/*
* A copy/past from https://github.com/Cleveroad/AdaptiveTableLayout
*/

package br.com.mrocigno.bigbrother.common.table

/**
 * Scroll type for drag and drop mode.
 */
@Retention(AnnotationRetention.SOURCE)
internal annotation class ScrollType() {
    companion object {
        val SCROLL_HORIZONTAL: Int = 0
        val SCROLL_VERTICAL: Int = 1
    }
}
