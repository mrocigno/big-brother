package br.com.mrocigno.sandman.common.utils

fun StringBuilder.appendSeparation(): StringBuilder = append(" - ")

fun StringBuilder.appendSpace(count: Int): StringBuilder = append(space(count))

fun space(count: Int) = StringBuilder().apply {
    repeat(count) { append("|   ") }
}