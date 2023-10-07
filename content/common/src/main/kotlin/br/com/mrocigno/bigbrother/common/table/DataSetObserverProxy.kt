package br.com.mrocigno.bigbrother.common.table

internal class DataSetObserverProxy(private val mAdaptiveTableAdapter: AdaptiveTableAdapter<*>) :
    AdaptiveTableDataSetObserver {
    override fun notifyDataSetChanged() {
        mAdaptiveTableAdapter.notifyDataSetChanged()
    }

    override fun notifyLayoutChanged() {
        mAdaptiveTableAdapter.notifyLayoutChanged()
    }

    override fun notifyItemChanged(rowIndex: Int, columnIndex: Int) {
        mAdaptiveTableAdapter.notifyItemChanged(rowIndex, columnIndex)
    }

    override fun notifyRowChanged(rowIndex: Int) {
        mAdaptiveTableAdapter.notifyRowChanged(rowIndex)
    }

    override fun notifyColumnChanged(columnIndex: Int) {
        mAdaptiveTableAdapter.notifyColumnChanged(columnIndex)
    }
}
