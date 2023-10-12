/*
* A copy/past from https://github.com/Cleveroad/AdaptiveTableLayout
*/

package br.com.mrocigno.bigbrother.common.table

import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import androidx.core.view.GestureDetectorCompat

internal class ScrollHelper(context: Context) : GestureDetector.OnGestureListener {

    private var mListener: ScrollHelperListener? = null
    private val mGestureDetectorCompat = GestureDetectorCompat(context, this).apply {
        setIsLongpressEnabled(true)
    }

    fun setListener(listener: ScrollHelperListener?) = apply {
        mListener = listener
    }

    override fun onDown(e: MotionEvent) =
        mListener == null || mListener!!.onDown(e)

    override fun onShowPress(e: MotionEvent) = Unit

    override fun onSingleTapUp(e: MotionEvent) =
        mListener != null && mListener!!.onSingleTapUp(e)

    override fun onScroll(
        e1: MotionEvent,
        e2: MotionEvent,
        distanceX: Float,
        distanceY: Float
    ): Boolean {
        // catch scroll action
        return mListener != null && mListener!!.onScroll(e1, e2, distanceX, distanceY)
    }

    override fun onLongPress(e: MotionEvent) {
        mListener?.onLongPress(e)
    }

    override fun onFling(
        e1: MotionEvent,
        e2: MotionEvent,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        // catch fling action
        return mListener == null || mListener!!.onFling(e1, e2, velocityX, velocityY)
    }

    fun onTouch(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_UP && mListener != null) {
            // stop drag and drop mode
            mListener!!.onActionUp(event)
        }
        // connect GestureDetector with our touch events
        return mGestureDetectorCompat.onTouchEvent(event)
    }

    internal interface ScrollHelperListener {
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
