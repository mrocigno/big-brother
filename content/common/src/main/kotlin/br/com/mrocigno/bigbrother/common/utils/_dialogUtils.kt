package br.com.mrocigno.bigbrother.common.utils

import android.content.DialogInterface
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

typealias DialogButtonClick = DialogInterface.(View?) -> Unit

fun AppCompatActivity.showDialog(
    content: Int = -1,
    title: String = "",
    positiveButton: Pair<String, DialogButtonClick>? = null,
    negativeButton: Pair<String, DialogButtonClick>? = null,
    onView: View.() -> Unit = {},
): AlertDialog {

    val view = content.takeIf { it != -1 }?.let {
        View.inflate(this, it, null)
    }

    return AlertDialog.Builder(this).apply {
        title.takeIf { it.isNotEmpty() }?.run(::setTitle)
        view?.run(::setView)
        view?.onView()
        positiveButton?.run {
            setPositiveButton(first) { dialog, _ ->
                second(dialog, view)
            }
        }
        negativeButton?.run {
            setNegativeButton(first) { dialog, _ ->
                second(dialog, view)
            }
        }
    }.show()
}