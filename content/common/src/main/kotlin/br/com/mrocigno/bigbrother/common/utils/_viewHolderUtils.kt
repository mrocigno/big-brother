package br.com.mrocigno.bigbrother.common.utils

import android.content.res.ColorStateList
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView.ViewHolder

fun ViewHolder.getColor(@ColorRes res: Int) =
    ContextCompat.getColor(itemView.context, res)

fun ViewHolder.getColorState(@ColorRes res: Int) =
    ColorStateList.valueOf(ContextCompat.getColor(itemView.context, res))
