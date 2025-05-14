package br.com.mrocigno.bigbrother.database.model

internal class ColumnContent(
    val data: String,
    val shouldExpand: Boolean = false,
    val sqlExpand: String? = null
) {

    val isJson: Boolean
        get() = data.trimStart().run {
            startsWith("{") || startsWith("[")
        }
}