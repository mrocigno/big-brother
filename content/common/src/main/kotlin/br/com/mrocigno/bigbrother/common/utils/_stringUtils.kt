package br.com.mrocigno.bigbrother.common.utils

import kotlin.math.roundToInt
import kotlin.math.roundToLong

fun StringBuilder.appendSeparation(): StringBuilder = append(" - ")

fun StringBuilder.appendSpace(count: Int): StringBuilder = append(space(count))

fun StringBuilder.appendIsolated(str: String) = append(" ", str)

fun space(count: Int) = StringBuilder().apply {
    repeat(count) { append("|   ") }
}

fun String.removeComma() = replace(",", "")

fun String.trimExtension() = substringBeforeLast(".")

fun String.startsWith(vararg chars: Char) =
    firstOrNull()?.run(chars::contains) ?: false

fun String.isJson() = trim().startsWith('{', '[')

fun String.toIntRound() = toFloat().roundToInt()

fun String.toLongRound() = toDouble().roundToLong()