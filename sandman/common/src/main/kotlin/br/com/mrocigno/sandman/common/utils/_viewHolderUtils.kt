package br.com.mrocigno.sandman.common.utils

import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView.ViewHolder

fun ViewHolder.getColor(@ColorRes res: Int) =
    ContextCompat.getColor(itemView.context, res)