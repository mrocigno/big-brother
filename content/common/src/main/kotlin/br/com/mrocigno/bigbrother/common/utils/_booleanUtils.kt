package br.com.mrocigno.bigbrother.common.utils

fun Boolean.ifTrue(block: () -> Unit) =
    if (this) block() else Unit

fun Boolean.ifFalse(block: () -> Unit) =
    if (!this) block() else Unit