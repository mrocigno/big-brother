package br.com.mrocigno.bigbrother.common.table

/**
 * {@inheritDoc}
 * Common base class of common implementation for an [AdaptiveTableAdapter] that
 * can be used in [AdaptiveTableLayout].
 */
abstract class LinkedAdaptiveTableAdapter<VH : ViewHolder> : AdaptiveTableAdapter<VH> {
    var isRtl: Boolean = false

    /**
     * Set with observers
     */
    private val mAdaptiveTableDataSetObservers: MutableList<AdaptiveTableDataSetObserver> =
        ArrayList()

    /**
     * Need to throw item click action
     */
    override var onItemClickListener: OnItemClickListener? = null

    /**
     * Need to throw long item click action
     */
    override var onItemLongClickListener: OnItemLongClickListener? = null

    val adaptiveTableDataSetObservers: List<AdaptiveTableDataSetObserver>
        get() {
            return mAdaptiveTableDataSetObservers
        }

    /**
     * {@inheritDoc}
     *
     * @param observer the object that gets notified when the data set changes.
     */
    public override fun registerDataSetObserver(observer: AdaptiveTableDataSetObserver) {
        mAdaptiveTableDataSetObservers.add(observer)
    }

    /**
     * {@inheritDoc}
     *
     * @param observer the object to unregister.
     */
    public override fun unregisterDataSetObserver(observer: AdaptiveTableDataSetObserver) {
        mAdaptiveTableDataSetObservers.remove(observer)
    }

    /**
     * {@inheritDoc}
     */
    public override fun notifyDataSetChanged() {
        for (observer: AdaptiveTableDataSetObserver in mAdaptiveTableDataSetObservers) {
            observer.notifyDataSetChanged()
        }
    }

    /**
     * {@inheritDoc}
     *
     * @param rowIndex    the row index
     * @param columnIndex the column index
     */
    public override fun notifyItemChanged(rowIndex: Int, columnIndex: Int) {
        for (observer: AdaptiveTableDataSetObserver in mAdaptiveTableDataSetObservers) {
            observer.notifyItemChanged(rowIndex, columnIndex)
        }
    }

    /**
     * {@inheritDoc}
     *
     * @param rowIndex the row index
     */
    public override fun notifyRowChanged(rowIndex: Int) {
        for (observer: AdaptiveTableDataSetObserver in mAdaptiveTableDataSetObservers) {
            observer.notifyRowChanged(rowIndex)
        }
    }

    /**
     * {@inheritDoc}
     *
     * @param columnIndex the column index
     */
    public override fun notifyColumnChanged(columnIndex: Int) {
        for (observer: AdaptiveTableDataSetObserver in mAdaptiveTableDataSetObservers) {
            observer.notifyColumnChanged(columnIndex)
        }
    }

    /**
     * {@inheritDoc}
     */
    public override fun notifyLayoutChanged() {
        for (observer: AdaptiveTableDataSetObserver in mAdaptiveTableDataSetObservers) {
            observer.notifyLayoutChanged()
        }
    }

    public override fun onViewHolderRecycled(viewHolder: VH) {
        //do something
    }
}
