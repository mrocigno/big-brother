package br.com.mrocigno.bigbrother.common.utils

import java.util.Locale
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

fun String.isHtml(): Boolean {
    val trimmed = trim()
    return trimmed.startsWith("<!DOCTYPE", ignoreCase = true) ||
            trimmed.contains("<html", ignoreCase = true) ||
            trimmed.contains("<body", ignoreCase = true)
}

fun String.toIntRound() = toFloat().roundToInt()

fun String.toLongRound() = toDouble().roundToLong()

fun String.compatCapitalize() = replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }