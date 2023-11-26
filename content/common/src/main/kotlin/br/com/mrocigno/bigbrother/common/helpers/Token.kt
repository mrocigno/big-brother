package br.com.mrocigno.bigbrother.common.helpers

data class Token(
    val type: TokenType,
    val value: String
)

enum class TokenType {
    KEYWORD, IDENTIFIER, EOS
}