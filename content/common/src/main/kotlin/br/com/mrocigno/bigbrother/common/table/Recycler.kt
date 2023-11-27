/*
* A copy/past from https://github.com/Cleveroad/AdaptiveTableLayout
*/

package br.com.mrocigno.bigbrother.common.table

import android.util.SparseArray
import java.util.ArrayDeque
import java.util.Deque

/**
 * The Recycler facilitates reuse of mViewHolders across layouts.
 */
internal class Recycler() {
    private val mViewHolders: SparseArray<Deque<ViewHolder?>>

    /**
     * Constructor
     */
    init {
        mViewHolders = SparseArray(3)
    }

    /**
     * Add a view to the Recycler. This view may be reused in the function
     * [.popRecycledViewHolder]
     *
     * @param viewHolder A viewHolder to add to the Recycler. It can no longer be used.
     */
    fun pushRecycledView(viewHolder: ViewHolder?) {
        var deque: Deque<ViewHolder?>? = mViewHolders[viewHolder!!.itemType]
        if (deque == null) {
            deque = ArrayDeque()
            mViewHolders.put(viewHolder.itemType, deque)
        }
        deque.push(viewHolder)
    }

    /**
     * Returns, if exists, a view of the type `typeView`.
     *
     * @param itemType the type of view that you want.
     * @return a viewHolder of the type `typeView`. `null` if
     * not found.
     */
    fun popRecycledViewHolder(itemType: Int): ViewHolder? {
        val deque: Deque<ViewHolder?>? = mViewHolders.get(itemType)
        return if (deque == null || deque.isEmpty()) null else deque.pop()
    }
}
