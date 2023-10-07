package br.com.mrocigno.bigbrother.common.table

import android.graphics.Point

/**
 * Helps implement dragging feature.
 * Contains start, offset and end point in drag and drop mode.
 */
internal class DragAndDropPoints() {
    /**
     * Start dragging touch point
     */
    val start: Point

    /**
     * Screen offset (touch position)
     */
    val offset: Point

    /**
     * End dragging touch point
     */
    val end: Point

    init {
        start = Point()
        offset = Point()
        end = Point()
    }

    fun setStart(x: Int, y: Int) {
        start.set(x, y)
    }

    fun setOffset(x: Int, y: Int) {
        offset.set(x, y)
    }

    fun setEnd(x: Int, y: Int) {
        end.set(x, y)
    }
}
