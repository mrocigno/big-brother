package br.com.mrocigno.bigbrother.common.helpers

import br.com.mrocigno.bigbrother.common.utils.appendIsolated
import br.com.mrocigno.bigbrother.common.utils.removeComma

/**
 * Only for selections by now
 */
class SQLBuilder(
    sql: String? = null,
    builder: (SQLBuilder.() -> Unit)? = null
) {

    var tableName = ""
    val columns = mutableListOf<String>()
    val wheres = mutableMapOf<String, String>()
    val orderBy = mutableMapOf<String, String>()

    private val lexer = SQLLexer(sql.orEmpty())

    init {
        var currentToken = lexer.scan()
        while (currentToken.type != TokenType.EOS) {
            currentToken = when (currentToken.type) {
                TokenType.KEYWORD -> currentToken.consumeKeyword()
                else -> lexer.scan()
            }
        }

        builder?.invoke(this)
    }

    fun getSelectSql() =
        StringBuilder()
            .append("SELECT")
            .appendColumns()
            .appendIsolated("FROM")
            .appendIsolated(tableName)
            .appendWhere()
            .appendOrderBy()
            .toString()

    fun clearConditions() = wheres.clear()
    fun clearOrderBy() = orderBy.clear()

    fun condition(modifier: String? = null, condition: String?) {
        condition ?: return
        val _modifier = when {
            wheres.isEmpty() -> "WHERE"
            modifier.isNullOrBlank() -> "AND"
            else -> modifier
        }

        if (wheres[condition].isNullOrBlank()) wheres[condition] = _modifier
    }

    fun condition(modifier: String? = null, block: () -> String?) =
        condition(modifier, block())

    fun orderAsc(columnName: String) {
        orderBy[columnName] = "ASC"
    }

    fun orderDesc(columnName: String) {
        orderBy[columnName] = "DESC"
    }

    private fun StringBuilder.appendColumns() = apply {
        val str =
            if (columns.isEmpty()) "*"
            else columns.joinToString(", ")

        appendIsolated(str)
    }

    private fun StringBuilder.appendWhere() = apply {
        if (wheres.isEmpty()) return@apply

        wheres.entries.forEach {
            appendIsolated(it.value.uppercase())
            appendIsolated(it.key)
        }
    }

    private fun StringBuilder.appendOrderBy() = apply {
        if (orderBy.isEmpty()) return@apply

        appendIsolated("ORDER BY")
        orderBy.entries.forEachIndexed { index, entry ->
            if (index > 0) append(",")
            appendIsolated(entry.key)
            appendIsolated(entry.value.uppercase())
        }
    }

    private fun Token.consumeKeyword() =
        when (value.uppercase()) {
            "SELECT" -> consumeColumns()
            "FROM", "UPDATE", "DELETE" -> consumeTableName()
            "WHERE", "AND", "OR" -> consumeWhere()
            "ORDER" -> consumeOrder()
            else -> lexer.scan()
        }

    private fun consumeColumns(): Token {
        var token = lexer.scan()
        while (token.type == TokenType.IDENTIFIER) {
            columns.add(token.value.removeComma())
            token = lexer.scan()
        }
        return token
    }

    private fun consumeTableName(): Token {
        val token = lexer.scan()
        if (token.type == TokenType.IDENTIFIER) {
            tableName = token.value
        }
        return lexer.scan()
    }

    private fun Token.consumeWhere(): Token {
        // Get value of current token
        val key = value

        // Get next token
        var token = lexer.scan()

        val temp = mutableListOf<String>()
        while (token.type == TokenType.IDENTIFIER) {
            temp.add(token.value)
            token = lexer.scan()
        }
        wheres[temp.joinToString(" ")] = key
        return token
    }

    private fun consumeOrder(): Token {
        // Ignore next scan but check structure
        check(lexer.scan().value.uppercase() == "BY") { "should have keyword 'BY' after ORDER" }

        return consumeOrderColumns()
    }

    private fun consumeOrderColumns(): Token {
        val token = lexer.scan()
        return if (token.type == TokenType.IDENTIFIER) {
            val sortKeyword = lexer.scan()
            check(sortKeyword.type == TokenType.KEYWORD) { "should have keyword 'ASC' or 'DESC'" }
            orderBy[token.value] = sortKeyword.value
            consumeOrderColumns()
        } else {
            token
        }
    }
}

fun main() {
    println(SQLBuilder("SELECT * FROM teste WHERE id = 1 OR session = 'bcs' AND session = 'ab ds sad asd sad asc' ORDER BY id ASC, session DESC")
        .getSelectSql())
    SQLBuilder("update tblReportLogs set session_id = 2 where id = 2")
}