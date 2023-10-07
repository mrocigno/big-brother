package br.com.mrocigno.bigbrother.common.table

import android.view.View
import android.widget.Scroller

/**
 * Fling table layout logic
 * {@see http://stackoverflow.com/a/6219382/842697 }
 */
internal class SmoothScrollRunnable(
    /**
     * Scrollable view
     */
    private val mView: View
) : Runnable {
    /**
     * Need to calculate offset.
     */
    private val mScroller: Scroller
    private var mLastX: Int = 0
    private var mLastY: Int = 0

    init {
        mScroller = Scroller(mView.getContext())
    }

    fun start(
        initX: Int,
        initY: Int,
        initialVelocityX: Int,
        initialVelocityY: Int,
        maxX: Int,
        maxY: Int
    ) {
        // start smooth scrolling
        mScroller.fling(initX, initY, initialVelocityX, initialVelocityY, 0, maxX, 0, maxY)

        // save new data
        mLastX = initX
        mLastY = initY

        // run self
        mView.post(this)
    }

    public override fun run() {
        if (mScroller.isFinished()) {
            return
        }
        // calculate offset
        val more: Boolean = mScroller.computeScrollOffset()
        val x: Int = mScroller.getCurrX()
        val y: Int = mScroller.getCurrY()
        val diffX: Int = mLastX - x
        val diffY: Int = mLastY - y
        if (diffX != 0 || diffY != 0) {
            mView.scrollBy(diffX, diffY)
            mLastX = x
            mLastY = y
        }
        if (more) {
            // run self
            mView.post(this)
        }
    }

    val isFinished: Boolean
        get() {
            return mScroller.isFinished()
        }

    fun forceFinished() {
        if (!mScroller.isFinished()) {
            mScroller.forceFinished(true)
        }
    }
}
