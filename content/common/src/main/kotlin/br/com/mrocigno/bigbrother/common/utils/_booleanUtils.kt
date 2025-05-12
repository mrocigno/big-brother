package br.com.mrocigno.bigbrother.common.utils

fun Boolean.ifTrue(block: () -> Unit): Boolean = apply {
    if (this) block()
}

fun Boolean.ifFalse(block: () -> Unit): Boolean = apply {
    if (!this) block()
}
