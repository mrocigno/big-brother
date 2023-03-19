package br.com.mrocigno.sandman.common.utils

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF

fun Activity.printScreen(clickPosition: PointF? = null): Bitmap {
    val root = rootView
    val bitmap = Bitmap.createBitmap(root.width, root.height, Bitmap.Config.ARGB_8888)
    Canvas(bitmap).apply {
        root.draw(this)
        clickPosition?.run(::drawClick)
    }
    return bitmap
}

fun Bitmap.save(context: Context, filename: String) {
    context.openFileOutput(filename, Context.MODE_PRIVATE).use {
        compress(Bitmap.CompressFormat.PNG, 100, it)
    }
}

fun Canvas.drawClick(position: PointF, paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
    color = Color.RED
    style = Paint.Style.STROKE
    strokeWidth = 10f
}) {
    drawCircle(position.x, position.y, 50f, paint)
}