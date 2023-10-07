package br.com.mrocigno.bigbrother.common.table

import android.view.ViewGroup

/**
 * Base AdaptiveTableAdapter Interface for AdaptiveTableLayout's adapter.
 *
 * @param <VH> Implementation of ViewHolder [ViewHolder]
</VH> */
interface AdaptiveTableAdapter<VH : ViewHolder?> : AdaptiveTableDataSetObserver {
    /**
     * Set new item click listener
     *
     * @param onItemClickListener new item click listener
     */
    var onItemClickListener: OnItemClickListener?

    /**
     * Set new item long click listener
     *
     * @param onItemLongClickListener new item long click listener
     */
    var onItemLongClickListener: OnItemLongClickListener?

    /**
     * Register an observer that is called when changes happen to the data used
     * by this adapter.
     *
     * @param observer the object that gets notified when the data set changes.
     */
    fun registerDataSetObserver(observer: AdaptiveTableDataSetObserver)

    /**
     * Unregister an observer that has previously been registered with this
     * adapter via [.registerDataSetObserver].
     *
     * @param observer the object to unregister.
     */
    fun unregisterDataSetObserver(observer: AdaptiveTableDataSetObserver)

    /**
     * How many rows are in the data table represented by this Adapter.
     *
     * @return count of rows with header
     */
    val rowCount: Int

    /**
     * How many columns are in the data table represented by this Adapter.
     *
     * @return count of columns with header
     */
    val columnCount: Int

    /**
     * Called when [AdaptiveTableLayout] needs a new ITEM [ViewHolder]
     *
     * @param parent The ViewGroup into which the new View will be added after it is bound to
     * an adapter position.
     * @return A new ViewHolder that holds a View of the given view type.
     * @see .onBindViewHolder
     */
    fun onCreateItemViewHolder(parent: ViewGroup): VH

    /**
     * Called when [AdaptiveTableLayout] needs a new COLUMN HEADER ITEM [ViewHolder]
     *
     * @param parent The ViewGroup into which the new View will be added after it is bound to
     * an adapter position.
     * @return A new ViewHolder that holds a View of the given view type.
     * @see .onBindHeaderColumnViewHolder
     */
    fun onCreateColumnHeaderViewHolder(parent: ViewGroup): VH

    /**
     * Called when [AdaptiveTableLayout] needs a new ROW HEADER ITEM [ViewHolder]
     *
     * @param parent The ViewGroup into which the new View will be added after it is bound to
     * an adapter position.
     * @return A new ViewHolder that holds a View of the given view type.
     * @see .onBindHeaderRowViewHolder
     */
    fun onCreateRowHeaderViewHolder(parent: ViewGroup): VH

    /**
     * Called when [AdaptiveTableLayout] needs a new LEFT TOP HEADER ITEM [ViewHolder]
     *
     * @param parent The ViewGroup into which the new View will be added after it is bound to
     * an adapter position.
     * @return A new ViewHolder that holds a View of the given view type.
     * @see .onBindLeftTopHeaderViewHolder
     */
    fun onCreateLeftTopHeaderViewHolder(parent: ViewGroup): VH

    /**
     * Called by [AdaptiveTableLayout] to display the data at the specified position. This method should
     * update the contents of the [ViewHolder.getItemView] to reflect the item at the given
     * position.
     *
     * @param viewHolder The ITEM [ViewHolder] which should be updated to represent the contents of the
     * item at the given position in the data set.
     * @param row        The row index of the item within the adapter's data set.
     * @param column     The column index of the item within the adapter's data set.
     */
    fun onBindViewHolder(viewHolder: VH, row: Int, column: Int)

    /**
     * Called by [AdaptiveTableLayout] to display the data at the specified position. This method should
     * update the contents of the [ViewHolder.getItemView] to reflect the item at the given
     * position.
     *
     * @param viewHolder The COLUMN HEADER ITEM [ViewHolder] which should be updated to represent the contents of the
     * item at the given position in the data set.
     * @param column     The column index of the item within the adapter's data set.
     */
    fun onBindHeaderColumnViewHolder(viewHolder: VH, column: Int)

    /**
     * Called by [AdaptiveTableLayout] to display the data at the specified position. This method should
     * update the contents of the [ViewHolder.getItemView] to reflect the item at the given
     * position.
     *
     * @param viewHolder The ROW HEADER ITEM [ViewHolder] which should be updated to represent the contents of the
     * item at the given position in the data set.
     * @param row        The row index of the item within the adapter's data set.
     */
    fun onBindHeaderRowViewHolder(viewHolder: VH, row: Int)

    /**
     * Called by [AdaptiveTableLayout] to display the data at the specified position. This method should
     * update the contents of the [ViewHolder.getItemView] to reflect the item at the given
     * position.
     *
     * @param viewHolder The TOP LEFT HEADER ITEM[ViewHolder] which should be updated to represent the contents of the
     * item at the given position in the data set.
     */
    fun onBindLeftTopHeaderViewHolder(viewHolder: VH)

    /**
     * Return the width of the column.
     *
     * @param column the column index.
     * @return The width of the column, in pixels.
     */
    fun getColumnWidth(column: Int): Int

    /**
     * Return the header column height.
     *
     * @return The header height of the columns, in pixels.
     */
    val headerColumnHeight: Int

    /**
     * Return the height of the row.
     *
     * @param row the row index.
     * @return The height of the row, in pixels.
     */
    fun getRowHeight(row: Int): Int

    /**
     * Return the header row width.
     *
     * @return The header width of the rows, in pixels.
     */
    val headerRowWidth: Int

    /**
     * Called when a view created by this adapter has been recycled.
     *
     *
     *
     * A view is recycled when a [AdaptiveTableLayout] decides that it no longer
     * needs to be attached. This can be because it has
     * fallen out of visibility or a set of cached views represented by views still
     * attached to the parent RecyclerView. If an item view has large or expensive data
     * bound to it such as large bitmaps, this may be a good place to release those
     * resources.
     *
     *
     * [AdaptiveTableLayout] calls this method right before clearing ViewHolder's internal data and
     * sending it to Recycler.
     *
     * @param viewHolder The [ViewHolder] for the view being recycled
     */
    fun onViewHolderRecycled(viewHolder: VH)
}
