/*
* A copy/past from https://github.com/Cleveroad/AdaptiveTableLayout
*/

package br.com.mrocigno.bigbrother.common.table

import android.view.View
import android.view.ViewGroup
import br.com.mrocigno.bigbrother.common.R

internal class ShadowHelper(private val mLayoutDirectionHelper: LayoutDirectionHelper) {
    var rightShadow: View? = null
        private set

    var leftShadow: View? = null
        private set

    var topShadow: View? = null
        private set

    var bottomShadow: View? = null
        private set

    var columnsHeadersShadow: View? = null
        private set

    var rowsHeadersShadow: View? = null
        private set

    fun addColumnsHeadersShadow(group: ViewGroup): View {
        if (columnsHeadersShadow == null) {
            columnsHeadersShadow = View(group.context)
            columnsHeadersShadow!!.setBackgroundResource(R.drawable.bigbrother_table_shadow_bottom)
            group.addView(columnsHeadersShadow, 0)
        }
        return columnsHeadersShadow!!
    }

    fun addRowsHeadersShadow(group: ViewGroup): View {
        if (rowsHeadersShadow == null) {
            rowsHeadersShadow = View(group.context).apply {
                setBackgroundResource(
                    if (!mLayoutDirectionHelper.isRTL) R.drawable.bigbrother_table_shadow_right
                    else R.drawable.bigbrother_table_shadow_left
                )
            }
            group.addView(rowsHeadersShadow, 0)
        }
        return rowsHeadersShadow!!
    }

    fun removeColumnsHeadersShadow(group: ViewGroup) {
        if (columnsHeadersShadow != null) {
            group.removeView(columnsHeadersShadow)
            columnsHeadersShadow = null
        }
    }

    fun removeRowsHeadersShadow(group: ViewGroup) {
        if (rowsHeadersShadow != null) {
            group.removeView(rowsHeadersShadow)
            rowsHeadersShadow = null
        }
    }

    fun addLeftShadow(group: ViewGroup): View {
        if (leftShadow == null) {
            leftShadow = View(group.getContext())
            leftShadow!!.setBackgroundResource(R.drawable.bigbrother_table_shadow_left)
            group.addView(leftShadow, 0)
        }
        return leftShadow!!
    }

    fun addRightShadow(group: ViewGroup): View {
        if (rightShadow == null) {
            rightShadow = View(group.getContext())
            rightShadow!!.setBackgroundResource(R.drawable.bigbrother_table_shadow_right)
            group.addView(rightShadow, 0)
        }
        return rightShadow!!
    }

    fun addTopShadow(group: ViewGroup): View {
        if (topShadow == null) {
            topShadow = View(group.getContext())
            topShadow!!.setBackgroundResource(R.drawable.bigbrother_table_shadow_top)
            group.addView(topShadow, 0)
        }
        return topShadow!!
    }

    fun addBottomShadow(group: ViewGroup): View {
        if (bottomShadow == null) {
            bottomShadow = View(group.getContext())
            bottomShadow!!.setBackgroundResource(R.drawable.bigbrother_table_shadow_bottom)
            group.addView(bottomShadow, 0)
        }
        return bottomShadow!!
    }

    fun removeAllDragAndDropShadows(group: ViewGroup) {
        if (leftShadow != null) {
            group.removeView(leftShadow)
            leftShadow = null
        }
        if (rightShadow != null) {
            group.removeView(rightShadow)
            rightShadow = null
        }
        if (topShadow != null) {
            group.removeView(topShadow)
            topShadow = null
        }
        if (bottomShadow != null) {
            group.removeView(bottomShadow)
            bottomShadow = null
        }
    }

    fun onLayoutDirectionChanged() {
        if (rowsHeadersShadow != null) {
            rowsHeadersShadow?.setBackgroundResource(
                if (!mLayoutDirectionHelper.isRTL) R.drawable.bigbrother_table_shadow_right else R.drawable.bigbrother_table_shadow_left
            )
            rowsHeadersShadow!!.requestLayout()
        }
    }
}
