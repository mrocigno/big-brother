package br.com.mrocigno.bigbrother.common.helpers

class SQLLexer(private val sql: String) {

    private var index: Int = 0
    private val keywords = listOf(
        "SELECT", "UPDATE", "DELETE",
        "FROM",
        "WHERE", "AND", "OR",
        "ORDER", "BY", "ASC", "DESC"
    )

    private fun getTokenType(tokenValue: String) =
        if (keywords.contains(tokenValue.uppercase())) {
            TokenType.KEYWORD
        } else {
            TokenType.IDENTIFIER
        }

    fun scan(): Token = try {
        val char = sql[index]

        when {
            char.isForbiddenChar() -> { index++; scan() }
            char.isQuote() -> scanString()
            else -> scanValue()
        }
    } catch (ex: StringIndexOutOfBoundsException) {
        Token(TokenType.EOS, "")
    }

    private fun scanValue(): Token {
        val start = index
        while (index < sql.length && !sql[index].isForbiddenChar()) index++

        val lexeme = sql.substring(start, index)
        return Token(getTokenType(lexeme), lexeme)
    }

    private fun scanString(): Token {
        val start = index

        index++ // to count first quote
        while (index < sql.length && !sql[index].isQuote()) index++
        index++ // to count last quote

        val lexeme = sql.substring(start, index)
        return Token(getTokenType(lexeme), lexeme)
    }

    private fun Char.isForbiddenChar() = " ,".contains(this)

    private fun Char.isQuote() = "\"'".contains(this)
}
