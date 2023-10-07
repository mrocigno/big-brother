package br.com.mrocigno.bigbrother.common.table

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.ViewGroup

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
    private var mColumnIndexToId: HashMap<Int, Int>?
    private var mColumnIdToIndex: HashMap<Int, Int>?

    /**
     * Redirect row's ids
     */
    private var mRowIndexToId: HashMap<Int, Int>?
    private var mRowIdToIndex: HashMap<Int, Int>?

    init {

        // init data
        mColumnIndexToId = HashMap()
        mColumnIdToIndex = HashMap()
        mRowIndexToId = HashMap()
        mRowIdToIndex = HashMap()
    }

    public override fun onItemLongClick(row: Int, column: Int) {
        val innerListener: OnItemLongClickListener? = mInner.onItemLongClickListener
        if (innerListener != null) {
            innerListener.onItemLongClick(rowIndexToId(row), columnIndexToId(column))
        }
    }

    public override fun onLeftTopHeaderLongClick() {
        val innerListener: OnItemLongClickListener? = mInner.onItemLongClickListener
        if (innerListener != null) {
            innerListener.onLeftTopHeaderLongClick()
        }
    }

    public override fun onItemClick(row: Int, column: Int) {
        val innerListener: OnItemClickListener? = mInner.onItemClickListener
        if (innerListener != null) {
            val tempRow: Int = row + 1 // need to merge matrix with table headers and without.
            val tempColumn: Int = column + 1 // need to merge matrix with table headers and without.
            innerListener.onItemClick(rowIndexToId(tempRow), columnIndexToId(tempColumn))
        }
    }

    public override fun onRowHeaderClick(row: Int) {
        val innerListener: OnItemClickListener? = mInner.onItemClickListener
        if (innerListener != null) {
            val tempRow: Int = row + 1 // need to merge matrix with table headers and without.
            innerListener.onRowHeaderClick(if (mIsSolidRowHeader) rowIndexToId(tempRow) else tempRow)
        }
    }

    public override fun onColumnHeaderClick(column: Int) {
        val innerListener: OnItemClickListener? = mInner.onItemClickListener
        if (innerListener != null) {
            val tempColumn: Int = column + 1 // need to merge matrix with table headers and without.
            innerListener.onColumnHeaderClick(columnIndexToId(tempColumn))
        }
    }

    public override fun onLeftTopHeaderClick() {
        val innerListener: OnItemClickListener? = mInner.onItemClickListener
        if (innerListener != null) {
            innerListener.onLeftTopHeaderClick()
        }
    }

    public override fun changeColumns(columnIndex: Int, columnToIndex: Int) {
        val tempColumnIndex: Int =
            columnIndex + 1 // need to merge matrix with table headers and without.
        val tempColumnToIndex: Int =
            columnToIndex + 1 // need to merge matrix with table headers and without.
        val fromId: Int = columnIndexToId(tempColumnIndex)
        val toId: Int = columnIndexToId(tempColumnToIndex)
        if (tempColumnIndex != toId) {
            mColumnIndexToId!!.put(tempColumnIndex, toId)
            mColumnIdToIndex!!.put(toId, tempColumnIndex)
        } else {
            //remove excess modifications
            mColumnIndexToId!!.remove(tempColumnIndex)
            mColumnIdToIndex!!.remove(toId)
        }
        if (tempColumnToIndex != fromId) {
            mColumnIndexToId!!.put(tempColumnToIndex, fromId)
            mColumnIdToIndex!!.put(fromId, tempColumnToIndex)
        } else {
            //remove excess modifications
            mColumnIndexToId!!.remove(tempColumnToIndex)
            mColumnIdToIndex!!.remove(fromId)
        }
    }

    public override fun changeRows(rowIndex: Int, rowToIndex: Int, solidRowHeader: Boolean) {
        val tempRowIndex: Int = rowIndex + 1 // need to merge matrix with table headers and without.
        val tempRowToIndex: Int =
            rowToIndex + 1 // need to merge matrix with table headers and without.
        mIsSolidRowHeader = solidRowHeader
        val fromId: Int = rowIndexToId(tempRowIndex)
        val toId: Int = rowIndexToId(tempRowToIndex)
        if (tempRowIndex != toId) {
            mRowIndexToId!!.put(tempRowIndex, toId)
            mRowIdToIndex!!.put(toId, tempRowIndex)
        } else {
            mRowIndexToId!!.remove(tempRowIndex)
            mRowIdToIndex!!.remove(toId)
        }
        if (tempRowToIndex != fromId) {
            mRowIndexToId!!.put(tempRowToIndex, fromId)
            mRowIdToIndex!!.put(fromId, tempRowToIndex)
        } else {
            mRowIndexToId!!.remove(tempRowToIndex)
            mRowIdToIndex!!.remove(fromId)
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

    public override fun onCreateItemViewHolder(parent: ViewGroup): VH {
        return mInner.onCreateItemViewHolder(parent)
    }

    public override fun onCreateColumnHeaderViewHolder(parent: ViewGroup): VH {
        return mInner.onCreateColumnHeaderViewHolder(parent)
    }

    public override fun onCreateRowHeaderViewHolder(parent: ViewGroup): VH {
        return mInner.onCreateRowHeaderViewHolder(parent)
    }

    public override fun onCreateLeftTopHeaderViewHolder(parent: ViewGroup): VH {
        return mInner.onCreateLeftTopHeaderViewHolder(parent)
    }

    public override fun onBindViewHolder(viewHolder: VH, row: Int, column: Int) {
        val tempRow: Int = row + 1 // need to merge matrix with table headers and without.
        val tempColumn: Int = column + 1 // need to merge matrix with table headers and without.
        mInner.onBindViewHolder(viewHolder, rowIndexToId(tempRow), columnIndexToId(tempColumn))
    }

    public override fun onBindHeaderColumnViewHolder(viewHolder: VH, column: Int) {
        val tempColumn: Int = column + 1 // need to merge matrix with table headers and without.
        mInner.onBindHeaderColumnViewHolder(viewHolder, columnIndexToId(tempColumn))
    }

    public override fun onBindHeaderRowViewHolder(viewHolder: VH, row: Int) {
        val tempRow: Int = row + 1 // need to merge matrix with table headers and without.
        mInner.onBindHeaderRowViewHolder(
            viewHolder,
            if (mIsSolidRowHeader) rowIndexToId(tempRow) else tempRow
        )
    }

    public override fun onBindLeftTopHeaderViewHolder(viewHolder: VH) {
        mInner.onBindLeftTopHeaderViewHolder(viewHolder)
    }

    public override fun getColumnWidth(column: Int): Int {
        val tempColumn: Int = column + 1 // need to merge matrix with table headers and without.
        return mInner.getColumnWidth(columnIndexToId(tempColumn))
    }

    override val headerColumnHeight: Int
        get() {
            return mInner.headerColumnHeight
        }

    public override fun getRowHeight(row: Int): Int {
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

    public override fun onViewHolderRecycled(viewHolder: VH) {
        mInner.onViewHolderRecycled(viewHolder)
    }

    public override fun onSaveInstanceState(bundle: Bundle) {
        bundle.putSerializable(EXTRA_SAVE_STATE_COLUMN_INDEX_TO_ID, mColumnIndexToId)
        bundle.putSerializable(EXTRA_SAVE_STATE_COLUMN_ID_TO_INDEX, mColumnIdToIndex)
        bundle.putSerializable(EXTRA_SAVE_STATE_ROW_INDEX_TO_ID, mRowIndexToId)
        bundle.putSerializable(EXTRA_SAVE_STATE_ROW_ID_TO_INDEX, mRowIdToIndex)
    }

    public override fun onRestoreInstanceState(bundle: Bundle) {
        mColumnIndexToId =
            bundle.getSerializable(EXTRA_SAVE_STATE_COLUMN_INDEX_TO_ID) as HashMap<Int, Int>?
        mColumnIdToIndex =
            bundle.getSerializable(EXTRA_SAVE_STATE_COLUMN_ID_TO_INDEX) as HashMap<Int, Int>?
        mRowIndexToId =
            bundle.getSerializable(EXTRA_SAVE_STATE_ROW_INDEX_TO_ID) as HashMap<Int, Int>?
        mRowIdToIndex =
            bundle.getSerializable(EXTRA_SAVE_STATE_ROW_ID_TO_INDEX) as HashMap<Int, Int>?
    }

    public override fun notifyItemChanged(rowIndex: Int, columnIndex: Int) {
        super.notifyItemChanged(rowIdToIndex(rowIndex), columnIdToIndex(columnIndex))
    }

    public override fun notifyRowChanged(rowIndex: Int) {
        super.notifyRowChanged(rowIdToIndex(rowIndex))
    }

    public override fun notifyColumnChanged(columnIndex: Int) {
        super.notifyColumnChanged(columnIdToIndex(columnIndex))
    }

    val rowsModifications: Map<Int, Int>?
        get() {
            return mRowIndexToId
        }
    val columnsModifications: Map<Int, Int>?
        get() {
            return mColumnIndexToId
        }

    private fun columnIndexToId(columnIndex: Int): Int {
        val id: Int? = mColumnIndexToId!!.get(columnIndex)
        return if (id != null) id else columnIndex
    }

    private fun columnIdToIndex(columnId: Int): Int {
        val index: Int? = mColumnIdToIndex!!.get(columnId)
        return if (index != null) index else columnId
    }

    private fun rowIndexToId(rowIndex: Int): Int {
        val id: Int? = mRowIndexToId!!.get(rowIndex)
        return if (id != null) id else rowIndex
    }

    private fun rowIdToIndex(rowId: Int): Int {
        val index: Int? = mRowIdToIndex!!.get(rowId)
        return if (index != null) index else rowId
    }

    companion object {
        private val EXTRA_SAVE_STATE_COLUMN_INDEX_TO_ID: String =
            "EXTRA_SAVE_STATE_COLUMN_INDEX_TO_ID"
        private val EXTRA_SAVE_STATE_COLUMN_ID_TO_INDEX: String =
            "EXTRA_SAVE_STATE_COLUMN_ID_TO_INDEX"
        private val EXTRA_SAVE_STATE_ROW_INDEX_TO_ID: String = "EXTRA_SAVE_STATE_ROW_INDEX_TO_ID"
        private val EXTRA_SAVE_STATE_ROW_ID_TO_INDEX: String = "EXTRA_SAVE_STATE_ROW_ID_TO_INDEX"
    }
}
