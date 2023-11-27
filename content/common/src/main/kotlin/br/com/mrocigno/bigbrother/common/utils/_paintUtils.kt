package br.com.mrocigno.bigbrother.common.utils

import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode

fun Paint.cleaner() = apply {
    xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
}