package br.com.mrocigno.bigbrother.common.table

import android.annotation.TargetApi
import android.os.Build

/**
 * same as [AdaptiveTableManager], but support rtl direction
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
internal class AdaptiveTableManagerRTL(private val mLayoutDirectionHelper: LayoutDirectionHelper) :
    AdaptiveTableManager() {
    public override fun getColumnByXWithShift(x: Int, shiftEveryStep: Int): Int {
        if (!mLayoutDirectionHelper.isRTL) {
            return super.getColumnByXWithShift(x, shiftEveryStep)
        } else {
            checkForInit()
            var sum: Int = 0
            if (x <= sum) {
                return 0
            }
            val count: Int = columnWidths!!.size
            var i: Int = 0
            while (i < count) {
                val nextSum: Int = sum + columnWidths!![i] + shiftEveryStep
                if (x > sum && x < nextSum) {
                    return i
                } else if (x < nextSum) {
                    return i - 1
                }
                sum = nextSum
                i++
            }
            return columnWidths!!.size - 1
        }
    }
}
