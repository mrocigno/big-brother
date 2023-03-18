package br.com.mrocigno.sandman.utils

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import br.com.mrocigno.sandman.Sandman
import br.com.mrocigno.sandman.log.SandmanLog.Companion.tag

internal fun Activity.printScreen(): Bitmap {
    val root = rootView
    val bitmap = Bitmap.createBitmap(root.width, root.height, Bitmap.Config.ARGB_8888)
    Canvas(bitmap).apply {
        root.draw(this)
        drawClick()
    }
    return bitmap
}

internal fun Bitmap.save(context: Context, filename: String) {
    context.openFileOutput(filename, Context.MODE_PRIVATE).use {
        compress(Bitmap.CompressFormat.PNG, 100, it)
    }
}

internal fun Canvas.drawClick(paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
    color = Color.RED
    style = Paint.Style.STROKE
    strokeWidth = 10f
}) {
    lastClickPosition?.run { drawCircle(x, y, 50f, paint) }
        ?: Sandman.tag().w("Cannot get last click position - did you implement sandman.report?")
}