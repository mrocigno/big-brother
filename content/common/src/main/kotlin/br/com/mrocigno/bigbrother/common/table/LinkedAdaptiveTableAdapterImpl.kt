/*
* A copy/past from https://github.com/Cleveroad/AdaptiveTableLayout
*/

package br.com.mrocigno.bigbrother.common.table

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.ViewGroup
import br.com.mrocigno.bigbrother.common.utils.getSerializableCompat

/**
 * This is AdaptiveTableAdapter decorator (wrapper).
 * It makes it possible to change the rows and columns without data mutations.
 *
 * @param <VH> Adapter's ViewHolder class
</VH> */
internal class LinkedAdaptiveTableAdapterImpl<VH : ViewHolder> @SuppressLint("UseSparseArrays") constructor(
    /**
     * Decorated AdaptiveTableAdapter
     */
    private val mInner: AdaptiveTableAdapter<VH>,
    /**
     * need to fix row header to data or to row' number
     * true - fixed to the row data.
     * false - fixed to row number.
     */
    private var mIsSolidRowHeader: Boolean
) : LinkedAdaptiveTableAdapter<VH>(), DataAdaptiveTableLayoutAdapter<VH>, OnItemClickListener,
    OnItemLongClickListener {
    /**
     * Redirect column's ids
     */
    private var mColumnIndexToId = HashMap<Int, Int>()
    private var mColumnIdToIndex = HashMap<Int, Int>()

    /**
     * Redirect row's ids
     */
    private var mRowIndexToId = HashMap<Int, Int>()
    private var mRowIdToIndex = HashMap<Int, Int>()

    override fun onItemLongClick(row: Int, column: Int) {
        val innerListener: OnItemLongClickListener? = mInner.onItemLongClickListener
        innerListener?.onItemLongClick(rowIndexToId(row), columnIndexToId(column))
    }

    override fun onLeftTopHeaderLongClick() {
        mInner.onItemLongClickListener?.onLeftTopHeaderLongClick()
    }

    override fun onItemClick(row: Int, column: Int) {
        val innerListener: OnItemClickListener? = mInner.onItemClickListener
        if (innerListener != null) {
            val tempRow: Int = row + 1 // need to merge matrix with table headers and without.
            val tempColumn: Int = column + 1 // need to merge matrix with table headers and without.
            innerListener.onItemClick(rowIndexToId(tempRow), columnIndexToId(tempColumn))
        }
    }

    override fun onRowHeaderClick(row: Int) {
        val innerListener: OnItemClickListener? = mInner.onItemClickListener
        if (innerListener != null) {
            val tempRow: Int = row + 1 // need to merge matrix with table headers and without.
            innerListener.onRowHeaderClick(if (mIsSolidRowHeader) rowIndexToId(tempRow) else tempRow)
        }
    }

    override fun onColumnHeaderClick(viewHolder: ViewHolder, column: Int) {
        val innerListener: OnItemClickListener? = mInner.onItemClickListener
        if (innerListener != null) {
            val tempColumn: Int = column + 1 // need to merge matrix with table headers and without.
            innerListener.onColumnHeaderClick(viewHolder, columnIndexToId(tempColumn))
        }
    }

    override fun onLeftTopHeaderClick() {
        mInner.onItemClickListener?.onLeftTopHeaderClick()
    }

    override fun changeColumns(columnIndex: Int, columnToIndex: Int) {
        val tempColumnIndex: Int =
            columnIndex + 1 // need to merge matrix with table headers and without.
        val tempColumnToIndex: Int =
            columnToIndex + 1 // need to merge matrix with table headers and without.
        val fromId: Int = columnIndexToId(tempColumnIndex)
        val toId: Int = columnIndexToId(tempColumnToIndex)
        if (tempColumnIndex != toId) {
            mColumnIndexToId[tempColumnIndex] = toId
            mColumnIdToIndex[toId] = tempColumnIndex
        } else {
            //remove excess modifications
            mColumnIndexToId.remove(tempColumnIndex)
            mColumnIdToIndex.remove(toId)
        }
        if (tempColumnToIndex != fromId) {
            mColumnIndexToId[tempColumnToIndex] = fromId
            mColumnIdToIndex[fromId] = tempColumnToIndex
        } else {
            //remove excess modifications
            mColumnIndexToId.remove(tempColumnToIndex)
            mColumnIdToIndex.remove(fromId)
        }
    }

    override fun changeRows(rowIndex: Int, rowToIndex: Int, solidRowHeader: Boolean) {
        val tempRowIndex: Int = rowIndex + 1 // need to merge matrix with table headers and without.
        val tempRowToIndex: Int =
            rowToIndex + 1 // need to merge matrix with table headers and without.
        mIsSolidRowHeader = solidRowHeader
        val fromId: Int = rowIndexToId(tempRowIndex)
        val toId: Int = rowIndexToId(tempRowToIndex)
        if (tempRowIndex != toId) {
            mRowIndexToId[tempRowIndex] = toId
            mRowIdToIndex[toId] = tempRowIndex
        } else {
            mRowIndexToId.remove(tempRowIndex)
            mRowIdToIndex.remove(toId)
        }
        if (tempRowToIndex != fromId) {
            mRowIndexToId[tempRowToIndex] = fromId
            mRowIdToIndex[fromId] = tempRowToIndex
        } else {
            mRowIndexToId.remove(tempRowToIndex)
            mRowIdToIndex.remove(fromId)
        }
    }

    override val rowCount: Int
        get() {
            return mInner.rowCount
        }
    override val columnCount: Int
        get() {
            return mInner.columnCount
        }

    override fun onCreateItemViewHolder(parent: ViewGroup): VH {
        return mInner.onCreateItemViewHolder(parent)
    }

    override fun onCreateColumnHeaderViewHolder(parent: ViewGroup): VH {
        return mInner.onCreateColumnHeaderViewHolder(parent)
    }

    override fun onCreateRowHeaderViewHolder(parent: ViewGroup): VH {
        return mInner.onCreateRowHeaderViewHolder(parent)
    }

    override fun onCreateLeftTopHeaderViewHolder(parent: ViewGroup): VH {
        return mInner.onCreateLeftTopHeaderViewHolder(parent)
    }

    override fun onBindViewHolder(viewHolder: VH, row: Int, column: Int) {
        val tempRow: Int = row + 1 // need to merge matrix with table headers and without.
        val tempColumn: Int = column + 1 // need to merge matrix with table headers and without.
        mInner.onBindViewHolder(viewHolder, rowIndexToId(tempRow), columnIndexToId(tempColumn))
    }

    override fun onBindHeaderColumnViewHolder(viewHolder: VH, column: Int) {
        val tempColumn: Int = column + 1 // need to merge matrix with table headers and without.
        mInner.onBindHeaderColumnViewHolder(viewHolder, columnIndexToId(tempColumn))
    }

    override fun onBindHeaderRowViewHolder(viewHolder: VH, row: Int) {
        val tempRow: Int = row + 1 // need to merge matrix with table headers and without.
        mInner.onBindHeaderRowViewHolder(
            viewHolder,
            if (mIsSolidRowHeader) rowIndexToId(tempRow) else tempRow
        )
    }

    override fun onBindLeftTopHeaderViewHolder(viewHolder: VH) {
        mInner.onBindLeftTopHeaderViewHolder(viewHolder)
    }

    override fun getColumnWidth(column: Int): Int {
        val tempColumn: Int = column + 1 // need to merge matrix with table headers and without.
        return mInner.getColumnWidth(columnIndexToId(tempColumn))
    }

    override val headerColumnHeight: Int
        get() {
            return mInner.headerColumnHeight
        }

    override fun getRowHeight(row: Int): Int {
        val tempRow: Int = row + 1 // need to merge matrix with table headers and without.
        return mInner.getRowHeight(rowIndexToId(tempRow))
    }

    override val headerRowWidth: Int
        get() {
            return mInner.headerRowWidth
        }

    override var onItemClickListener: OnItemClickListener?
        get() {
            return this
        }
        set(onItemClickListener) {
            super.onItemClickListener = onItemClickListener
        }

    override var onItemLongClickListener: OnItemLongClickListener?
        get() {
            return this
        }
        set(onItemLongClickListener) {
            super.onItemLongClickListener = onItemLongClickListener
        }

    override fun onViewHolderRecycled(viewHolder: VH) {
        mInner.onViewHolderRecycled(viewHolder)
    }

    override fun onSaveInstanceState(bundle: Bundle) {
        bundle.putSerializable(EXTRA_SAVE_STATE_COLUMN_INDEX_TO_ID, mColumnIndexToId)
        bundle.putSerializable(EXTRA_SAVE_STATE_COLUMN_ID_TO_INDEX, mColumnIdToIndex)
        bundle.putSerializable(EXTRA_SAVE_STATE_ROW_INDEX_TO_ID, mRowIndexToId)
        bundle.putSerializable(EXTRA_SAVE_STATE_ROW_ID_TO_INDEX, mRowIdToIndex)
    }

    override fun onRestoreInstanceState(bundle: Bundle) {
        mColumnIndexToId =
            bundle.getSerializableCompat<HashMap<Int, Int>>(EXTRA_SAVE_STATE_COLUMN_INDEX_TO_ID)!!
        mColumnIdToIndex =
            bundle.getSerializableCompat<HashMap<Int, Int>>(EXTRA_SAVE_STATE_COLUMN_ID_TO_INDEX)!!
        mRowIndexToId =
            bundle.getSerializableCompat<HashMap<Int, Int>>(EXTRA_SAVE_STATE_ROW_INDEX_TO_ID)!!
        mRowIdToIndex =
            bundle.getSerializableCompat<HashMap<Int, Int>>(EXTRA_SAVE_STATE_ROW_ID_TO_INDEX)!!
    }

    override fun notifyItemChanged(rowIndex: Int, columnIndex: Int) {
        super.notifyItemChanged(rowIdToIndex(rowIndex), columnIdToIndex(columnIndex))
    }

    override fun notifyRowChanged(rowIndex: Int) {
        super.notifyRowChanged(rowIdToIndex(rowIndex))
    }

    override fun notifyColumnChanged(columnIndex: Int) {
        super.notifyColumnChanged(columnIdToIndex(columnIndex))
    }

    val rowsModifications: Map<Int, Int>
        get() = mRowIndexToId

    val columnsModifications: Map<Int, Int>
        get() = mColumnIndexToId

    private fun columnIndexToId(columnIndex: Int): Int {
        return mColumnIndexToId[columnIndex] ?: columnIndex
    }

    private fun columnIdToIndex(columnId: Int): Int {
        return mColumnIdToIndex[columnId] ?: columnId
    }

    private fun rowIndexToId(rowIndex: Int): Int {
        return mRowIndexToId[rowIndex] ?: rowIndex
    }

    private fun rowIdToIndex(rowId: Int): Int {
        return mRowIdToIndex[rowId] ?: rowId
    }

    companion object {
        private const val EXTRA_SAVE_STATE_COLUMN_INDEX_TO_ID = "EXTRA_SAVE_STATE_COLUMN_INDEX_TO_ID"
        private const val EXTRA_SAVE_STATE_COLUMN_ID_TO_INDEX = "EXTRA_SAVE_STATE_COLUMN_ID_TO_INDEX"
        private const val EXTRA_SAVE_STATE_ROW_INDEX_TO_ID = "EXTRA_SAVE_STATE_ROW_INDEX_TO_ID"
        private const val EXTRA_SAVE_STATE_ROW_ID_TO_INDEX = "EXTRA_SAVE_STATE_ROW_ID_TO_INDEX"
    }
}
