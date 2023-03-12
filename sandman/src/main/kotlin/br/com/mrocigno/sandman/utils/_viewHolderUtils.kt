package br.com.mrocigno.sandman.utils

import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView.ViewHolder

internal fun ViewHolder.getColor(@ColorRes res: Int) =
    ContextCompat.getColor(itemView.context, res)