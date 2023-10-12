/*
* A copy/past from https://github.com/Cleveroad/AdaptiveTableLayout
*/

package br.com.mrocigno.bigbrother.common.table

import androidx.collection.SparseArrayCompat
import java.util.LinkedList

/**
 * Custom matrix realisation to hold Objects
 *
 * @param <TObj> Object
</TObj> */
internal class SparseMatrix<TObj>() {
    private val mData: SparseArrayCompat<SparseArrayCompat<TObj>>

    init {
        mData = SparseArrayCompat()
    }

    /**
     * Put item to the matrix in row, column position.
     *
     * @param row    item row position
     * @param column item column  position
     * @param item   Object
     */
    fun put(row: Int, column: Int, item: TObj) {
        var array: SparseArrayCompat<TObj>? = mData.get(row)
        if (array == null) {
            array = SparseArrayCompat()
            array.put(column, item)
            mData.put(row, array)
        } else {
            array.put(column, item)
        }
    }

    /**
     * Get Object from matrix by row and column.
     *
     * @param row    item row position
     * @param column item column position
     * @return Object in row, column position in the matrix
     */
    operator fun get(row: Int, column: Int): TObj? {
        val array: SparseArrayCompat<TObj>? = mData.get(row)
        return if (array == null) null else array.get(column)
    }

    /**
     * Get all row's items
     *
     * @param row row index
     * @return Collection with row's Objects
     */
    fun getRowItems(row: Int): Collection<TObj> {
        val result: MutableCollection<TObj> = LinkedList()
        val array: SparseArrayCompat<TObj> = mData[row]!!
        val count: Int = array.size()
        var i: Int = 0
        while (i < count) {
            val key: Int = array.keyAt(i)
            val columnObj: TObj? = array.get(key)
            if (columnObj != null) {
                result.add(columnObj)
            }
            i++
        }
        return result
    }

    /**
     * Get all column's items
     *
     * @param column column index
     * @return Collection with column's Objects
     */
    fun getColumnItems(column: Int): Collection<TObj> {
        val result: MutableCollection<TObj> = LinkedList()
        val count: Int = mData.size()
        var i: Int = 0
        while (i < count) {
            val key: Int = mData.keyAt(i)
            val columnObj: TObj? = mData[key]?.get(column)
            if (columnObj != null) {
                result.add(columnObj)
            }
            i++
        }
        return result
    }

    val all: Collection<TObj>
        /**
         * Get all matrix's items
         *
         * @return Collection with column's Objects
         */
        get() {
            val result: MutableCollection<TObj> = LinkedList()
            val countR: Int = mData.size()
            var i: Int = 0
            while (i < countR) {
                val rowKey: Int = mData.keyAt(i)
                val columns: SparseArrayCompat<TObj> = mData[rowKey]!!
                val countC: Int = columns.size()
                var j: Int = 0
                while (j < countC) {
                    val key: Int = columns.keyAt(j)
                    result.add(columns[key]!!)
                    j++
                }
                i++
            }
            return result
        }

    /**
     * Remove item in row, column position int the matrix
     *
     * @param row    item row position
     * @param column item column position
     */
    fun remove(row: Int, column: Int) {
        val array: SparseArrayCompat<TObj>? = mData.get(row)
        if (array != null) {
            array.remove(column)
        }
    }
}
