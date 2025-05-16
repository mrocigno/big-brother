package br.com.mrocigno.bigbrother.common.utils

import android.content.DialogInterface
import android.view.View
import androidx.annotation.ColorRes
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentActivity

typealias DialogButtonClick = DialogInterface.(View?) -> Unit

fun FragmentActivity.showDialog(
    content: Int = -1,
    title: String = "",
    positiveButton: Pair<String, DialogButtonClick>? = null,
    @ColorRes positiveButtonColor: Int? = null,
    negativeButton: Pair<String, DialogButtonClick>? = null,
    @ColorRes negativeButtonColor: Int? = null,
    onView: View.() -> Unit = {},
): AlertDialog {

    val view = content.takeIf { it != -1 }?.let {
        View.inflate(this, it, null)
    }

    val alert = AlertDialog.Builder(this).apply {
        title.takeIf { it.isNotEmpty() }?.run(::setTitle)
        view?.onView()
        view?.run(::setView)
        positiveButton?.run {
            setPositiveButton(first, null)
        }
        negativeButton?.run {
            setNegativeButton(first, null)
        }
    }.show()

    positiveButton?.second?.run {
        alert.getButton(AlertDialog.BUTTON_POSITIVE)?.apply {
            positiveButtonColor
                ?.let(::getColor)
                ?.run(::setTextColor)
            setOnClickListener {
                invoke(alert, view)
            }
        }
    }

    negativeButton?.second?.run {
        alert.getButton(AlertDialog.BUTTON_NEGATIVE)?.apply {
            negativeButtonColor
                ?.let(::getColor)
                ?.run(::setTextColor)
            setOnClickListener {
                invoke(alert, view)
            }
        }
    }

    return alert
}