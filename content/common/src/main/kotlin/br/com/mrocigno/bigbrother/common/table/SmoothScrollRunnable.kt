/*
* A copy/past from https://github.com/Cleveroad/AdaptiveTableLayout
*/

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
    private val mScroller: Scroller = Scroller(mView.context)
    private var mLastX: Int = 0
    private var mLastY: Int = 0

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

    override fun run() {
        if (mScroller.isFinished) {
            return
        }
        // calculate offset
        val more: Boolean = mScroller.computeScrollOffset()
        val x: Int = mScroller.currX
        val y: Int = mScroller.currY
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

    val isFinished: Boolean get() = mScroller.isFinished

    fun forceFinished() {
        if (!mScroller.isFinished) {
            mScroller.forceFinished(true)
        }
    }
}
