package br.com.mrocigno.bigbrother.common.table

/**
 * Helper for convenient work with layout direction
 */
internal class LayoutDirectionHelper(var layoutDirection: Int) {
    val isRTL: Boolean
        get() {
            return layoutDirection == LayoutDirection.RTL
        }
}
