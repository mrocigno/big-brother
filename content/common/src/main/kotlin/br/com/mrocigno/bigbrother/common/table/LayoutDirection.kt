package br.com.mrocigno.bigbrother.common.table

/**
 * Type of layout directions. Same values as in android.util.LayoutDirection
 * This interface needed because project min API is 16
 */
@Retention(AnnotationRetention.SOURCE)
internal annotation class LayoutDirection() {
    companion object {
        /**
         * Top left header
         */
        val LTR: Int = 0

        /**
         * Vertical header
         */
        val RTL: Int = 1
    }
}
