package br.com.mrocigno.bigbrother.common.utils

fun Map<String, List<String>>?.toReadable(): String {
    if (this.isNullOrEmpty()) return "empty"

    val builder = StringBuilder()
    keys.forEach {
        builder.append(it)
        builder.append(": ")
        builder.append(this[it]?.joinToString(", "))
        builder.append("\n")
    }
    return builder.toString()
}

fun Map<String, List<String>>?.toHtml(): String {
    if (this.isNullOrEmpty()) return "empty"

    val builder = StringBuilder()
    this.keys.forEach {
        builder.append("<b>$it</b>")
        builder.append(": ")
        builder.append(this[it]?.joinToString(", "))
        builder.appendLine()
    }
    return builder.toString()
}