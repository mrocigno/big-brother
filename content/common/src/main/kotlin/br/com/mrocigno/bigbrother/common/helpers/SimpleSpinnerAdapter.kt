package br.com.mrocigno.bigbrother.common.helpers

import android.content.Context
import android.widget.ArrayAdapter
import android.widget.Filter

class SimpleSpinnerAdapter<T>(
    context: Context,
    var values: Array<T>,
    layout: Int = android.R.layout.simple_list_item_1,
) : ArrayAdapter<T>(context, layout, values) {

    override fun getFilter() = object: Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val results = FilterResults()
            results.values = values
            results.count = values.size
            return results
        }
        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            notifyDataSetChanged()
        }
    }
}