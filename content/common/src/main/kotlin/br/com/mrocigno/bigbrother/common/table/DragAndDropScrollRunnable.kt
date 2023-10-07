package br.com.mrocigno.bigbrother.common.table

import android.view.View

/**
 * Move table layout logic in dragging mode
 */
internal class DragAndDropScrollRunnable(private val mView: View) : Runnable {
    var isFinished: Boolean = true
        private set
    private var mDiffX: Int = 0
    private var mDiffY: Int = 0
    @Synchronized
    fun touch(touchX: Int, touchY: Int, @ScrollType orientation: Int) {
        val partOfWidth: Int = mView.width / 4
        // vertical scroll area (top, bottom)
        val partOfHeight: Int = mView.height / 4
        if (orientation == ScrollType.Companion.SCROLL_HORIZONTAL) {
            if (touchX < partOfWidth) {
                // if touch in left horizontal area -> scroll to left
                start(touchX - partOfWidth, 0)
            } else if (touchX > mView.width - partOfWidth) {
                // if touch in right horizontal area -> scroll to right
                start(touchX - mView.width + partOfWidth, 0)
            } else {
                // touch between scroll left and right areas.
                mDiffX = 0
                mDiffY = 0
            }
        } else if (orientation == ScrollType.Companion.SCROLL_VERTICAL) {
            if (touchY < partOfHeight) {
                // if touch in top vertical area -> scroll to top
                start(0, touchY - partOfHeight)
            } else if (touchY > mView.height - partOfHeight) {
                // if touch in bottom vertical area -> scroll to bottom
                start(0, touchY - mView.height + partOfHeight)
            } else {
                // touch between scroll top and bottom areas.
                mDiffX = 0
                mDiffY = 0
            }
        }
    }

    @Synchronized
    private fun start(diffX: Int, diffY: Int) {
        // save start data
        mDiffX = diffX
        mDiffY = diffY
        // check if scrolling in the process
        if (isFinished) {
            // start scroll
            isFinished = false
            mView.post(this)
        }
    }

    public override fun run() {
        // scroll speed. Calculated by hand.
        val shiftDistanceX: Int = mDiffX / 5
        val shiftDistanceY: Int = mDiffY / 5
        if ((shiftDistanceX != 0 || shiftDistanceY != 0) && !isFinished) {
            // change state
            isFinished = false
            // scroll view
            mView.scrollBy(shiftDistanceX, shiftDistanceY)
            // start self again
            mView.post(this)
        } else {
            // have no shift distance or need to finish.
            stop()
        }
    }

    @Synchronized
    fun stop() {
        // vars to default
        mDiffX = 0
        mDiffY = 0

        // change state
        isFinished = true

        // remove callbacks
        mView.removeCallbacks(this)
    }
}
