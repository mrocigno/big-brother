package br.com.mrocigno.bigbrother.common.table

import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import androidx.core.view.GestureDetectorCompat

internal class ScrollHelper(context: Context) : GestureDetector.OnGestureListener {
    /**
     * Gesture detector -> Scroll, Fling, Tap, LongPress, ...
     * Using when user need to scroll table
     */
    private val mGestureDetectorCompat: GestureDetectorCompat

    private var mListener: ScrollHelperListener? = null

    init {
        mGestureDetectorCompat = GestureDetectorCompat(context, this)
        mGestureDetectorCompat.setIsLongpressEnabled(true)
    }

    fun setListener(listener: ScrollHelperListener?) {
        mListener = listener
    }

    public override fun onDown(e: MotionEvent): Boolean {
        // catch down action
        return mListener == null || mListener!!.onDown(e)
    }

    public override fun onShowPress(e: MotionEvent) {
        // nothing to do
    }

    public override fun onSingleTapUp(e: MotionEvent): Boolean {
        // catch click action
        return mListener != null && mListener!!.onSingleTapUp(e)
    }

    public override fun onScroll(
        e1: MotionEvent,
        e2: MotionEvent,
        distanceX: Float,
        distanceY: Float
    ): Boolean {
        // catch scroll action
        return mListener != null && mListener!!.onScroll(e1, e2, distanceX, distanceY)
    }

    public override fun onLongPress(e: MotionEvent) {
        // catch long click action
        if (mListener != null) {
            mListener!!.onLongPress(e)
        }
    }

    public override fun onFling(
        e1: MotionEvent,
        e2: MotionEvent,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        // catch fling action
        return mListener == null || mListener!!.onFling(e1, e2, velocityX, velocityY)
    }

    fun onTouch(event: MotionEvent): Boolean {
        if (event.getAction() == MotionEvent.ACTION_UP && mListener != null) {
            // stop drag and drop mode
            mListener!!.onActionUp(event)
        }
        // connect GestureDetector with our touch events
        return mGestureDetectorCompat.onTouchEvent(event)
    }

    internal open interface ScrollHelperListener {
        fun onDown(e: MotionEvent?): Boolean
        fun onSingleTapUp(e: MotionEvent): Boolean
        fun onLongPress(e: MotionEvent)
        fun onActionUp(e: MotionEvent?): Boolean
        fun onScroll(
            e1: MotionEvent?,
            e2: MotionEvent?,
            distanceX: Float,
            distanceY: Float
        ): Boolean

        fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean
    }
}
