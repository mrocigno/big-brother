package br.com.mrocigno.bigbrother.common.utils

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF

fun Activity.printScreen(clickPosition: PointF? = null): Bitmap {
    val root = rootView
    val bitmap = runCatching {
        val temp = Bitmap.createBitmap(root.width, root.height, Bitmap.Config.ARGB_8888)
        Canvas(temp).apply {
            root.draw(this)
            clickPosition?.run(::drawClick)
        }
        temp
    }.getOrElse {
        assets.open("file-not-found.webp").use {
            BitmapFactory.decodeStream(it)
        }
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