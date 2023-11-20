/*
* A copy/past from https://github.com/Cleveroad/AdaptiveTableLayout
*/

package br.com.mrocigno.bigbrother.common.table

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Point
import android.graphics.Rect
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.collection.SparseArrayCompat
import androidx.core.content.withStyledAttributes
import androidx.core.view.NestedScrollingChild2
import androidx.core.view.NestedScrollingChildHelper
import androidx.core.view.ViewCompat
import br.com.mrocigno.bigbrother.common.R
import br.com.mrocigno.bigbrother.common.table.AdaptiveTableState.Companion.NO_DRAGGING_POSITION
import br.com.mrocigno.bigbrother.common.table.ScrollType.Companion.SCROLL_HORIZONTAL
import br.com.mrocigno.bigbrother.common.table.ScrollType.Companion.SCROLL_VERTICAL
import br.com.mrocigno.bigbrother.common.table.ViewHolderType.Companion.COLUMN_HEADER
import br.com.mrocigno.bigbrother.common.table.ViewHolderType.Companion.FIRST_HEADER
import br.com.mrocigno.bigbrother.common.table.ViewHolderType.Companion.ITEM
import br.com.mrocigno.bigbrother.common.table.ViewHolderType.Companion.ROW_HEADER
import br.com.mrocigno.bigbrother.common.utils.getParcelableCompat
import kotlin.math.roundToInt

class AdaptiveTableLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr),
    ScrollHelper.ScrollHelperListener,
    AdaptiveTableDataSetObserver,
    NestedScrollingChild2 {

    private val viewHolders: SparseMatrix<ViewHolder> = SparseMatrix()
    private val headerColumnViewHolders = SparseArrayCompat<ViewHolder>()
    private val headerRowViewHolders = SparseArrayCompat<ViewHolder>()
    private val dragAndDropPoints = DragAndDropPoints()
    private val state = AdaptiveTableState()

    private var layoutDirection: Int = ViewCompat.getLayoutDirection(this)
    private val layoutDirectionHelper = LayoutDirectionHelper(layoutDirection)
    private val manager = AdaptiveTableManagerRTL(layoutDirectionHelper)

    private val lastSwitchHeaderPoint = Point()
    private val visibleArea = Rect()
    private var leftTopViewHolder: ViewHolder? = null
    private var adapter: DataAdaptiveTableLayoutAdapter<ViewHolder>? = null
    private val recycler = Recycler()
    private val settings = AdaptiveTableLayoutSettings()
    private val scrollHelper = ScrollHelper(context).setListener(this)
    private val scrollerRunnable = SmoothScrollRunnable(this)
    private val scrollerDragAndDropRunnable = DragAndDropScrollRunnable(this)
    private val shadowHelper = ShadowHelper(layoutDirectionHelper)
    private var mSaver: TableInstanceSaver? = null

    val isRTL: Boolean get() = layoutDirectionHelper.isRTL

    init {
        context.withStyledAttributes(attrs, R.styleable.AdaptiveTableLayout, defStyleAttr, 0) {
            settings.setHeaderFixed(getBoolean(R.styleable.AdaptiveTableLayout_fixedHeaders, true))
            settings.setCellMargin(getDimensionPixelSize(R.styleable.AdaptiveTableLayout_cellMargin, 0))
            settings.setSolidRowHeader(getBoolean(R.styleable.AdaptiveTableLayout_solidRowHeaders, true))
            settings.isDragAndDropEnabled = getBoolean(R.styleable.AdaptiveTableLayout_dragAndDropEnabled, true)
        }
    }

    override fun setLayoutDirection(layoutDirection: Int) {
        super.setLayoutDirection(layoutDirection)
        this.layoutDirection = layoutDirection
        layoutDirectionHelper.layoutDirection = layoutDirection
        shadowHelper.onLayoutDirectionChanged()
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        if (changed) {
            // calculate layout width and height
            settings.setLayoutWidth(r - l)
            settings.setLayoutHeight(b - t)
            // init data
            initItems()
        }
    }

    override fun onSaveInstanceState(): Parcelable {
        val bundle = Bundle()
        bundle.putParcelable(EXTRA_STATE_SUPER, super.onSaveInstanceState())
        mSaver = TableInstanceSaver().apply {
            mScrollX = state.scrollX
            mScrollY = state.scrollY
            mLayoutDirection = layoutDirection
            mFixedHeaders = settings.isHeaderFixed
        }
        adapter?.onSaveInstanceState(bundle)
        bundle.putParcelable(EXTRA_STATE_VIEW_GROUP, mSaver)
        return bundle
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        var result = state
        if (state is Bundle) {
            state.getParcelableCompat<Parcelable>(EXTRA_STATE_VIEW_GROUP)?.let { parcel ->
                if (parcel is TableInstanceSaver) {
                    mSaver = parcel
                    layoutDirection = parcel.mLayoutDirection
                    layoutDirection = parcel.mLayoutDirection
                    settings.setHeaderFixed(parcel.mFixedHeaders)
                }
            }
            adapter?.onRestoreInstanceState(state)
            result = state.getParcelableCompat(EXTRA_STATE_SUPER)!!
        }
        super.onRestoreInstanceState(result)
    }

    private fun initItems() {
        val adapter = adapter ?: run {
            manager.clear()
            recycleViewHolders(true)
            return
        }

        // init manager. Not include headers
        manager.init(adapter.rowCount - 1, adapter.columnCount - 1)

        // calculate widths
        repeat(manager.columnCount) {
            val item = adapter.getColumnWidth(it)
            manager.putColumnWidth(it, item)
        }

        // calculate heights
        repeat(manager.rowCount) {
            val item = adapter.getRowHeight(it)
            manager.putRowHeight(it, item)
        }

        // set header's width and height. Set 0 in case < 0
        manager.headerColumnHeight = 0.coerceAtLeast(adapter.headerColumnHeight)
        manager.headerRowWidth = 0.coerceAtLeast(adapter.headerRowWidth)

        // start calculating full width and full height
        manager.invalidate()

        // show items in this area
        visibleArea[
            state.scrollX,
            state.scrollY,
            state.scrollX + settings.layoutWidth
        ] = state.scrollY + settings.layoutHeight
        addViewHolders(visibleArea)

        if (mSaver != null) {
            scrollTo(mSaver!!.mScrollX, mSaver!!.mScrollY)
            mSaver = null
        } else if (isRTL) {
            scrollTo(settings.layoutWidth, 0)
        }
    }

    /**
     * Set adapter with IMMUTABLE data.
     * Create wrapper with links between layout rows, columns and data rows, columns.
     * On drag and drop event just change links but not change data in adapter.
     *
     * @param adapter AdaptiveTableLayout adapter
     */
    fun setAdapter(adapter: AdaptiveTableAdapter<ViewHolder>?) {
        // remove observers from old adapter
        this.adapter?.unregisterDataSetObserver(this)

        this.adapter = adapter?.let {
            LinkedAdaptiveTableAdapterImpl(it, settings.isSolidRowHeader).apply {
                registerDataSetObserver(this@AdaptiveTableLayout)
                adapter.registerDataSetObserver(DataSetObserverProxy(this@apply))
            }
        }

        initItems()
    }

    /**
     * Set adapter with MUTABLE data.
     * You need to implement switch rows and columns methods.
     * On drag and drop event calls [DataAdaptiveTableLayoutAdapter.changeColumns] and
     * [DataAdaptiveTableLayoutAdapter.changeRows]
     *
     *
     * DO NOT USE WITH BIG DATA!!
     *
     * @param adapter DataAdaptiveTableLayoutAdapter adapter
     */
    fun setAdapter(adapter: DataAdaptiveTableLayoutAdapter<ViewHolder>?) {
        this.adapter?.unregisterDataSetObserver(this)

        this.adapter = adapter?.apply {
            registerDataSetObserver(this)
        }

        if (settings.layoutHeight != 0 && settings.layoutWidth != 0) {
            initItems()
        }
    }

    val linkedAdapterRowsModifications: Map<Int, Int>?
        /**
         * When used adapter with IMMUTABLE data, returns rows position modifications
         * (old position -> new position)
         *
         * @return row position modification map. Includes only modified row numbers
         */
        get() = if (adapter is LinkedAdaptiveTableAdapterImpl<*>) (adapter as LinkedAdaptiveTableAdapterImpl<*>).rowsModifications
                else emptyMap()

    val linkedAdapterColumnsModifications: Map<Int, Int>?
        /**
         * When used adapter with IMMUTABLE data, returns columns position modifications
         * (old position -> new position)
         *
         * @return row position modification map. Includes only modified column numbers
         */
        get() = if (adapter is LinkedAdaptiveTableAdapterImpl<*>) (adapter as LinkedAdaptiveTableAdapterImpl<*>).columnsModifications
                else emptyMap()

    override fun scrollTo(x: Int, y: Int) {
        scrollBy(x, y)
    }

    override fun scrollBy(x: Int, y: Int) {
        // block scroll one axle
        val tempX = if (state.isRowDragging) 0 else x
        val tempY = if (state.isColumnDragging) 0 else y
        var diffX = tempX
        var diffY = tempY
        val shadowShiftX = manager.columnCount * settings.cellMargin
        val shadowShiftY = manager.rowCount * settings.cellMargin
        val maxX = manager.fullWidth + shadowShiftX
        val maxY = manager.fullHeight + shadowShiftY

        when {
            state.scrollX + tempX <= 0 -> {
                // scroll over view to the left
                diffX = state.scrollX
                state.scrollX = 0
            }

            settings.layoutWidth > maxX -> {
                // few items and we have free space.
                diffX = 0
                state.scrollX = 0
            }

            state.scrollX + settings.layoutWidth + tempX > maxX -> {
                // scroll over view to the right
                diffX = (maxX - state.scrollX - settings.layoutWidth).toInt()
                state.scrollX = state.scrollX + diffX
            }

            else -> {
                state.scrollX = state.scrollX + tempX
            }
        }

        when {
            state.scrollY + tempY <= 0 -> {
                // scroll over view to the top
                diffY = state.scrollY
                state.scrollY = 0
            }

            settings.layoutHeight > maxY -> {
                // few items and we have free space.
                diffY = 0
                state.scrollY = 0
            }

            state.scrollY + settings.layoutHeight + tempY > maxY -> {
                // scroll over view to the bottom
                diffY = (maxY - state.scrollY - settings.layoutHeight).toInt()
                state.scrollY = state.scrollY + diffY
            }

            else -> {
                state.scrollY = state.scrollY + tempY
            }
        }

        if (diffX == 0 && diffY == 0) return

        if (adapter != null) {
            // refresh views
            recycleViewHolders()
            visibleArea[
                state.scrollX,
                state.scrollY,
                state.scrollX + settings.layoutWidth
            ] = state.scrollY + settings.layoutHeight
            addViewHolders(visibleArea)
            refreshViewHolders()
        }
    }

    /**
     * Refresh all view holders
     */
    private fun refreshViewHolders() {
        if (adapter != null) {
            viewHolders.all.forEach {
                refreshItemViewHolder(it, state.isRowDragging, state.isColumnDragging)
            }

            if (state.isColumnDragging) {
                refreshAllColumnHeadersHolders()
                refreshAllRowHeadersHolders()
            } else {
                refreshAllRowHeadersHolders()
                refreshAllColumnHeadersHolders()
            }

            leftTopViewHolder?.run {
                refreshLeftTopHeaderViewHolder(this)
                itemView.bringToFront()
            }
        }
    }

    private fun refreshAllColumnHeadersHolders() {
        repeat(headerColumnViewHolders.size()) {
            val key = headerColumnViewHolders.keyAt(it)
            headerColumnViewHolders[key]?.run(::refreshHeaderColumnViewHolder)
        }
    }

    private fun refreshAllRowHeadersHolders() {
        repeat(headerRowViewHolders.size()) {
            val key = headerRowViewHolders.keyAt(it)
            headerRowViewHolders[key]?.run(::refreshHeaderRowViewHolder)
        }
    }
    /**
     * Refresh current item view holder.
     *
     * @param holder           current view holder
     * @param isRowDragging    row dragging state
     * @param isColumnDragging column dragging state
     */
    private fun refreshItemViewHolder(
        holder: ViewHolder,
        isRowDragging: Boolean = false,
        isColumnDragging: Boolean = false
    ) {
        var left = emptySpace + manager.getColumnsWidth(0, 0.coerceAtLeast(holder.columnIndex))
        var top = manager.getRowsHeight(0, 0.coerceAtLeast(holder.rowIndex))
        val view = holder.itemView

        if (isColumnDragging && holder.isDragging && dragAndDropPoints.offset.x > 0) {
            // visible dragging column. Calculate left offset using drag and drop points.
            left = state.scrollX + dragAndDropPoints.offset.x - view.width / 2
            if (!isRTL) left -= manager.headerRowWidth
            view.bringToFront()
        } else if (isRowDragging && holder.isDragging && dragAndDropPoints.offset.y > 0) {
            // visible dragging row. Calculate top offset using drag and drop points.
            top = state.scrollY + dragAndDropPoints.offset.y - view.height / 2 - manager.headerColumnHeight
            view.bringToFront()
        }

        val leftMargin = holder.columnIndex * settings.cellMargin + settings.cellMargin
        val topMargin = holder.rowIndex * settings.cellMargin + settings.cellMargin
        if (!isRTL) left += manager.headerRowWidth

        // calculate view layout positions
        val viewPosLeft = left - state.scrollX + leftMargin
        val viewPosRight = viewPosLeft + manager.getColumnWidth(holder.columnIndex)
        val viewPosTop = top - state.scrollY + manager.headerColumnHeight + topMargin
        val viewPosBottom = viewPosTop + manager.getRowHeight(holder.rowIndex)

        // update layout position
        view.layout(viewPosLeft, viewPosTop, viewPosRight, viewPosBottom)
    }

    /**
     * Refresh current column header view holder.
     *
     * @param holder current view holder
     */
    private fun refreshHeaderColumnViewHolder(holder: ViewHolder) {
        var left = emptySpace + manager.getColumnsWidth(0, 0.coerceAtLeast(holder.columnIndex))
        val top = if (settings.isHeaderFixed) 0 else -state.scrollY
        val view = holder.itemView
        val leftMargin = holder.columnIndex * settings.cellMargin + settings.cellMargin
        val topMargin = holder.rowIndex * settings.cellMargin + settings.cellMargin

        if (!isRTL) left += manager.headerRowWidth

        if (holder.isDragging && dragAndDropPoints.offset.x > 0) {
            left = state.scrollX + dragAndDropPoints.offset.x - view.width / 2
            view.bringToFront()
        }
        if (holder.isDragging) {
            val leftShadow = shadowHelper.leftShadow
            val rightShadow = shadowHelper.rightShadow
            if (leftShadow != null) {
                val shadowLeft = left - state.scrollX
                leftShadow.layout(
                    (manager.headerRowWidth - state.scrollX).coerceAtLeast(shadowLeft - SHADOW_THICK) + leftMargin,
                    0,
                    shadowLeft + leftMargin,
                    settings.layoutHeight
                )
                leftShadow.bringToFront()
            }
            if (rightShadow != null) {
                val shadowLeft = left + manager.getColumnWidth(holder.columnIndex) - state.scrollX
                rightShadow.layout(
                    (manager.headerRowWidth - state.scrollX).coerceAtLeast(shadowLeft) + leftMargin,
                    0,
                    shadowLeft + SHADOW_THICK + leftMargin,
                    settings.layoutHeight
                )
                rightShadow.bringToFront()
            }
        }

        val viewPosLeft = left - state.scrollX + leftMargin
        val viewPosRight = viewPosLeft + manager.getColumnWidth(holder.columnIndex)
        val viewPosTop = top + topMargin
        val viewPosBottom = viewPosTop + manager.headerColumnHeight

        view.layout(viewPosLeft, viewPosTop, viewPosRight, viewPosBottom)

        if (state.isRowDragging) view.bringToFront()

        if (!state.isColumnDragging) {
            (shadowHelper.columnsHeadersShadow ?: shadowHelper.addColumnsHeadersShadow(this)).apply {
                layout(
                    if (state.isRowDragging) 0 else if (settings.isHeaderFixed) 0 else -state.scrollX,
                    top + manager.headerColumnHeight,
                    settings.layoutWidth,
                    top + manager.headerColumnHeight + SHADOW_HEADERS_THICK
                )
            }.bringToFront()
        }
    }

    /**
     * Refresh current row header view holder.
     *
     * @param holder current view holder
     */
    private fun refreshHeaderRowViewHolder(holder: ViewHolder) {
        var top = manager.getRowsHeight(0, 0.coerceAtLeast(holder.rowIndex)) + manager.headerColumnHeight
        var left = calculateRowHeadersLeft()
        val view = holder.itemView
        val leftMargin = holder.columnIndex * settings.cellMargin + settings.cellMargin
        val topMargin = holder.rowIndex * settings.cellMargin + settings.cellMargin

        if (isRTL) left += settings.cellMargin

        if (holder.isDragging && dragAndDropPoints.offset.y > 0) {
            top = state.scrollY + dragAndDropPoints.offset.y - view.height / 2
            view.bringToFront()
        }

        if (holder.isDragging) {
            val topShadow = shadowHelper.topShadow
            val bottomShadow = shadowHelper.bottomShadow
            if (topShadow != null) {
                val shadowTop = top - state.scrollY
                topShadow.layout(
                    0,
                    (manager.headerColumnHeight - state.scrollY).coerceAtLeast(shadowTop - SHADOW_THICK) + topMargin,
                    settings.layoutWidth,
                    shadowTop + topMargin
                )
                topShadow.bringToFront()
            }
            if (bottomShadow != null) {
                val shadowBottom = top - state.scrollY + manager.getRowHeight(holder.rowIndex)
                bottomShadow.layout(
                    0,
                    (manager.headerColumnHeight - state.scrollY).coerceAtLeast(shadowBottom) + topMargin,
                    settings.layoutWidth,
                    shadowBottom + SHADOW_THICK + topMargin
                )
                bottomShadow.bringToFront()
            }
        }

        view.layout(
            left + leftMargin * if (isRTL) 0 else 1,
            top - state.scrollY + topMargin,
            left + manager.headerRowWidth + leftMargin * if (isRTL) 1 else 0,
            top + manager.getRowHeight(holder.rowIndex) - state.scrollY + topMargin
        )

        if (state.isColumnDragging) view.bringToFront()

        if (!state.isRowDragging) {
            (shadowHelper.rowsHeadersShadow ?: shadowHelper.addRowsHeadersShadow(this)).apply {
                val shadowStart = if (!isRTL) view.right else view.left - SHADOW_HEADERS_THICK
                val shadowEnd = shadowStart + SHADOW_HEADERS_THICK
                layout(
                    shadowStart,
                    if (state.isColumnDragging) 0 else if (settings.isHeaderFixed) 0 else -state.scrollY,
                    shadowEnd,
                    settings.layoutHeight
                )
            }.bringToFront()
        }
    }

    /**
     * Refresh current row header view holder.
     *
     * @param holder current view holder
     */
    private fun refreshLeftTopHeaderViewHolder(holder: ViewHolder) {
        var left = calculateRowHeadersLeft()
        if (isRTL) left += settings.cellMargin
        val top = if (settings.isHeaderFixed) 0 else -state.scrollY
        val view = holder.itemView
        val leftMargin = if (isRTL) 0 else settings.cellMargin
        val topMargin = settings.cellMargin

        view.layout(
            left + leftMargin,
            top + topMargin,
            left + manager.headerRowWidth + leftMargin,
            top + manager.headerColumnHeight + topMargin
        )
    }

    private fun calculateRowHeadersLeft() =
        if (isHeaderFixed) if (!isRTL) 0 else rowHeaderStartX
        else {
            if (!isRTL) -state.scrollX
            else {
                if (manager.fullWidth <= settings.layoutWidth) -state.scrollX + rowHeaderStartX
                else (-state.scrollX
                        + (manager.fullWidth - manager.headerRowWidth).toInt()
                        + (manager.columnCount * settings.cellMargin)
                )
            }
        }

    /**
     * Recycle view holders outside screen
     *
     * @param isRecycleAll recycle all view holders if true
     */
    private fun recycleViewHolders(isRecycleAll: Boolean = false) {
        adapter ?: return

        val headerKeysToRemove = arrayListOf<Int>()

        // item view holders
        viewHolders.all.forEach { holder ->
            if (!holder.isDragging) {
                val view = holder.itemView
                // recycle view holder
                if (isRecycleAll || ((view.right < 0)
                        || (view.left > settings.layoutWidth)
                        || (view.bottom < 0)
                        || (view.top > settings.layoutHeight)
                    )
                ) {
                    // recycle view holder
                    viewHolders.remove(holder.rowIndex, holder.columnIndex)
                    recycleViewHolder(holder)
                }
            }
        }

        // column header view holders
        repeat(headerColumnViewHolders.size()) {
            val key = headerColumnViewHolders.keyAt(it)
            headerColumnViewHolders[key]?.run {
                if (isRecycleAll || itemView.right < 0 || itemView.left > settings.layoutWidth) {
                    headerKeysToRemove.add(key)
                    recycleViewHolder(this)
                }
            }
        }
        removeKeys(headerKeysToRemove, headerColumnViewHolders)

        if (headerKeysToRemove.isNotEmpty()) headerKeysToRemove.clear()

        repeat(headerRowViewHolders.size()) {
            val key = headerRowViewHolders.keyAt(it)
            headerRowViewHolders[key]?.run {
                if (!isDragging && (isRecycleAll || itemView.bottom < 0 || itemView.top > settings.layoutHeight)) {
                    headerKeysToRemove.add(key)
                    recycleViewHolder(this)
                }
            }
        }
        removeKeys(headerKeysToRemove, headerRowViewHolders)
    }

    /**
     * Remove recycled viewHolders from sparseArray
     *
     * @param keysToRemove List of ViewHolders keys that we need to remove
     * @param headers      SparseArray of viewHolders from where we need to remove
     */
    private fun removeKeys(keysToRemove: List<Int>, headers: SparseArrayCompat<ViewHolder>?) {
        for (key: Int in keysToRemove) headers?.remove(key)
    }

    /**
     * Recycle view holder and remove view from layout.
     *
     * @param holder view holder to recycle
     */
    private fun recycleViewHolder(holder: ViewHolder) {
        recycler.pushRecycledView(holder)
        removeView(holder.itemView)
        adapter?.onViewHolderRecycled(holder)
    }

    private val emptySpace: Int
        get() =
            if (isRTL && settings.layoutWidth > manager.fullWidth) settings.layoutWidth -
                    (manager.fullWidth.toInt()) -
                    (manager.columnCount * settings.cellMargin)
            else 0


    /**
     * Create and add view holders with views to the layout.
     *
     * @param filledArea visible rect
     */
    private fun addViewHolders(filledArea: Rect) {
        //search indexes for columns and rows which NEED TO BE showed in this area
        val leftColumn = manager.getColumnByXWithShift(filledArea.left, settings.cellMargin)
        val rightColumn = manager.getColumnByXWithShift(filledArea.right, settings.cellMargin)
        val topRow = manager.getRowByYWithShift(filledArea.top, settings.cellMargin)
        val bottomRow = manager.getRowByYWithShift(filledArea.bottom, settings.cellMargin)

        for (vertical in topRow..bottomRow) {
            for (horizontal in leftColumn..rightColumn) {
                // item view holders
                val viewHolder = viewHolders[vertical, horizontal]
                if (viewHolder == null && adapter != null) {
                    addViewHolder(vertical, horizontal, ITEM)
                }
            }

            // row view headers holders
            val viewHolder = headerRowViewHolders.get(vertical)
            if (viewHolder == null && adapter != null) {
                addViewHolder(
                    vertical,
                    if (isRTL) manager.columnCount else 0,
                    ROW_HEADER
                )
            } else if (viewHolder != null && adapter != null) {
                refreshHeaderRowViewHolder(viewHolder)
            }
        }

        for (horizontal in leftColumn..rightColumn) {
            // column view header holders
            val viewHolder = headerColumnViewHolders.get(horizontal)
            if (viewHolder == null && adapter != null) {
                addViewHolder(0, horizontal, COLUMN_HEADER)
            } else if (viewHolder != null && adapter != null) {
                refreshHeaderColumnViewHolder(viewHolder)
            }
        }

        // add view left top view.
        if (leftTopViewHolder == null && adapter != null) {
            leftTopViewHolder = adapter!!.onCreateLeftTopHeaderViewHolder(this)
            leftTopViewHolder?.itemType = FIRST_HEADER
            val view = leftTopViewHolder?.itemView
            view?.setTag(R.id.tag_view_holder, leftTopViewHolder)
            addView(view, 0)
            adapter!!.onBindLeftTopHeaderViewHolder(leftTopViewHolder!!)
            view?.measure(
                MeasureSpec.makeMeasureSpec(manager.headerRowWidth, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(manager.headerColumnHeight, MeasureSpec.EXACTLY)
            )
            var viewPosLeft = settings.cellMargin
            if (isRTL) viewPosLeft += rowHeaderStartX
            val viewPosRight = viewPosLeft + manager.headerRowWidth
            val viewPosTop = settings.cellMargin
            val viewPosBottom = viewPosTop + manager.headerColumnHeight
            view?.layout(
                viewPosLeft,
                viewPosTop,
                viewPosRight,
                viewPosBottom
            )
        } else if (leftTopViewHolder != null && adapter != null) {
            refreshLeftTopHeaderViewHolder(leftTopViewHolder!!)
        }
    }

    private fun getBindColumn(column: Int) =
        if (!isRTL) column else manager.columnCount - 1 - column

    private fun addViewHolder(row: Int, column: Int, itemType: Int) {
        // need to add new one
        var viewHolder = recycler.popRecycledViewHolder(itemType)
        val createdNewView = viewHolder == null
        if (createdNewView) viewHolder = createViewHolder(itemType)
        viewHolder ?: return

        val adapter = this.adapter ?: return

        // prepare view holder
        viewHolder.rowIndex = row
        viewHolder.columnIndex = column
        viewHolder.itemType = itemType
        val view = viewHolder.itemView
        view.setTag(R.id.tag_view_holder, viewHolder)
        addView(view, 0)

        // save and measure view holder
        if (itemType == ITEM) {
            viewHolders.put(row, column, viewHolder)
            if (createdNewView) {
                // DO NOT REMOVE THIS!! Fix bug with request layout "requestLayout() improperly called"
                adapter.onBindViewHolder(viewHolder, row, getBindColumn(column))
            }
            view.measure(
                MeasureSpec.makeMeasureSpec(manager.getColumnWidth(column), MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(manager.getRowHeight(row), MeasureSpec.EXACTLY)
            )
            refreshItemViewHolder(viewHolder)
            if (!createdNewView) {
                // DO NOT REMOVE THIS!! Fix bug with request layout "requestLayout() improperly called"
                adapter.onBindViewHolder(viewHolder, row, getBindColumn(column))
            }
        } else if (itemType == ROW_HEADER) {
            headerRowViewHolders.put(row, viewHolder)
            if (createdNewView) {
                // DO NOT REMOVE THIS!! Fix bug with request layout "requestLayout() improperly called"
                adapter.onBindHeaderRowViewHolder(viewHolder, row)
            }
            view.measure(
                MeasureSpec.makeMeasureSpec(manager.headerRowWidth, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(manager.getRowHeight(row), MeasureSpec.EXACTLY)
            )
            refreshHeaderRowViewHolder(viewHolder)
            if (!createdNewView) {
                // DO NOT REMOVE THIS!! Fix bug with request layout "requestLayout() improperly called"
                adapter.onBindHeaderRowViewHolder(viewHolder, row)
            }
        } else if (itemType == COLUMN_HEADER) {
            headerColumnViewHolders.put(column, viewHolder)
            if (createdNewView) {
                // DO NOT REMOVE THIS!! Fix bug with request layout "requestLayout() improperly called"
                adapter.onBindHeaderColumnViewHolder(viewHolder, getBindColumn(column))
            }
            view.measure(
                MeasureSpec.makeMeasureSpec(manager.getColumnWidth(column), MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(manager.headerColumnHeight, MeasureSpec.EXACTLY)
            )
            refreshHeaderColumnViewHolder(viewHolder)
            if (!createdNewView) {
                // DO NOT REMOVE THIS!! Fix bug with request layout "requestLayout() improperly called"
                adapter.onBindHeaderColumnViewHolder(viewHolder, getBindColumn(column))
            }
        }
    }

    /**
     * Create view holder by type
     *
     * @param itemType view holder type
     * @return Created view holder
     */
    private fun createViewHolder(itemType: Int) =
        when (itemType) {
            ITEM -> adapter?.onCreateItemViewHolder(this@AdaptiveTableLayout)
            ROW_HEADER -> adapter?.onCreateRowHeaderViewHolder(this@AdaptiveTableLayout)
            COLUMN_HEADER -> adapter?.onCreateColumnHeaderViewHolder(this@AdaptiveTableLayout)
            else -> null
        }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        // intercept event before OnClickListener on item view.
        scrollHelper.onTouch(ev)
        return true
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (state.isDragging) {
            // Drag and drop logic
            if (event.action == MotionEvent.ACTION_UP) {
                // end drag and drop event
                dragAndDropPoints.setEnd(
                    (state.scrollX + event.x).toInt(),
                    (state.scrollY + event.y).toInt()
                )
                lastSwitchHeaderPoint[0] = 0
                return scrollHelper.onTouch(event)
            }
            // calculate absolute x, y
            val absoluteX = (state.scrollX + event.x).toInt() - emptySpace
            val absoluteY = (state.scrollY + event.y).toInt()

            // if column drag and drop mode and column offset > SHIFT_VIEWS_THRESHOLD
            if (state.isColumnDragging) {
                val dragAndDropHolder = headerColumnViewHolders[state.columnDraggingIndex]
                if (dragAndDropHolder != null) {
                    val fromColumn = dragAndDropHolder.columnIndex
                    val toColumn =
                        manager.getColumnByXWithShift(absoluteX, settings.cellMargin)
                    if (fromColumn != toColumn) {
                        val columnWidth = manager.getColumnWidth(toColumn)
                        var absoluteColumnX = manager.getColumnsWidth(0, toColumn)
                        if (!isRTL) {
                            absoluteColumnX += manager.headerRowWidth
                        }
                        if (fromColumn < toColumn) {
                            // left column is dragging one
                            val deltaX = (absoluteColumnX + columnWidth * 0.6f).toInt()
                            if (absoluteX > deltaX) {
                                // move column from left to right
                                for (i in fromColumn until toColumn) {
                                    shiftColumnsViews(i, i + 1)
                                }
                                state.setColumnDragging(true, toColumn)
                            }
                        } else {
                            // right column is dragging one
                            val deltaX = (absoluteColumnX + columnWidth * 0.4f).toInt()
                            if (absoluteX < deltaX) {
                                // move column from right to left
                                for (i in fromColumn downTo toColumn + 1) {
                                    shiftColumnsViews(i - 1, i)
                                }
                                state.setColumnDragging(true, toColumn)
                            }
                        }
                    }
                }
            } else if (state.isRowDragging) {
                val dragAndDropHolder = headerRowViewHolders[state.rowDraggingIndex]
                if (dragAndDropHolder != null) {
                    val fromRow = dragAndDropHolder.rowIndex
                    val toRow = manager.getRowByYWithShift(absoluteY, settings.cellMargin)
                    if (fromRow != toRow) {
                        val rowHeight = manager.getRowHeight(toRow)
                        val absoluteColumnY =
                            manager.getRowsHeight(0, toRow) + manager.headerColumnHeight
                        if (fromRow < toRow) {
                            // left column is dragging one
                            val deltaY = (absoluteColumnY + rowHeight * 0.6f).toInt()
                            if (absoluteY > deltaY) {
                                // move column from left to right
                                for (i in fromRow until toRow) {
                                    shiftRowsViews(i, i + 1)
                                }
                                state.setRowDragging(true, toRow)
                            }
                        } else {
                            // right column is dragging one
                            val deltaY = (absoluteColumnY + rowHeight * 0.4f).toInt()
                            if (absoluteY < deltaY) {
                                // move column from right to left
                                for (i in fromRow downTo toRow + 1) {
                                    shiftRowsViews(i - 1, i)
                                }
                                state.setRowDragging(true, toRow)
                            }
                        }
                    }
                }
            }

            // set drag and drop offset
            dragAndDropPoints.setOffset((event.x).toInt(), (event.y).toInt())

            // intercept touch for scroll in drag and drop mode
            scrollerDragAndDropRunnable.touch(
                event.x.toInt(), event.y.toInt(),
                if (state.isColumnDragging) SCROLL_HORIZONTAL else SCROLL_VERTICAL
            )

            // update positions
            refreshViewHolders()
            return true
        }
        return scrollHelper.onTouch(event)
    }

    /**
     * Method change columns. Change view holders indexes, kay in map, init changing items in adapter.
     *
     * @param fromColumn from column index which need to shift
     * @param toColumn   to column index which need to shift
     */
    private fun shiftColumnsViews(fromColumn: Int, toColumn: Int) {
        if (adapter != null) {

            // change data
            adapter!!.changeColumns(getBindColumn(fromColumn), getBindColumn(toColumn))

            // change view holders
            switchHeaders(
                headerColumnViewHolders,
                fromColumn,
                toColumn,
                COLUMN_HEADER
            )

            // change indexes in array with widths
            manager.switchTwoColumns(fromColumn, toColumn)
            val fromHolders = viewHolders.getColumnItems(fromColumn)
            val toHolders = viewHolders.getColumnItems(toColumn)
            removeViewHolders(fromHolders)
            removeViewHolders(toHolders)
            for (holder: ViewHolder in fromHolders) {
                holder.columnIndex = toColumn
                viewHolders.put(holder.rowIndex, holder.columnIndex, holder)
            }
            for (holder: ViewHolder in toHolders) {
                holder.columnIndex = fromColumn
                viewHolders.put(holder.rowIndex, holder.columnIndex, holder)
            }
        }
    }

    /**
     * Method change rows. Change view holders indexes, kay in map, init changing items in adapter.
     *
     * @param fromRow from row index which need to shift
     * @param toRow   to row index which need to shift
     */
    private fun shiftRowsViews(fromRow: Int, toRow: Int) {
        if (adapter != null) {
            // change data
            adapter!!.changeRows(fromRow, toRow, settings.isSolidRowHeader)

            // change view holders
            switchHeaders(
                headerRowViewHolders,
                fromRow,
                toRow,
                ROW_HEADER
            )

            // change indexes in array with heights
            manager.switchTwoRows(fromRow, toRow)
            val fromHolders = viewHolders.getRowItems(fromRow)
            val toHolders = viewHolders.getRowItems(toRow)
            removeViewHolders(fromHolders)
            removeViewHolders(toHolders)
            for (holder: ViewHolder in fromHolders) {
                holder.rowIndex = toRow
                viewHolders.put(holder.rowIndex, holder.columnIndex, (holder))
            }
            for (holder: ViewHolder in toHolders) {
                holder.rowIndex = fromRow
                viewHolders.put(holder.rowIndex, holder.columnIndex, (holder))
            }

            // update row headers
            if (!settings.isSolidRowHeader) {
                val fromViewHolder = headerRowViewHolders.get(fromRow)
                val toViewHolder = headerRowViewHolders.get(toRow)
                if (fromViewHolder != null) {
                    adapter!!.onBindHeaderRowViewHolder(fromViewHolder, fromRow)
                }
                if (toViewHolder != null) {
                    adapter!!.onBindHeaderRowViewHolder(toViewHolder, toRow)
                }
            }
        }
    }

    /**
     * Method switch view holders in map (map with headers view holders).
     *
     * @param map       header view holder's map
     * @param fromIndex index from view holder
     * @param toIndex   index to view holder
     * @param type      type of items (column header or row header)
     */
    @Suppress("unused")
    private fun switchHeaders(
        map: HashMap<Int, ViewHolder>,
        fromIndex: Int,
        toIndex: Int,
        type: Int
    ) {
        val fromVh = map[fromIndex]
        if (fromVh != null) {
            map.remove(fromIndex)
            if (type == COLUMN_HEADER) {
                fromVh.columnIndex = toIndex
            } else if (type == ROW_HEADER) {
                fromVh.rowIndex = toIndex
            }
        }
        val toVh = map[toIndex]
        if (toVh != null) {
            map.remove(toIndex)
            if (type == COLUMN_HEADER) {
                toVh.columnIndex = fromIndex
            } else if (type == ROW_HEADER) {
                toVh.rowIndex = fromIndex
            }
        }
        if (fromVh != null) {
            map[toIndex] = fromVh
        }
        if (toVh != null) {
            map[fromIndex] = toVh
        }
    }

    /**
     * Method switch view holders in map (map with headers view holders).
     *
     * @param map       header view holder's map
     * @param fromIndex index from view holder
     * @param toIndex   index to view holder
     * @param type      type of items (column header or row header)
     */
    @Suppress("unused")
    private fun switchHeaders(
        map: SparseArrayCompat<ViewHolder>,
        fromIndex: Int,
        toIndex: Int,
        type: Int
    ) {
        val fromVh = map[fromIndex]
        if (fromVh != null) {
            map.remove(fromIndex)
            if (type == COLUMN_HEADER) {
                fromVh.columnIndex = toIndex
            } else if (type == ROW_HEADER) {
                fromVh.rowIndex = toIndex
            }
        }
        val toVh = map[toIndex]
        if (toVh != null) {
            map.remove(toIndex)
            if (type == COLUMN_HEADER) {
                toVh.columnIndex = fromIndex
            } else if (type == ROW_HEADER) {
                toVh.rowIndex = fromIndex
            }
        }
        if (fromVh != null) {
            map.put(toIndex, fromVh)
        }
        if (toVh != null) {
            map.put(fromIndex, toVh)
        }
    }

    /**
     * Remove item view holders from base collection
     *
     * @param toRemove Collection with view holders which need to remove
     */
    private fun removeViewHolders(toRemove: Collection<ViewHolder>?) {
        if (toRemove != null) {
            for (holder: ViewHolder in toRemove) {
                viewHolders.remove(holder.rowIndex, holder.columnIndex)
            }
        }
    }

    private val rowHeaderStartX: Int
        get() = if (isRTL) right - manager.headerRowWidth else 0

    override fun drawChild(canvas: Canvas, child: View, drawingTime: Long): Boolean {
        val viewHolder: ViewHolder? = child.getTag(R.id.tag_view_holder) as? ViewHolder
        canvas.save()
        val headerFixedX = if (settings.isHeaderFixed) rowHeaderStartX else state.scrollX
        val headerFixedY = if (settings.isHeaderFixed) 0 else state.scrollY
        val itemsAndColumnsLeft =
            if (!isRTL) 0.coerceAtLeast(manager.headerRowWidth - headerFixedX) else 0
        var itemsAndColumnsRight = settings.layoutWidth
        if (isRTL) {
            itemsAndColumnsRight += (settings.cellMargin
                    - manager.headerRowWidth * (if (isHeaderFixed) 1 else 0))
        }
        if (viewHolder == null) {
            //ignore
        } else if (viewHolder.itemType == ITEM) {
            // prepare canvas rect area for draw item (cell in table)
            canvas.clipRect(
                itemsAndColumnsLeft,
                0.coerceAtLeast(manager.headerColumnHeight - headerFixedY),
                itemsAndColumnsRight,
                settings.layoutHeight
            )
        } else if (viewHolder.itemType == ROW_HEADER) {
            // prepare canvas rect area for draw row header
            canvas.clipRect(
                rowHeaderStartX - settings.cellMargin * (if (isRTL) 0 else 1),
                0.coerceAtLeast(manager.headerColumnHeight - headerFixedY),
                0.coerceAtLeast(rowHeaderStartX + manager.headerRowWidth + settings.cellMargin),
                settings.layoutHeight
            )
        } else if (viewHolder.itemType == COLUMN_HEADER) {
            // prepare canvas rect area for draw column header
            canvas.clipRect(
                itemsAndColumnsLeft,
                0,
                itemsAndColumnsRight,
                0.coerceAtLeast(manager.headerColumnHeight - headerFixedY)
            )
        } else if (viewHolder.itemType == FIRST_HEADER) {
            // prepare canvas rect area for draw item (cell in table)
            canvas.clipRect(
                if (!isRTL) 0 else rowHeaderStartX,
                0,
                if (!isRTL) 0.coerceAtLeast(manager.headerRowWidth - headerFixedX)
                else 0.coerceAtLeast(rowHeaderStartX + manager.headerRowWidth),
                0.coerceAtLeast(manager.headerColumnHeight - headerFixedY)
            )
        }
        val result = super.drawChild(canvas, child, drawingTime)
        canvas.restore() // need to restore here.
        return result
    }

    override fun onDown(e: MotionEvent?): Boolean {
        startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL, ViewCompat.TYPE_TOUCH)
        // stop smooth scrolling
        if (!scrollerRunnable.isFinished) {
            scrollerRunnable.forceFinished()
        }
        return true
    }

    override fun onSingleTapUp(e: MotionEvent): Boolean {
        // simple click event
        val viewHolder = getViewHolderByPosition(e.x.toInt(), e.y.toInt())
        if (viewHolder != null) {
            adapter?.onItemClickListener?.run {
                when (viewHolder.itemType) {
                    ITEM -> onItemClick(viewHolder.rowIndex, getBindColumn(viewHolder.columnIndex))
                    ROW_HEADER -> onRowHeaderClick(viewHolder.rowIndex)
                    COLUMN_HEADER -> onColumnHeaderClick(viewHolder, getBindColumn(viewHolder.columnIndex))
                    else -> onLeftTopHeaderClick()
                }
            }
        }
        return true
    }

    override fun onLongPress(e: MotionEvent) {
        // prepare drag and drop
        // search view holder by x, y
        val viewHolder = getViewHolderByPosition(e.x.toInt(), e.y.toInt())
        if (viewHolder != null) {
            if (!settings.isDragAndDropEnabled) {
                checkLongPressForItemAndFirstHeader(viewHolder)
                return
            }
            // save start dragging touch position
            dragAndDropPoints.setStart(
                (state.scrollX + e.x).toInt(),
                (state.scrollY + e.y).toInt()
            )
            when (viewHolder.itemType) {
                COLUMN_HEADER -> {
                    // dragging column header
                    state.setRowDragging(false, viewHolder.rowIndex)
                    state.setColumnDragging(true, viewHolder.columnIndex)

                    // set dragging flags to column's view holder
                    setDraggingToColumn(viewHolder.columnIndex)
                    shadowHelper.removeColumnsHeadersShadow(this)
                    shadowHelper.addLeftShadow(this)
                    shadowHelper.addRightShadow(this)

                    // update view
                    refreshViewHolders()
                }
                ROW_HEADER -> {
                    // dragging column header
                    state.setRowDragging(true, viewHolder.rowIndex)
                    state.setColumnDragging(false, viewHolder.columnIndex)

                    // set dragging flags to row's view holder
                    setDraggingToRow(viewHolder.rowIndex)
                    shadowHelper.removeRowsHeadersShadow(this)
                    shadowHelper.addTopShadow(this)
                    shadowHelper.addBottomShadow(this)

                    // update view
                    refreshViewHolders()
                }
                else -> {
                    checkLongPressForItemAndFirstHeader(viewHolder)
                }
            }
        }
    }

    private fun checkLongPressForItemAndFirstHeader(viewHolder: ViewHolder) {
        val onItemClickListener = adapter?.onItemLongClickListener
        if (onItemClickListener != null) {
            if (viewHolder.itemType == ITEM) {
                onItemClickListener.onItemLongClick(viewHolder.rowIndex, viewHolder.columnIndex)
            } else if (viewHolder.itemType == FIRST_HEADER) {
                onItemClickListener.onLeftTopHeaderLongClick()
            }
        }
    }

    /**
     * Method set dragging flag to all view holders in the specific column
     *
     * @param column     specific column
     */
    private fun setDraggingToColumn(column: Int) {
        val holders = viewHolders.getColumnItems(column)
        for (holder: ViewHolder in holders) holder.isDragging = true
        headerColumnViewHolders.get(column)?.isDragging = true
    }

    /**
     * Method set dragging flag to all view holders in the specific row
     *
     * @param row        specific row
     */
    @Suppress("unused")
    private fun setDraggingToRow(row: Int) {
        val holders = viewHolders.getRowItems(row)
        for (holder: ViewHolder in holders) holder.isDragging = true
        headerRowViewHolders.get(row)?.isDragging = true
    }

    override fun onActionUp(e: MotionEvent?): Boolean {
        stopNestedScroll(ViewCompat.TYPE_TOUCH)
        if (state.isDragging) {
            // remove shadows from dragging views
            shadowHelper.removeAllDragAndDropShadows(this)

            // stop smooth scrolling
            if (!scrollerDragAndDropRunnable.isFinished) {
                scrollerDragAndDropRunnable.stop()
            }

            // remove dragging flag from all item view holders
            viewHolders.all.forEach { it.isDragging = false }

            // remove dragging flag from all column header view holders
            repeat(headerColumnViewHolders.size()) {
                val key = headerColumnViewHolders.keyAt(it)
                headerColumnViewHolders[key]?.isDragging = false
            }

            // remove dragging flag from all row header view holders
            repeat(headerRowViewHolders.size()) {
                val key: Int = headerRowViewHolders.keyAt(it)
                headerRowViewHolders[key]?.isDragging = false
            }

            // remove dragging flags from state
            state.setRowDragging(false, NO_DRAGGING_POSITION)
            state.setColumnDragging(false, NO_DRAGGING_POSITION)

            // clear dragging point positions
            dragAndDropPoints.setStart(0, 0)
            dragAndDropPoints.setOffset(0, 0)
            dragAndDropPoints.setEnd(0, 0)

            // update main layout
            refreshViewHolders()
        }
        return true
    }

    private fun getViewHolderByPosition(x: Int, y: Int): ViewHolder? {
        var tempX = x
        var tempY = y
        val viewHolder: ViewHolder?
        val absX = tempX + state.scrollX - emptySpace
        val absY = tempY + state.scrollY
        if (!settings.isHeaderFixed && isRTL && (emptySpace == 0)) {
            tempX = absX - state.scrollX
            tempY = absY - state.scrollY
        } else if (!settings.isHeaderFixed && !isRTL) {
            tempX = absX
            tempY = absY
        }
        if (((tempY < manager.headerColumnHeight) && (tempX < manager.headerRowWidth) && !isRTL
                    || (tempY < manager.headerColumnHeight) && (tempX > calculateRowHeadersLeft()) && isRTL)
        ) {
            // left top view was clicked
            viewHolder = leftTopViewHolder
        } else if (settings.isHeaderFixed) {
            if (tempY < manager.headerColumnHeight) {
                // coordinate x, y in the column header's area
                val column = manager.getColumnByXWithShift(absX, settings.cellMargin)
                viewHolder = headerColumnViewHolders.get(column)
            } else if ((tempX < manager.headerRowWidth && !isRTL
                        || tempX > calculateRowHeadersLeft() && isRTL)
            ) {
                // coordinate x, y in the row header's area
                val row = manager.getRowByYWithShift(absY, settings.cellMargin)
                viewHolder = headerRowViewHolders.get(row)
            } else {
                // coordinate x, y in the items area
                val column = manager.getColumnByXWithShift(absX, settings.cellMargin)
                val row = manager.getRowByYWithShift(absY, settings.cellMargin)
                viewHolder = viewHolders[row, column]
            }
        } else {
            if (absY < manager.headerColumnHeight) {
                // coordinate x, y in the column header's area
                val column = manager.getColumnByXWithShift(absX, settings.cellMargin)
                viewHolder = headerColumnViewHolders.get(column)
            } else if ((absX < manager.headerRowWidth && !isRTL
                        || absX - state.scrollX > calculateRowHeadersLeft() - emptySpace && isRTL)
            ) {
                // coordinate x, y in the row header's area
                val row = manager.getRowByYWithShift(absY, settings.cellMargin)
                viewHolder = headerRowViewHolders.get(row)
            } else {
                // coordinate x, y in the items area
                val column = manager.getColumnByXWithShift(absX, settings.cellMargin)
                val row = manager.getRowByYWithShift(absY, settings.cellMargin)
                viewHolder = viewHolders[row, column]
            }
        }
        return viewHolder
    }

    override fun onScroll(
        e1: MotionEvent?,
        e2: MotionEvent?,
        distanceX: Float,
        distanceY: Float
    ): Boolean {
        if (!state.isDragging) {
            val diff = ((e1?.y ?: 0f) - (e2?.y ?: 0f)).roundToInt()
            dispatchNestedPreScroll(0, diff, null, null, ViewCompat.TYPE_TOUCH)
            if (!scrollerRunnable.isFinished) {
                scrollerRunnable.forceFinished()
            }
            scrollBy(distanceX.toInt(), distanceY.toInt())
        }
        return true
    }

    override fun onFling(
        e1: MotionEvent?,
        e2: MotionEvent?,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        if (!state.isDragging) {
            // simple fling
            scrollerRunnable.start(
                state.scrollX,
                state.scrollY,
                velocityX.toInt() / 2,
                velocityY.toInt() / 2,
                (manager.fullWidth - settings.layoutWidth + manager.columnCount * settings.cellMargin).toInt(),
                (manager.fullHeight - settings.layoutHeight + manager.rowCount * settings.cellMargin).toInt()
            )
        }
        return true
    }

    override fun notifyDataSetChanged() {
        recycleViewHolders(true)
        visibleArea[state.scrollX, state.scrollY, state.scrollX + settings.layoutWidth] =
            state.scrollY + settings.layoutHeight
        addViewHolders(visibleArea)
    }

    override fun notifyLayoutChanged() {
        recycleViewHolders(true)
        invalidate()
        visibleArea[state.scrollX, state.scrollY, state.scrollX + settings.layoutWidth] =
            state.scrollY + settings.layoutHeight
        addViewHolders(visibleArea)
    }

    override fun notifyItemChanged(rowIndex: Int, columnIndex: Int) {
        val holder = if (rowIndex == 0 && columnIndex == 0) {
            leftTopViewHolder
        } else if (rowIndex == 0) {
            headerColumnViewHolders.get(columnIndex - 1)
        } else if (columnIndex == 0) {
            headerRowViewHolders.get(rowIndex - 1)
        } else {
            viewHolders[rowIndex - 1, columnIndex - 1]
        }
        holder?.run { viewHolderChanged(this) }
    }

    override fun notifyRowChanged(rowIndex: Int) {
        val rowHolders = viewHolders.getRowItems(rowIndex)
        for (holder: ViewHolder in rowHolders) {
            viewHolderChanged(holder)
        }
    }

    override fun notifyColumnChanged(columnIndex: Int) {
        val columnHolders = viewHolders.getColumnItems(columnIndex)
        for (holder: ViewHolder in columnHolders) {
            viewHolderChanged(holder)
        }
    }

    private fun viewHolderChanged(holder: ViewHolder) {
        when (holder.itemType) {
            FIRST_HEADER -> {
                leftTopViewHolder = holder
                adapter!!.onBindLeftTopHeaderViewHolder(leftTopViewHolder!!)
            }
            COLUMN_HEADER -> {
                headerColumnViewHolders.remove(holder.columnIndex)
                recycleViewHolder(holder)
                addViewHolder(holder.rowIndex, holder.columnIndex, holder.itemType)
            }
            ROW_HEADER -> {
                headerRowViewHolders.remove(holder.rowIndex)
                recycleViewHolder(holder)
                addViewHolder(holder.rowIndex, holder.columnIndex, holder.itemType)
            }
            else -> {
                viewHolders.remove(holder.rowIndex, holder.columnIndex)
                recycleViewHolder(holder)
                addViewHolder(holder.rowIndex, holder.columnIndex, holder.itemType)
            }
        }
    }

    var isHeaderFixed: Boolean
        get() = settings.isHeaderFixed
        set(headerFixed) {
            settings.setHeaderFixed(headerFixed)
        }

    var isSolidRowHeader: Boolean
        get() = settings.isSolidRowHeader
        set(solidRowHeader) {
            settings.setSolidRowHeader(solidRowHeader)
        }

    var isDragAndDropEnabled: Boolean
        get() = settings.isDragAndDropEnabled
        set(enabled) {
            settings.isDragAndDropEnabled = enabled
        }

    private class TableInstanceSaver : Parcelable {
        var mScrollX = 0
        var mScrollY = 0
        var mLayoutDirection = 0
        var mFixedHeaders = false

        constructor()
        constructor(`in`: Parcel) {
            mScrollX = `in`.readInt()
            mScrollY = `in`.readInt()
            mLayoutDirection = `in`.readInt()
            mFixedHeaders = `in`.readByte().toInt() != 0
        }

        override fun describeContents(): Int {
            return 0
        }

        override fun writeToParcel(dest: Parcel, flags: Int) {
            dest.writeInt(mScrollX)
            dest.writeInt(mScrollY)
            dest.writeInt(mLayoutDirection)
            dest.writeByte((if (mFixedHeaders) 1 else 0).toByte())
        }

        companion object {
            @JvmField
            val CREATOR = object : Parcelable.Creator<TableInstanceSaver?> {
                override fun createFromParcel(source: Parcel): TableInstanceSaver {
                    return TableInstanceSaver(source)
                }

                override fun newArray(size: Int): Array<TableInstanceSaver?> {
                    return arrayOfNulls(size)
                }
            }
        }
    }

    companion object {
        private const val EXTRA_STATE_SUPER = "EXTRA_STATE_SUPER"
        private const val EXTRA_STATE_VIEW_GROUP = "EXTRA_STATE_VIEW_GROUP"
        private const val SHADOW_THICK = 20
        private const val SHADOW_HEADERS_THICK = 10
    }

    private val childHelper = NestedScrollingChildHelper(this).apply {
        isNestedScrollingEnabled = true
    }

    // NestedScrollingChild2
    override fun startNestedScroll(axes: Int, type: Int): Boolean {
        return childHelper.startNestedScroll(axes, type)
    }

    override fun stopNestedScroll(type: Int) {
        childHelper.stopNestedScroll(type)
    }

    override fun hasNestedScrollingParent(type: Int): Boolean {
        return childHelper.hasNestedScrollingParent(type)
    }

    override fun dispatchNestedScroll(
        dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int,
        dyUnconsumed: Int, offsetInWindow: IntArray?, type: Int
    ): Boolean {
        return childHelper.dispatchNestedScroll(
            dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed,
            offsetInWindow, type
        )
    }

    override fun dispatchNestedPreScroll(
        dx: Int, dy: Int, consumed: IntArray?,
        offsetInWindow: IntArray?, type: Int
    ): Boolean {
        return childHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow, type)
    }
}
