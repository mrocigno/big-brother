package br.com.mrocigno.bigbrother.database.model

class TableDump(
    val data: List<Map<String, String>>,
    val executedSql: String,
    val rowCount: Int,
    val page: Int
) {

    val columnNames = data.firstOrNull()?.keys?.toTypedArray().orEmpty()
}