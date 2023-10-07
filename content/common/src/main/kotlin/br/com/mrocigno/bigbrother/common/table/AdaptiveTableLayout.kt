package br.com.mrocigno.bigbrother.common.table

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
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
import androidx.core.view.ViewCompat
import br.com.mrocigno.bigbrother.common.R
import br.com.mrocigno.bigbrother.common.table.ViewHolderType.Companion.FIRST_HEADER

class AdaptiveTableLayout : ViewGroup, ScrollHelper.ScrollHelperListener, AdaptiveTableDataSetObserver {
    /**
     * Matrix with item view holders
     */
    private lateinit var mViewHolders: SparseMatrix<ViewHolder>

    /**
     * Map with column's headers view holders
     */
    private lateinit var mHeaderColumnViewHolders: SparseArrayCompat<ViewHolder>

    /**
     * Map with row's headers view holders
     */
    private lateinit var mHeaderRowViewHolders: SparseArrayCompat<ViewHolder>

    /**
     * Contained with drag and drop points
     */
    private lateinit var mDragAndDropPoints: DragAndDropPoints

    /**
     * Container with layout state
     */
    private lateinit var mState: AdaptiveTableState

    /**
     * Item's widths and heights manager.
     */
    private lateinit var mManager: AdaptiveTableManager

    /**
     * Need to fix columns bounce when dragging header.
     * Saved absolute point when header switched in drag and drop mode.
     */
    private lateinit var mLastSwitchHeaderPoint: Point

    /**
     * Contains visible area rect. Left top point and right bottom
     */
    private lateinit var mVisibleArea: Rect

    /**
     * View holder in the left top corner.
     */
    private var mLeftTopViewHolder: ViewHolder? = null

    /**
     * Table layout adapter
     */
    private var mAdapter: DataAdaptiveTableLayoutAdapter<ViewHolder>? = null

    /**
     * Recycle ViewHolders
     */
    private lateinit var mRecycler: Recycler

    /**
     * Keep layout settings
     */
    private lateinit var mSettings: AdaptiveTableLayoutSettings

    /**
     * Detect all gestures on layout.
     */
    private lateinit var mScrollHelper: ScrollHelper

    /**
     * Runnable helps with fling events
     */
    private lateinit var mScrollerRunnable: SmoothScrollRunnable

    /**
     * Runnable helps with scroll in drag and drop mode
     */
    private lateinit var mScrollerDragAndDropRunnable: DragAndDropScrollRunnable

    /**
     * Helps work with row' or column' shadows.
     */
    private lateinit var mShadowHelper: ShadowHelper
    private var mLayoutDirection: Int = ViewCompat.getLayoutDirection(this)
    private lateinit var mLayoutDirectionHelper: LayoutDirectionHelper

    /**
     * Instant state
     */
    private var mSaver: TableInstanceSaver? = null

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context)
        initAttrs(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context)
        initAttrs(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(
        context,
        attrs,
        defStyleAttr,
        defStyleRes
    ) {
        init(context)
        initAttrs(context, attrs)
    }

    val isRTL: Boolean
        /**
         * @return true if layout direction is RightToLeft
         */
        get() = mLayoutDirectionHelper.isRTL

    override fun setLayoutDirection(layoutDirection: Int) {
        super.setLayoutDirection(layoutDirection)
        mLayoutDirection = layoutDirection
        mLayoutDirectionHelper.layoutDirection = mLayoutDirection
        mShadowHelper.onLayoutDirectionChanged()
    }

    protected override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        if (changed) {
            // calculate layout width and height
            mSettings.setLayoutWidth(r - l)
            mSettings.setLayoutHeight(b - t)
            // init data
            initItems()
        }
    }

    private fun initAttrs(context: Context, attrs: AttributeSet) {
        val a: TypedArray = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.AdaptiveTableLayout,
            0, 0
        )
        try {
            mSettings.setHeaderFixed(
                a.getBoolean(
                    R.styleable.AdaptiveTableLayout_fixedHeaders,
                    true
                )
            )
            mSettings.setCellMargin(
                a.getDimensionPixelSize(
                    R.styleable.AdaptiveTableLayout_cellMargin,
                    0
                )
            )
            mSettings.setSolidRowHeader(
                a.getBoolean(
                    R.styleable.AdaptiveTableLayout_solidRowHeaders,
                    true
                )
            )
            mSettings.isDragAndDropEnabled = (
                a.getBoolean(
                    R.styleable.AdaptiveTableLayout_dragAndDropEnabled,
                    true
                )
            )
        } finally {
            a.recycle()
        }
    }

    private fun init(context: Context) {
        mViewHolders = SparseMatrix()
        mLayoutDirectionHelper = LayoutDirectionHelper(mLayoutDirection)
        mHeaderColumnViewHolders = SparseArrayCompat()
        mHeaderRowViewHolders = SparseArrayCompat()
        mDragAndDropPoints = DragAndDropPoints()
        mState = AdaptiveTableState()
        mManager = AdaptiveTableManagerRTL(mLayoutDirectionHelper)
        mLastSwitchHeaderPoint = Point()
        mVisibleArea = Rect()
        // init scroll and fling helpers
        mScrollerRunnable = SmoothScrollRunnable(this)
        mScrollerDragAndDropRunnable = DragAndDropScrollRunnable(this)
        mRecycler = Recycler()
        mSettings = AdaptiveTableLayoutSettings()
        mScrollHelper = ScrollHelper(context)
        mScrollHelper.setListener(this)
        mShadowHelper = ShadowHelper(mLayoutDirectionHelper)
    }

    protected override fun onSaveInstanceState(): Parcelable {
        val bundle = Bundle()
        bundle.putParcelable(EXTRA_STATE_SUPER, super.onSaveInstanceState())
        mSaver = TableInstanceSaver()
        mSaver!!.mScrollX = mState.scrollX
        mSaver!!.mScrollY = mState.scrollY
        mSaver!!.mLayoutDirection = mLayoutDirection
        mSaver!!.mFixedHeaders = mSettings.isHeaderFixed
        if (mAdapter != null) {
            mAdapter!!.onSaveInstanceState(bundle)
        }
        bundle.putParcelable(EXTRA_STATE_VIEW_GROUP, mSaver)
        return bundle
    }

    protected override fun onRestoreInstanceState(state: Parcelable) {
        var result: Parcelable = state
        if (state is Bundle) {
            state.getParcelable<Parcelable>(EXTRA_STATE_VIEW_GROUP)?.let { parcel ->
                if (parcel is TableInstanceSaver) {
                    mSaver = parcel
                    mLayoutDirection = parcel.mLayoutDirection
                    layoutDirection = parcel.mLayoutDirection
                    mSettings.setHeaderFixed(parcel.mFixedHeaders)
                }
            }
            if (mAdapter != null) {
                mAdapter!!.onRestoreInstanceState(state)
            }
            result = state.getParcelable(EXTRA_STATE_SUPER)!!
        }
        super.onRestoreInstanceState(result)
    }

    private fun initItems() {
        val adapter = mAdapter ?: run {
            // clear
            mManager.clear()
            recycleViewHolders(true)
            return
        }


        // init manager. Not include headers
        mManager.init(adapter.rowCount - 1, adapter.columnCount - 1)

        // calculate widths
        run {
            val count: Int = mManager.columnCount
            var i: Int = 0
            while (i < count) {
                val item: Int = adapter!!.getColumnWidth(i)
                mManager.putColumnWidth(i, item)
                i++
            }
        }

        // calculate heights
        val count = mManager.rowCount
        var i = 0
        while (i < count) {
            val item = adapter!!.getRowHeight(i)
            mManager.putRowHeight(i, item)
            i++
        }

        // set header's width and height. Set 0 in case < 0
        mManager.headerColumnHeight = Math.max(0, adapter.headerColumnHeight)
        mManager.headerRowWidth = Math.max(0, adapter.headerRowWidth)

        // start calculating full width and full height
        mManager.invalidate()

        // show items in this area
        mVisibleArea[mState.scrollX, mState.scrollY, mState.scrollX + mSettings.layoutWidth] =
            mState.scrollY + mSettings.layoutHeight
        addViewHolders(mVisibleArea)
        if (mSaver != null) {
            scrollTo(mSaver!!.mScrollX, mSaver!!.mScrollY)
            mSaver = null
        } else if (isRTL) {
            scrollTo(mSettings.layoutWidth, 0)
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
        if (mAdapter != null) {
            // remove observers from old adapter
            mAdapter!!.unregisterDataSetObserver(this)
        }
        if (adapter != null) {
            // wrap adapter
            mAdapter = LinkedAdaptiveTableAdapterImpl(adapter, mSettings.isSolidRowHeader).apply {
                // register notify callbacks
                registerDataSetObserver(this@AdaptiveTableLayout)
                adapter.registerDataSetObserver(DataSetObserverProxy(this@apply))
            }
        } else {
            // remove adapter
            mAdapter = null
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
        if (mAdapter != null) {
            mAdapter!!.unregisterDataSetObserver(this)
        }
        mAdapter = adapter
        if (mAdapter != null) {
            mAdapter!!.registerDataSetObserver(this)
        }
        if (mSettings.layoutHeight != 0 && mSettings.layoutWidth != 0) {
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
        get() = if (mAdapter is LinkedAdaptiveTableAdapterImpl<*>) (mAdapter as LinkedAdaptiveTableAdapterImpl<*>).rowsModifications else emptyMap()

    val linkedAdapterColumnsModifications: Map<Int, Int>?
        /**
         * When used adapter with IMMUTABLE data, returns columns position modifications
         * (old position -> new position)
         *
         * @return row position modification map. Includes only modified column numbers
         */
        get() = if (mAdapter is LinkedAdaptiveTableAdapterImpl<*>) (mAdapter as LinkedAdaptiveTableAdapterImpl<*>).columnsModifications else emptyMap()

    override fun scrollTo(x: Int, y: Int) {
        scrollBy(x, y)
    }

    override fun scrollBy(x: Int, y: Int) {
        // block scroll one axle
        val tempX = if (mState.isRowDragging) 0 else x
        val tempY = if (mState.isColumnDragging) 0 else y
        var diffX = tempX
        var diffY = tempY
        val shadowShiftX = mManager.columnCount * mSettings.cellMargin
        val shadowShiftY = mManager.rowCount * mSettings.cellMargin
        val maxX = mManager.fullWidth + shadowShiftX
        val maxY = mManager.fullHeight + shadowShiftY
        if (mState.scrollX + tempX <= 0) {
            // scroll over view to the left
            diffX = mState.scrollX
            mState.scrollX = 0
        } else if (mSettings.layoutWidth > maxX) {
            // few items and we have free space.
            diffX = 0
            mState.scrollX = 0
        } else if (mState.scrollX + mSettings.layoutWidth + tempX > maxX) {
            // scroll over view to the right
            diffX = (maxX - mState.scrollX - mSettings.layoutWidth).toInt()
            mState.scrollX = mState.scrollX + diffX
        } else {
            mState.scrollX = mState.scrollX + tempX
        }
        if (mState.scrollY + tempY <= 0) {
            // scroll over view to the top
            diffY = mState.scrollY
            mState.scrollY = 0
        } else if (mSettings.layoutHeight > maxY) {
            // few items and we have free space.
            diffY = 0
            mState.scrollY = 0
        } else if (mState.scrollY + mSettings.layoutHeight + tempY > maxY) {
            // scroll over view to the bottom
            diffY = (maxY - mState.scrollY - mSettings.layoutHeight).toInt()
            mState.scrollY = mState.scrollY + diffY
        } else {
            mState.scrollY = mState.scrollY + tempY
        }
        if (diffX == 0 && diffY == 0) {
            return
        }
        if (mAdapter != null) {
            // refresh views
            recycleViewHolders()
            mVisibleArea!![mState.scrollX, mState.scrollY, mState.scrollX + mSettings.layoutWidth] =
                mState.scrollY + mSettings.layoutHeight
            addViewHolders(mVisibleArea)
            refreshViewHolders()
        }
    }

    /**
     * Refresh all view holders
     */
    private fun refreshViewHolders() {
        if (mAdapter != null) {
            for (holder: ViewHolder? in mViewHolders.all) {
                if (holder != null) {
                    // cell item
                    refreshItemViewHolder(holder, mState.isRowDragging, mState.isColumnDragging)
                }
            }
            if (mState.isColumnDragging) {
                refreshAllColumnHeadersHolders()
                refreshAllRowHeadersHolders()
            } else {
                refreshAllRowHeadersHolders()
                refreshAllColumnHeadersHolders()
            }
            mLeftTopViewHolder?.run {
                refreshLeftTopHeaderViewHolder(this)
                itemView.bringToFront()
            }
        }
    }

    private fun refreshAllColumnHeadersHolders() {
        val count: Int = mHeaderColumnViewHolders.size()
        var i = 0
        while (i < count) {
            val key: Int = mHeaderColumnViewHolders.keyAt(i)
            // get the object by the key.
            val holder = mHeaderColumnViewHolders.get(key)
            if (holder != null) {
                // column header
                refreshHeaderColumnViewHolder(holder)
            }
            i++
        }
    }

    private fun refreshAllRowHeadersHolders() {
        val count: Int = mHeaderRowViewHolders.size()
        var i = 0
        while (i < count) {
            val key: Int = mHeaderRowViewHolders.keyAt(i)
            // get the object by the key.
            val holder = mHeaderRowViewHolders.get(key)
            if (holder != null) {
                // column header
                refreshHeaderRowViewHolder(holder)
            }
            i++
        }
    }
    /**
     * Refresh current item view holder.
     *
     * @param holder           current view holder
     * @param isRowDragging    row dragging state
     * @param isColumnDragging column dragging state
     */
    /**
     * Refresh current item view holder with default parameters.
     *
     * @param holder current view holder
     */
    private fun refreshItemViewHolder(
        holder: ViewHolder,
        isRowDragging: Boolean = false, isColumnDragging: Boolean = false
    ) {
        var left = emptySpace + mManager.getColumnsWidth(0, Math.max(0, holder.columnIndex))
        var top = mManager.getRowsHeight(0, Math.max(0, holder.rowIndex))
        val view = holder.itemView
        if (isColumnDragging && holder.isDragging && mDragAndDropPoints.offset.x > 0) {
            // visible dragging column. Calculate left offset using drag and drop points.
            left = mState.scrollX + mDragAndDropPoints.offset.x - view!!.width / 2
            if (!isRTL) {
                left -= mManager.headerRowWidth
            }
            view.bringToFront()
        } else if (isRowDragging && holder.isDragging && mDragAndDropPoints.offset.y > 0) {
            // visible dragging row. Calculate top offset using drag and drop points.
            top =
                mState.scrollY + mDragAndDropPoints.offset.y - view!!.height / 2 - mManager.headerColumnHeight
            view.bringToFront()
        }
        val leftMargin = holder.columnIndex * mSettings.cellMargin + mSettings.cellMargin
        val topMargin = holder.rowIndex * mSettings.cellMargin + mSettings.cellMargin
        if (!isRTL) {
            left += mManager.headerRowWidth
        }

        // calculate view layout positions
        val viewPosLeft = left - mState.scrollX + leftMargin
        val viewPosRight = viewPosLeft + mManager.getColumnWidth(holder.columnIndex)
        val viewPosTop = top - mState.scrollY + mManager.headerColumnHeight + topMargin
        val viewPosBottom = viewPosTop + mManager.getRowHeight(holder.rowIndex)

        // update layout position
        view!!.layout(viewPosLeft, viewPosTop, viewPosRight, viewPosBottom)
    }

    /**
     * Refresh current column header view holder.
     *
     * @param holder current view holder
     */
    private fun refreshHeaderColumnViewHolder(holder: ViewHolder) {
        var left = emptySpace + mManager.getColumnsWidth(0, Math.max(0, holder.columnIndex))
        if (!isRTL) {
            left += mManager.headerRowWidth
        }
        val top = if (mSettings.isHeaderFixed) 0 else -mState.scrollY
        val view = holder.itemView
        val leftMargin = holder.columnIndex * mSettings.cellMargin + mSettings.cellMargin
        val topMargin = holder.rowIndex * mSettings.cellMargin + mSettings.cellMargin
        if (holder.isDragging && mDragAndDropPoints.offset.x > 0) {
            left = mState.scrollX + mDragAndDropPoints.offset.x - view!!.width / 2
            view.bringToFront()
        }
        if (holder.isDragging) {
            val leftShadow = mShadowHelper.leftShadow
            val rightShadow = mShadowHelper.rightShadow
            if (leftShadow != null) {
                val shadowLeft = left - mState.scrollX
                leftShadow.layout(
                    Math.max(
                        mManager.headerRowWidth - mState.scrollX,
                        shadowLeft - SHADOW_THICK
                    ) + leftMargin,
                    0,
                    shadowLeft + leftMargin,
                    mSettings.layoutHeight
                )
                leftShadow.bringToFront()
            }
            if (rightShadow != null) {
                val shadowLeft =
                    left + mManager.getColumnWidth(holder.columnIndex) - mState.scrollX
                rightShadow.layout(
                    Math.max(
                        mManager.headerRowWidth - mState.scrollX,
                        shadowLeft
                    ) + leftMargin,
                    0,
                    shadowLeft + SHADOW_THICK + leftMargin,
                    mSettings.layoutHeight
                )
                rightShadow.bringToFront()
            }
        }
        val viewPosLeft = left - mState.scrollX + leftMargin
        val viewPosRight = viewPosLeft + mManager.getColumnWidth(holder.columnIndex)
        val viewPosTop = top + topMargin
        val viewPosBottom = viewPosTop + mManager.headerColumnHeight
        view!!.layout(
            viewPosLeft,
            viewPosTop,
            viewPosRight,
            viewPosBottom
        )
        if (mState.isRowDragging) {
            view.bringToFront()
        }
        if (!mState.isColumnDragging) {
            var shadow = mShadowHelper.columnsHeadersShadow
            if (shadow == null) {
                shadow = mShadowHelper.addColumnsHeadersShadow(this)
            }
            shadow.layout(
                if (mState.isRowDragging) 0 else if (mSettings.isHeaderFixed) 0 else -mState.scrollX,
                top + mManager.headerColumnHeight,
                mSettings.layoutWidth,
                top + mManager.headerColumnHeight + SHADOW_HEADERS_THICK
            )
            shadow.bringToFront()
        }
    }

    /**
     * Refresh current row header view holder.
     *
     * @param holder current view holder
     */
    private fun refreshHeaderRowViewHolder(holder: ViewHolder) {
        var top = mManager.getRowsHeight(
            0,
            Math.max(0, holder.rowIndex)
        ) + mManager.headerColumnHeight
        var left = calculateRowHeadersLeft()
        if (isRTL) {
            left += mSettings.cellMargin
        }
        val view = holder.itemView
        val leftMargin = holder.columnIndex * mSettings.cellMargin + mSettings.cellMargin
        val topMargin = holder.rowIndex * mSettings.cellMargin + mSettings.cellMargin
        if (holder.isDragging && mDragAndDropPoints.offset.y > 0) {
            top = mState.scrollY + mDragAndDropPoints.offset.y - view.height / 2
            view.bringToFront()
        }
        if (holder.isDragging) {
            val topShadow = mShadowHelper.topShadow
            val bottomShadow = mShadowHelper.bottomShadow
            if (topShadow != null) {
                val shadowTop = top - mState.scrollY
                topShadow.layout(
                    0,
                    Math.max(
                        mManager.headerColumnHeight - mState.scrollY,
                        shadowTop - SHADOW_THICK
                    ) + topMargin,
                    mSettings.layoutWidth,
                    shadowTop + topMargin
                )
                topShadow.bringToFront()
            }
            if (bottomShadow != null) {
                val shadowBottom =
                    top - mState.scrollY + mManager.getRowHeight(holder.rowIndex)
                bottomShadow.layout(
                    0,
                    Math.max(
                        mManager.headerColumnHeight - mState.scrollY,
                        shadowBottom
                    ) + topMargin,
                    mSettings.layoutWidth,
                    shadowBottom + SHADOW_THICK + topMargin
                )
                bottomShadow.bringToFront()
            }
        }
        view!!.layout(
            left + leftMargin * if (isRTL) 0 else 1,
            top - mState.scrollY + topMargin,
            left + mManager.headerRowWidth + leftMargin * if (isRTL) 1 else 0,
            top + mManager.getRowHeight(holder.rowIndex) - mState.scrollY + topMargin
        )
        if (mState.isColumnDragging) {
            view.bringToFront()
        }
        if (!mState.isRowDragging) {
            var shadow = mShadowHelper.rowsHeadersShadow
            if (shadow == null) {
                shadow = mShadowHelper.addRowsHeadersShadow(this)
            }
            val shadowStart: Int
            val shadowEnd: Int
            shadowStart = if (!isRTL) view.right else view.left - SHADOW_HEADERS_THICK
            shadowEnd = shadowStart + SHADOW_HEADERS_THICK
            shadow.layout(
                shadowStart,
                if (mState.isColumnDragging) 0 else if (mSettings.isHeaderFixed) 0 else -mState.scrollY,
                shadowEnd,
                mSettings.layoutHeight
            )
            shadow.bringToFront()
        }
    }

    /**
     * Refresh current row header view holder.
     *
     * @param holder current view holder
     */
    private fun refreshLeftTopHeaderViewHolder(holder: ViewHolder) {
        var left = calculateRowHeadersLeft()
        if (isRTL) left += mSettings.cellMargin
        val top = if (mSettings.isHeaderFixed) 0 else -mState.scrollY
        val view = holder.itemView
        val leftMargin = if (isRTL) 0 else mSettings.cellMargin
        val topMargin = mSettings.cellMargin
        view!!.layout(
            left + leftMargin,
            top + topMargin,
            left + mManager.headerRowWidth + leftMargin,
            top + mManager.headerColumnHeight + topMargin
        )
    }

    private fun calculateRowHeadersLeft(): Int {
        val left: Int
        if (isHeaderFixed) {
            if (!isRTL) {
                left = 0
            } else {
                left = rowHeaderStartX
            }
        } else {
            if (!isRTL) {
                left = -mState.scrollX
            } else {
                if (mManager.fullWidth <= mSettings.layoutWidth) {
                    left = -mState.scrollX + rowHeaderStartX
                } else {
                    left = (-mState.scrollX
                            + (mManager.fullWidth - mManager.headerRowWidth).toInt() + (mManager.columnCount * mSettings.cellMargin))
                }
            }
        }
        return left
    }
    /**
     * Recycle view holders outside screen
     *
     * @param isRecycleAll recycle all view holders if true
     */
    /**
     * Recycle all views
     */
    private fun recycleViewHolders(isRecycleAll: Boolean = false) {
        if (mAdapter == null) {
            return
        }
        val headerKeysToRemove: MutableList<Int> = ArrayList()

        // item view holders
        for (holder: ViewHolder? in mViewHolders.all) {
            if (holder != null && !holder.isDragging) {
                val view = holder.itemView
                // recycle view holder
                if ((isRecycleAll
                            || ((view!!.right < 0
                            ) || (view.left > mSettings.layoutWidth
                            ) || (view.bottom < 0
                            ) || (view.top > mSettings.layoutHeight)))
                ) {
                    // recycle view holder
                    mViewHolders!!.remove(holder.rowIndex, holder.columnIndex)
                    recycleViewHolder(holder)
                }
            }
        }
        if (!headerKeysToRemove.isEmpty()) {
            headerKeysToRemove.clear()
        }
        // column header view holders
        run {
            val count: Int = mHeaderColumnViewHolders.size()
            var i: Int = 0
            while (i < count) {
                val key: Int = mHeaderColumnViewHolders.keyAt(i)
                // get the object by the key.
                val holder: ViewHolder? = mHeaderColumnViewHolders.get(key)
                if (holder != null) {
                    val view: View? = holder.itemView
                    // recycle view holder
                    if ((isRecycleAll
                                || (view!!.getRight() < 0
                                ) || (view.getLeft() > mSettings.layoutWidth))
                    ) {
                        headerKeysToRemove.add(key)
                        recycleViewHolder(holder)
                    }
                }
                i++
            }
        }
        removeKeys(headerKeysToRemove, mHeaderColumnViewHolders)
        if (!headerKeysToRemove.isEmpty()) {
            headerKeysToRemove.clear()
        }
        // row header view holders
        val count: Int = mHeaderRowViewHolders.size()
        var i = 0
        while (i < count) {
            val key: Int = mHeaderRowViewHolders.keyAt(i)
            // get the object by the key.
            val holder = mHeaderRowViewHolders.get(key)
            if (holder != null && !holder.isDragging) {
                val view = holder.itemView
                // recycle view holder
                if ((isRecycleAll
                            || (view!!.bottom < 0
                            ) || (view.top > mSettings.layoutHeight))
                ) {
                    headerKeysToRemove.add(key)
                    recycleViewHolder(holder)
                }
            }
            i++
        }
        removeKeys(headerKeysToRemove, mHeaderRowViewHolders)
    }

    /**
     * Remove recycled viewholders from sparseArray
     *
     * @param keysToRemove List of ViewHolders keys that we need to remove
     * @param headers      SparseArray of viewHolders from where we need to remove
     */
    private fun removeKeys(keysToRemove: List<Int>, headers: SparseArrayCompat<ViewHolder>?) {
        for (key: Int in keysToRemove) {
            headers?.remove(key)
        }
    }

    /**
     * Recycle view holder and remove view from layout.
     *
     * @param holder view holder to recycle
     */
    private fun recycleViewHolder(holder: ViewHolder) {
        mRecycler.pushRecycledView(holder)
        removeView(holder.itemView)
        mAdapter!!.onViewHolderRecycled(holder)
    }

    private val emptySpace: Int
        private get() {
            return if (isRTL && mSettings.layoutWidth > mManager.fullWidth) {
                mSettings.layoutWidth - (mManager.fullWidth
                    .toInt()) - (mManager.columnCount * mSettings.cellMargin)
            } else {
                0
            }
        }

    /**
     * Create and add view holders with views to the layout.
     *
     * @param filledArea visible rect
     */
    private fun addViewHolders(filledArea: Rect?) {
        //search indexes for columns and rows which NEED TO BE showed in this area
        val leftColumn =
            mManager.getColumnByXWithShift(filledArea!!.left, mSettings.cellMargin)
        val rightColumn =
            mManager.getColumnByXWithShift(filledArea.right, mSettings.cellMargin)
        val topRow = mManager.getRowByYWithShift(filledArea.top, mSettings.cellMargin)
        val bottomRow = mManager.getRowByYWithShift(filledArea.bottom, mSettings.cellMargin)
        for (i in topRow..bottomRow) {
            for (j in leftColumn..rightColumn) {
                // item view holders
                val viewHolder = mViewHolders!![i, j]
                if (viewHolder == null && mAdapter != null) {
                    addViewHolder(i, j, ViewHolderType.Companion.ITEM)
                }
            }

            // row view headers holders
            val viewHolder = mHeaderRowViewHolders.get(i)
            if (viewHolder == null && mAdapter != null) {
                addViewHolder(
                    i,
                    if (isRTL) mManager.columnCount else 0,
                    ViewHolderType.Companion.ROW_HEADER
                )
            } else if (viewHolder != null && mAdapter != null) {
                refreshHeaderRowViewHolder(viewHolder)
            }
        }
        for (i in leftColumn..rightColumn) {
            // column view header holders
            val viewHolder = mHeaderColumnViewHolders.get(i)
            if (viewHolder == null && mAdapter != null) {
                addViewHolder(0, i, ViewHolderType.Companion.COLUMN_HEADER)
            } else if (viewHolder != null && mAdapter != null) {
                refreshHeaderColumnViewHolder(viewHolder)
            }
        }

        // add view left top view.
        if (mLeftTopViewHolder == null && mAdapter != null) {
            mLeftTopViewHolder =
                mAdapter!!.onCreateLeftTopHeaderViewHolder(this@AdaptiveTableLayout)
            mLeftTopViewHolder?.itemType = FIRST_HEADER
            val view = mLeftTopViewHolder?.itemView
            view?.setTag(R.id.tag_view_holder, mLeftTopViewHolder)
            addView(view, 0)
            mAdapter!!.onBindLeftTopHeaderViewHolder(mLeftTopViewHolder!!)
            view?.measure(
                MeasureSpec.makeMeasureSpec(mManager.headerRowWidth, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(mManager.headerColumnHeight, MeasureSpec.EXACTLY)
            )
            var viewPosLeft = mSettings.cellMargin
            if (isRTL) {
                viewPosLeft += rowHeaderStartX
            }
            val viewPosRight = viewPosLeft + mManager.headerRowWidth
            val viewPosTop = mSettings.cellMargin
            val viewPosBottom = viewPosTop + mManager.headerColumnHeight
            view?.layout(
                viewPosLeft,
                viewPosTop,
                viewPosRight,
                viewPosBottom
            )
        } else if (mLeftTopViewHolder != null && mAdapter != null) {
            refreshLeftTopHeaderViewHolder(mLeftTopViewHolder!!)
        }
    }

    private fun getBindColumn(column: Int): Int {
        return if (!isRTL) column else mManager.columnCount - 1 - column
    }

    @Suppress("unused")
    private fun addViewHolder(row: Int, column: Int, itemType: Int) {
        val createdNewView: Boolean
        // need to add new one
        var viewHolder = mRecycler.popRecycledViewHolder(itemType)
        createdNewView = viewHolder == null
        if (createdNewView) {
            viewHolder = createViewHolder(itemType)
        }
        if (viewHolder == null) {
            return
        }

        // prepare view holder
        viewHolder.rowIndex = row
        viewHolder.columnIndex = column
        viewHolder.itemType = itemType
        val view = viewHolder.itemView
        view!!.setTag(R.id.tag_view_holder, viewHolder)
        // add view to the layout
        addView(view, 0)

        // save and measure view holder
        if (itemType == ViewHolderType.Companion.ITEM) {
            mViewHolders!!.put(row, column, viewHolder)
            if (createdNewView) {
                // DO NOT REMOVE THIS!! Fix bug with request layout "requestLayout() improperly called"
                mAdapter!!.onBindViewHolder(viewHolder, row, getBindColumn(column))
            }
            view.measure(
                MeasureSpec.makeMeasureSpec(mManager.getColumnWidth(column), MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(mManager.getRowHeight(row), MeasureSpec.EXACTLY)
            )
            refreshItemViewHolder(viewHolder)
            if (!createdNewView) {
                // DO NOT REMOVE THIS!! Fix bug with request layout "requestLayout() improperly called"
                mAdapter!!.onBindViewHolder(viewHolder, row, getBindColumn(column))
            }
        } else if (itemType == ViewHolderType.Companion.ROW_HEADER) {
            mHeaderRowViewHolders.put(row, viewHolder)
            if (createdNewView) {
                // DO NOT REMOVE THIS!! Fix bug with request layout "requestLayout() improperly called"
                mAdapter!!.onBindHeaderRowViewHolder(viewHolder, row)
            }
            view.measure(
                MeasureSpec.makeMeasureSpec(mManager.headerRowWidth, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(mManager.getRowHeight(row), MeasureSpec.EXACTLY)
            )
            refreshHeaderRowViewHolder(viewHolder)
            if (!createdNewView) {
                // DO NOT REMOVE THIS!! Fix bug with request layout "requestLayout() improperly called"
                mAdapter!!.onBindHeaderRowViewHolder(viewHolder, row)
            }
        } else if (itemType == ViewHolderType.Companion.COLUMN_HEADER) {
            mHeaderColumnViewHolders.put(column, viewHolder)
            if (createdNewView) {
                // DO NOT REMOVE THIS!! Fix bug with request layout "requestLayout() improperly called"
                mAdapter!!.onBindHeaderColumnViewHolder(viewHolder, getBindColumn(column))
            }
            view.measure(
                MeasureSpec.makeMeasureSpec(mManager.getColumnWidth(column), MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(mManager.headerColumnHeight, MeasureSpec.EXACTLY)
            )
            refreshHeaderColumnViewHolder(viewHolder)
            if (!createdNewView) {
                // DO NOT REMOVE THIS!! Fix bug with request layout "requestLayout() improperly called"
                mAdapter!!.onBindHeaderColumnViewHolder(viewHolder, getBindColumn(column))
            }
        }
    }

    /**
     * Create view holder by type
     *
     * @param itemType view holder type
     * @return Created view holder
     */
    private fun createViewHolder(itemType: Int): ViewHolder? {
        if (itemType == ViewHolderType.Companion.ITEM) {
            return mAdapter!!.onCreateItemViewHolder(this@AdaptiveTableLayout)
        } else if (itemType == ViewHolderType.Companion.ROW_HEADER) {
            return mAdapter!!.onCreateRowHeaderViewHolder(this@AdaptiveTableLayout)
        } else if (itemType == ViewHolderType.Companion.COLUMN_HEADER) {
            return mAdapter!!.onCreateColumnHeaderViewHolder(this@AdaptiveTableLayout)
        }
        return null
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        // intercept event before OnClickListener on item view.
        mScrollHelper.onTouch(ev)
        return true
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (mState.isDragging) {
            // Drag and drop logic
            if (event.getAction() == MotionEvent.ACTION_UP) {
                // end drag and drop event
                mDragAndDropPoints.setEnd(
                    (mState.scrollX + event.getX()).toInt(),
                    (mState.scrollY + event.getY()).toInt()
                )
                mLastSwitchHeaderPoint[0] = 0
                return mScrollHelper.onTouch(event)
            }
            // calculate absolute x, y
            val absoluteX: Int = (mState.scrollX + event.getX()).toInt() - emptySpace
            val absoluteY: Int = (mState.scrollY + event.getY()).toInt()

            // if column drag and drop mode and column offset > SHIFT_VIEWS_THRESHOLD
            if (mState.isColumnDragging) {
                val dragAndDropHolder = mHeaderColumnViewHolders[mState.columnDraggingIndex]
                if (dragAndDropHolder != null) {
                    val fromColumn = dragAndDropHolder.columnIndex
                    val toColumn =
                        mManager.getColumnByXWithShift(absoluteX, mSettings.cellMargin)
                    if (fromColumn != toColumn) {
                        val columnWidth = mManager.getColumnWidth(toColumn)
                        var absoluteColumnX = mManager.getColumnsWidth(0, toColumn)
                        if (!isRTL) {
                            absoluteColumnX += mManager.headerRowWidth
                        }
                        if (fromColumn < toColumn) {
                            // left column is dragging one
                            val deltaX = (absoluteColumnX + columnWidth * 0.6f).toInt()
                            if (absoluteX > deltaX) {
                                // move column from left to right
                                for (i in fromColumn until toColumn) {
                                    shiftColumnsViews(i, i + 1)
                                }
                                mState.setColumnDragging(true, toColumn)
                            }
                        } else {
                            // right column is dragging one
                            val deltaX = (absoluteColumnX + columnWidth * 0.4f).toInt()
                            if (absoluteX < deltaX) {
                                // move column from right to left
                                for (i in fromColumn downTo toColumn + 1) {
                                    shiftColumnsViews(i - 1, i)
                                }
                                mState.setColumnDragging(true, toColumn)
                            }
                        }
                    }
                }
            } else if (mState.isRowDragging) {
                val dragAndDropHolder = mHeaderRowViewHolders[mState.rowDraggingIndex]
                if (dragAndDropHolder != null) {
                    val fromRow = dragAndDropHolder.rowIndex
                    val toRow = mManager.getRowByYWithShift(absoluteY, mSettings.cellMargin)
                    if (fromRow != toRow) {
                        val rowHeight = mManager.getRowHeight(toRow)
                        val absoluteColumnY =
                            mManager.getRowsHeight(0, toRow) + mManager.headerColumnHeight
                        if (fromRow < toRow) {
                            // left column is dragging one
                            val deltaY = (absoluteColumnY + rowHeight * 0.6f).toInt()
                            if (absoluteY > deltaY) {
                                // move column from left to right
                                for (i in fromRow until toRow) {
                                    shiftRowsViews(i, i + 1)
                                }
                                mState.setRowDragging(true, toRow)
                            }
                        } else {
                            // right column is dragging one
                            val deltaY = (absoluteColumnY + rowHeight * 0.4f).toInt()
                            if (absoluteY < deltaY) {
                                // move column from right to left
                                for (i in fromRow downTo toRow + 1) {
                                    shiftRowsViews(i - 1, i)
                                }
                                mState.setRowDragging(true, toRow)
                            }
                        }
                    }
                }
            }

            // set drag and drop offset
            mDragAndDropPoints.setOffset((event.getX()).toInt(), (event.getY()).toInt())

            // intercept touch for scroll in drag and drop mode
            mScrollerDragAndDropRunnable.touch(
                event.getX().toInt(), event.getY().toInt(),
                if (mState.isColumnDragging) ScrollType.Companion.SCROLL_HORIZONTAL else ScrollType.Companion.SCROLL_VERTICAL
            )

            // update positions
            refreshViewHolders()
            return true
        }
        return mScrollHelper.onTouch(event)
    }

    /**
     * Method change columns. Change view holders indexes, kay in map, init changing items in adapter.
     *
     * @param fromColumn from column index which need to shift
     * @param toColumn   to column index which need to shift
     */
    private fun shiftColumnsViews(fromColumn: Int, toColumn: Int) {
        if (mAdapter != null) {

            // change data
            mAdapter!!.changeColumns(getBindColumn(fromColumn), getBindColumn(toColumn))

            // change view holders
            switchHeaders(
                mHeaderColumnViewHolders,
                fromColumn,
                toColumn,
                ViewHolderType.Companion.COLUMN_HEADER
            )

            // change indexes in array with widths
            mManager.switchTwoColumns(fromColumn, toColumn)
            val fromHolders = mViewHolders.getColumnItems(fromColumn)
            val toHolders = mViewHolders.getColumnItems(toColumn)
            removeViewHolders(fromHolders)
            removeViewHolders(toHolders)
            for (holder: ViewHolder in fromHolders) {
                holder.columnIndex = toColumn
                mViewHolders.put(holder.rowIndex, holder.columnIndex, holder)
            }
            for (holder: ViewHolder in toHolders) {
                holder.columnIndex = fromColumn
                mViewHolders.put(holder.rowIndex, holder.columnIndex, holder)
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
        if (mAdapter != null) {
            // change data
            mAdapter!!.changeRows(fromRow, toRow, mSettings.isSolidRowHeader)

            // change view holders
            switchHeaders(
                mHeaderRowViewHolders,
                fromRow,
                toRow,
                ViewHolderType.Companion.ROW_HEADER
            )

            // change indexes in array with heights
            mManager.switchTwoRows(fromRow, toRow)
            val fromHolders = mViewHolders!!.getRowItems(fromRow)
            val toHolders = mViewHolders!!.getRowItems(toRow)
            removeViewHolders(fromHolders)
            removeViewHolders(toHolders)
            for (holder: ViewHolder in fromHolders) {
                holder.rowIndex = toRow
                mViewHolders.put(holder.rowIndex, holder.columnIndex, (holder))
            }
            for (holder: ViewHolder in toHolders) {
                holder.rowIndex = fromRow
                mViewHolders.put(holder.rowIndex, holder.columnIndex, (holder))
            }

            // update row headers
            if (!mSettings.isSolidRowHeader) {
                val fromViewHolder = mHeaderRowViewHolders.get(fromRow)
                val toViewHolder = mHeaderRowViewHolders.get(toRow)
                if (fromViewHolder != null) {
                    mAdapter!!.onBindHeaderRowViewHolder(fromViewHolder, fromRow)
                }
                if (toViewHolder != null) {
                    mAdapter!!.onBindHeaderRowViewHolder(toViewHolder, toRow)
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
            if (type == ViewHolderType.Companion.COLUMN_HEADER) {
                fromVh.columnIndex = toIndex
            } else if (type == ViewHolderType.Companion.ROW_HEADER) {
                fromVh.rowIndex = toIndex
            }
        }
        val toVh = map[toIndex]
        if (toVh != null) {
            map.remove(toIndex)
            if (type == ViewHolderType.Companion.COLUMN_HEADER) {
                toVh.columnIndex = fromIndex
            } else if (type == ViewHolderType.Companion.ROW_HEADER) {
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
            if (type == ViewHolderType.Companion.COLUMN_HEADER) {
                fromVh.columnIndex = toIndex
            } else if (type == ViewHolderType.Companion.ROW_HEADER) {
                fromVh.rowIndex = toIndex
            }
        }
        val toVh = map[toIndex]
        if (toVh != null) {
            map.remove(toIndex)
            if (type == ViewHolderType.Companion.COLUMN_HEADER) {
                toVh.columnIndex = fromIndex
            } else if (type == ViewHolderType.Companion.ROW_HEADER) {
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
                mViewHolders.remove(holder.rowIndex, holder.columnIndex)
            }
        }
    }

    private val rowHeaderStartX: Int
        private get() = if (isRTL) getRight() - mManager.headerRowWidth else 0

    protected override fun drawChild(canvas: Canvas, child: View, drawingTime: Long): Boolean {
        val result: Boolean
        val viewHolder: ViewHolder? = child.getTag(R.id.tag_view_holder) as? ViewHolder
        canvas.save()
        val headerFixedX = if (mSettings.isHeaderFixed) rowHeaderStartX else mState.scrollX
        val headerFixedY = if (mSettings.isHeaderFixed) 0 else mState.scrollY
        val itemsAndColumnsLeft =
            if (!isRTL) Math.max(0, mManager.headerRowWidth - headerFixedX) else 0
        var itemsAndColumnsRight = mSettings.layoutWidth
        if (isRTL) {
            itemsAndColumnsRight += (mSettings.cellMargin
                    - mManager.headerRowWidth * (if (isHeaderFixed) 1 else 0))
        }
        if (viewHolder == null) {
            //ignore
        } else if (viewHolder.itemType == ViewHolderType.Companion.ITEM) {
            // prepare canvas rect area for draw item (cell in table)
            canvas.clipRect(
                itemsAndColumnsLeft,
                Math.max(0, mManager.headerColumnHeight - headerFixedY),
                itemsAndColumnsRight,
                mSettings.layoutHeight
            )
        } else if (viewHolder.itemType == ViewHolderType.Companion.ROW_HEADER) {
            // prepare canvas rect area for draw row header
            canvas.clipRect(
                rowHeaderStartX - mSettings.cellMargin * (if (isRTL) 0 else 1),
                Math.max(0, mManager.headerColumnHeight - headerFixedY),
                Math.max(
                    0,
                    rowHeaderStartX + mManager.headerRowWidth + mSettings.cellMargin
                ),
                mSettings.layoutHeight
            )
        } else if (viewHolder.itemType == ViewHolderType.Companion.COLUMN_HEADER) {
            // prepare canvas rect area for draw column header
            canvas.clipRect(
                itemsAndColumnsLeft,
                0,
                itemsAndColumnsRight,
                Math.max(0, mManager.headerColumnHeight - headerFixedY)
            )
        } else if (viewHolder.itemType == ViewHolderType.Companion.FIRST_HEADER) {
            // prepare canvas rect area for draw item (cell in table)
            canvas.clipRect(
                if (!isRTL) 0 else rowHeaderStartX,
                0,
                if (!isRTL) Math.max(
                    0,
                    mManager.headerRowWidth - headerFixedX
                ) else Math.max(0, rowHeaderStartX + mManager.headerRowWidth),
                Math.max(0, mManager.headerColumnHeight - headerFixedY)
            )
        }
        result = super.drawChild(canvas, child, drawingTime)
        canvas.restore() // need to restore here.
        return result
    }

    override fun onDown(e: MotionEvent?): Boolean {
        // stop smooth scrolling
        if (!mScrollerRunnable!!.isFinished) {
            mScrollerRunnable!!.forceFinished()
        }
        return true
    }

    override fun onSingleTapUp(e: MotionEvent): Boolean {
        // simple click event
        val viewHolder = getViewHolderByPosition(e.getX().toInt(), e.getY().toInt())
        if (viewHolder != null) {
            val onItemClickListener = mAdapter?.onItemClickListener
            if (onItemClickListener != null) {
                if (viewHolder.itemType == ViewHolderType.Companion.ITEM) {
                    onItemClickListener.onItemClick(
                        viewHolder.rowIndex,
                        getBindColumn(viewHolder.columnIndex)
                    )
                } else if (viewHolder.itemType == ViewHolderType.Companion.ROW_HEADER) {
                    onItemClickListener.onRowHeaderClick(viewHolder.rowIndex)
                } else if (viewHolder.itemType == ViewHolderType.Companion.COLUMN_HEADER) {
                    onItemClickListener.onColumnHeaderClick(getBindColumn(viewHolder.columnIndex))
                } else {
                    onItemClickListener.onLeftTopHeaderClick()
                }
            }
        }
        return true
    }

    override fun onLongPress(e: MotionEvent) {
        // prepare drag and drop
        // search view holder by x, y
        val viewHolder = getViewHolderByPosition(e.getX().toInt(), e.getY().toInt())
        if (viewHolder != null) {
            if (!mSettings.isDragAndDropEnabled) {
                checkLongPressForItemAndFirstHeader(viewHolder)
                return
            }
            // save start dragging touch position
            mDragAndDropPoints.setStart(
                (mState.scrollX + e.getX()).toInt(),
                (mState.scrollY + e.getY()).toInt()
            )
            if (viewHolder.itemType == ViewHolderType.Companion.COLUMN_HEADER) {
                // dragging column header
                mState.setRowDragging(false, viewHolder.rowIndex)
                mState.setColumnDragging(true, viewHolder.columnIndex)

                // set dragging flags to column's view holder
                setDraggingToColumn(viewHolder.columnIndex, true)
                mShadowHelper!!.removeColumnsHeadersShadow(this)
                mShadowHelper!!.addLeftShadow(this)
                mShadowHelper!!.addRightShadow(this)

                // update view
                refreshViewHolders()
            } else if (viewHolder.itemType == ViewHolderType.Companion.ROW_HEADER) {
                // dragging column header
                mState.setRowDragging(true, viewHolder.rowIndex)
                mState.setColumnDragging(false, viewHolder.columnIndex)

                // set dragging flags to row's view holder
                setDraggingToRow(viewHolder.rowIndex, true)
                mShadowHelper!!.removeRowsHeadersShadow(this)
                mShadowHelper!!.addTopShadow(this)
                mShadowHelper!!.addBottomShadow(this)

                // update view
                refreshViewHolders()
            } else {
                checkLongPressForItemAndFirstHeader(viewHolder)
            }
        }
    }

    private fun checkLongPressForItemAndFirstHeader(viewHolder: ViewHolder) {
        val onItemClickListener = mAdapter?.onItemLongClickListener
        if (onItemClickListener != null) {
            if (viewHolder.itemType == ViewHolderType.Companion.ITEM) {
                onItemClickListener.onItemLongClick(viewHolder.rowIndex, viewHolder.columnIndex)
            } else if (viewHolder.itemType == ViewHolderType.Companion.FIRST_HEADER) {
                onItemClickListener.onLeftTopHeaderLongClick()
            }
        }
    }

    /**
     * Method set dragging flag to all view holders in the specific column
     *
     * @param column     specific column
     * @param isDragging flag to set
     */
    @Suppress("unused")
    private fun setDraggingToColumn(column: Int, isDragging: Boolean) {
        val holders = mViewHolders.getColumnItems(column)
        for (holder: ViewHolder in holders) {
            holder.isDragging = isDragging
        }
        val holder = mHeaderColumnViewHolders.get(column)
        if (holder != null) {
            holder.isDragging = isDragging
        }
    }

    /**
     * Method set dragging flag to all view holders in the specific row
     *
     * @param row        specific row
     * @param isDragging flag to set
     */
    @Suppress("unused")
    private fun setDraggingToRow(row: Int, isDragging: Boolean) {
        val holders = mViewHolders.getRowItems(row)
        for (holder: ViewHolder in holders) {
            holder.isDragging = isDragging
        }
        val holder = mHeaderRowViewHolders.get(row)
        if (holder != null) {
            holder.isDragging = isDragging
        }
    }

    override fun onActionUp(e: MotionEvent?): Boolean {
        if (mState.isDragging) {
            // remove shadows from dragging views
            mShadowHelper!!.removeAllDragAndDropShadows(this)

            // stop smooth scrolling
            if (!mScrollerDragAndDropRunnable.isFinished) {
                mScrollerDragAndDropRunnable.stop()
            }

            // remove dragging flag from all item view holders
            val holders = mViewHolders.all
            for (holder: ViewHolder in holders) {
                holder.isDragging = false
            }

            // remove dragging flag from all column header view holders
            run {
                val count: Int = mHeaderColumnViewHolders.size()
                var i: Int = 0
                while (i < count) {
                    val key: Int = mHeaderColumnViewHolders.keyAt(i)
                    // get the object by the key.
                    val holder = mHeaderColumnViewHolders.get(key)
                    if (holder != null) {
                        holder.isDragging = false
                    }
                    i++
                }
            }

            // remove dragging flag from all row header view holders
            val count: Int = mHeaderRowViewHolders.size()
            var i = 0
            while (i < count) {
                val key: Int = mHeaderRowViewHolders.keyAt(i)
                // get the object by the key.
                mHeaderRowViewHolders.get(key)?.isDragging = false
                i++
            }

            // remove dragging flags from state
            mState.setRowDragging(false, AdaptiveTableState.Companion.NO_DRAGGING_POSITION)
            mState.setColumnDragging(false, AdaptiveTableState.Companion.NO_DRAGGING_POSITION)

            // clear dragging point positions
            mDragAndDropPoints.setStart(0, 0)
            mDragAndDropPoints.setOffset(0, 0)
            mDragAndDropPoints.setEnd(0, 0)

            // update main layout
            refreshViewHolders()
        }
        return true
    }

    private fun getViewHolderByPosition(x: Int, y: Int): ViewHolder? {
        var tempX = x
        var tempY = y
        val viewHolder: ViewHolder?
        val absX = tempX + mState.scrollX - emptySpace
        val absY = tempY + mState.scrollY
        if (!mSettings.isHeaderFixed && isRTL && (emptySpace == 0)) {
            tempX = absX - mState.scrollX
            tempY = absY - mState.scrollY
        } else if (!mSettings.isHeaderFixed && !isRTL) {
            tempX = absX
            tempY = absY
        }
        if (((tempY < mManager.headerColumnHeight) && (tempX < mManager.headerRowWidth) && !isRTL
                    || (tempY < mManager.headerColumnHeight) && (tempX > calculateRowHeadersLeft()) && isRTL)
        ) {
            // left top view was clicked
            viewHolder = mLeftTopViewHolder
        } else if (mSettings.isHeaderFixed) {
            if (tempY < mManager.headerColumnHeight) {
                // coordinate x, y in the column header's area
                val column = mManager.getColumnByXWithShift(absX, mSettings.cellMargin)
                viewHolder = mHeaderColumnViewHolders.get(column)
            } else if ((tempX < mManager.headerRowWidth && !isRTL
                        || tempX > calculateRowHeadersLeft() && isRTL)
            ) {
                // coordinate x, y in the row header's area
                val row = mManager.getRowByYWithShift(absY, mSettings.cellMargin)
                viewHolder = mHeaderRowViewHolders.get(row)
            } else {
                // coordinate x, y in the items area
                val column = mManager.getColumnByXWithShift(absX, mSettings.cellMargin)
                val row = mManager.getRowByYWithShift(absY, mSettings.cellMargin)
                viewHolder = mViewHolders!![row, column]
            }
        } else {
            if (absY < mManager.headerColumnHeight) {
                // coordinate x, y in the column header's area
                val column = mManager.getColumnByXWithShift(absX, mSettings.cellMargin)
                viewHolder = mHeaderColumnViewHolders.get(column)
            } else if ((absX < mManager.headerRowWidth && !isRTL
                        || absX - mState.scrollX > calculateRowHeadersLeft() - emptySpace && isRTL)
            ) {
                // coordinate x, y in the row header's area
                val row = mManager.getRowByYWithShift(absY, mSettings.cellMargin)
                viewHolder = mHeaderRowViewHolders.get(row)
            } else {
                // coordinate x, y in the items area
                val column = mManager.getColumnByXWithShift(absX, mSettings.cellMargin)
                val row = mManager.getRowByYWithShift(absY, mSettings.cellMargin)
                viewHolder = mViewHolders!![row, column]
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
        if (!mState.isDragging) {
            // simple scroll....
            if (!mScrollerRunnable!!.isFinished) {
                mScrollerRunnable!!.forceFinished()
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
        if (!mState.isDragging) {
            // simple fling
            mScrollerRunnable!!.start(
                mState.scrollX,
                mState.scrollY,
                velocityX.toInt() / 2,
                velocityY.toInt() / 2,
                (mManager.fullWidth - mSettings.layoutWidth + mManager.columnCount * mSettings.cellMargin).toInt(),
                (mManager.fullHeight - mSettings.layoutHeight + mManager.rowCount * mSettings.cellMargin).toInt()
            )
        }
        return true
    }

    override fun notifyDataSetChanged() {
        recycleViewHolders(true)
        mVisibleArea!![mState.scrollX, mState.scrollY, mState.scrollX + mSettings.layoutWidth] =
            mState.scrollY + mSettings.layoutHeight
        addViewHolders(mVisibleArea)
    }

    override fun notifyLayoutChanged() {
        recycleViewHolders(true)
        invalidate()
        mVisibleArea!![mState.scrollX, mState.scrollY, mState.scrollX + mSettings.layoutWidth] =
            mState.scrollY + mSettings.layoutHeight
        addViewHolders(mVisibleArea)
    }

    override fun notifyItemChanged(rowIndex: Int, columnIndex: Int) {
        val holder: ViewHolder?
        if (rowIndex == 0 && columnIndex == 0) {
            holder = mLeftTopViewHolder
        } else if (rowIndex == 0) {
            holder = mHeaderColumnViewHolders.get(columnIndex - 1)
        } else if (columnIndex == 0) {
            holder = mHeaderRowViewHolders.get(rowIndex - 1)
        } else {
            holder = mViewHolders[rowIndex - 1, columnIndex - 1]
        }
        holder?.let { viewHolderChanged(it) }
    }

    override fun notifyRowChanged(rowIndex: Int) {
        val rowHolders = mViewHolders.getRowItems(rowIndex)
        for (holder: ViewHolder in rowHolders) {
            viewHolderChanged(holder)
        }
    }

    override fun notifyColumnChanged(columnIndex: Int) {
        val columnHolders = mViewHolders.getColumnItems(columnIndex)
        for (holder: ViewHolder in columnHolders) {
            viewHolderChanged(holder)
        }
    }

    private fun viewHolderChanged(holder: ViewHolder) {
        if (holder.itemType == ViewHolderType.Companion.FIRST_HEADER) {
            mLeftTopViewHolder = holder
            mAdapter!!.onBindLeftTopHeaderViewHolder(mLeftTopViewHolder!!)
        } else if (holder.itemType == ViewHolderType.Companion.COLUMN_HEADER) {
            mHeaderColumnViewHolders.remove(holder.columnIndex)
            recycleViewHolder(holder)
            addViewHolder(holder.rowIndex, holder.columnIndex, holder.itemType)
        } else if (holder.itemType == ViewHolderType.Companion.ROW_HEADER) {
            mHeaderRowViewHolders.remove(holder.rowIndex)
            recycleViewHolder(holder)
            addViewHolder(holder.rowIndex, holder.columnIndex, holder.itemType)
        } else {
            mViewHolders!!.remove(holder.rowIndex, holder.columnIndex)
            recycleViewHolder(holder)
            addViewHolder(holder.rowIndex, holder.columnIndex, holder.itemType)
        }
    }

    var isHeaderFixed: Boolean
        get() = mSettings.isHeaderFixed
        set(headerFixed) {
            mSettings.setHeaderFixed(headerFixed)
        }
    var isSolidRowHeader: Boolean
        get() = mSettings.isSolidRowHeader
        set(solidRowHeader) {
            mSettings.setSolidRowHeader(solidRowHeader)
        }
    var isDragAndDropEnabled: Boolean
        get() = mSettings.isDragAndDropEnabled
        set(enabled) {
            mSettings.isDragAndDropEnabled = enabled
        }

    private class TableInstanceSaver : Parcelable {
        var mScrollX = 0
        var mScrollY = 0
        var mLayoutDirection = 0
        var mFixedHeaders = false

        constructor()
        protected constructor(`in`: Parcel) {
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
        private val EXTRA_STATE_SUPER = "EXTRA_STATE_SUPER"
        private val EXTRA_STATE_VIEW_GROUP = "EXTRA_STATE_VIEW_GROUP"
        private val SHADOW_THICK = 20
        private val SHADOW_HEADERS_THICK = 10
    }
}
