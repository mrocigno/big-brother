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

    val alert = AlertDialog.Builder(this).apply {
        title.takeIf { it.isNotEmpty() }?.run(::setTitle)
        view?.run(::setView)
        view?.onView()
        positiveButton?.run {
            setPositiveButton(first, null)
        }
        negativeButton?.run {
            setNegativeButton(first, null)
        }
    }.show()

    positiveButton?.second?.run {
        alert.getButton(AlertDialog.BUTTON_POSITIVE)?.setOnClickListener {
            invoke(alert, view)
        }
    }

    negativeButton?.second?.run {
        alert.getButton(AlertDialog.BUTTON_NEGATIVE)?.setOnClickListener {
            invoke(alert, view)
        }
    }

    return alert
}