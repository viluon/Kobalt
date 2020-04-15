package me.viluon.kobalt.compiler.syntax

fun length(token: Token): Int = when (token) {
    is Literal -> token.length
    is Keyword -> token::class.simpleName!!.length - 4
    is TkComment -> token.contents.length
    is TkIdentifier -> token.name.length
    is TkWhitespace -> token.contents.length
    is TkLabel -> token.length
    is Operator -> token.operator.length
    is SingleCharacter -> 1
    TkEof -> 0
    else -> throw IllegalStateException("No branch matches $token")
}

val Token.length: Int get() = length(this)

fun <T> keyword(token: T): String where T : Token, T : Keyword = token::class.simpleName!!.substring(4).toLowerCase()
val <T> T.keyword: String where T : Token, T : Keyword get() = keyword(this)
