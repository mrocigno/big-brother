package br.com.mrocigno.sandman.utils

import android.content.res.ColorStateList
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

internal fun Fragment.getColor(@ColorRes res: Int) =
    ContextCompat.getColor(requireContext(), res)

internal fun Fragment.getColorState(@ColorRes res: Int) =
    ColorStateList.valueOf(getColor(res))