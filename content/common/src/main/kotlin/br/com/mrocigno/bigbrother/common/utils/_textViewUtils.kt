package br.com.mrocigno.bigbrother.common.utils

import android.content.res.ColorStateList
import androidx.appcompat.widget.AppCompatTextView
import br.com.mrocigno.bigbrother.common.R

fun AppCompatTextView.byMethod(method: String) {
    val color = when (method) {
        "POST" -> R.color.bb_net_entry_post
        "PUT" -> R.color.bb_net_entry_put
        "GET" -> R.color.bb_net_entry_get
        "DELETE" -> R.color.bb_net_entry_delete
        else -> R.color.bb_text_title
    }
    val colorList = ColorStateList.valueOf(context.getColor(color))
    backgroundTintList = colorList
    setTextColor(colorList)
}