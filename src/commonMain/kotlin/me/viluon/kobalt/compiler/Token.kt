package me.viluon.kobalt.compiler

import me.viluon.kobalt.compiler.syntax.Keyword
import me.viluon.kobalt.compiler.syntax.Operator
import me.viluon.kobalt.extensions.isValidInsideIdentifier
import me.viluon.kobalt.standard.VersionSpan
import me.viluon.kobalt.standard.Versioned

interface Token : Versioned {
    val length: Int
    val valid: Boolean
    override val span: VersionSpan
        get() = VersionSpan.permanent
}

interface StringToken : Token {
    val str: String
    override val length: Int
        get() = str.length
}

interface SingleCharacterToken : Token {
    override val length: Int
        get() = 1
    override val valid: Boolean
        get() = true
}

// TODO string interpolation
data class TkStringLiteral(val str: String, val finished: Boolean, override val length: Int) : Token {
    override val valid: Boolean
        get() = finished
}

data class TkIntegerLiteral(val n: Long, override val length: Int, override val valid: Boolean) : Token
data class TkDoubleLiteral(val n: Double, override val length: Int, override val valid: Boolean) : Token

inline class TkKeyword(val kw: Keyword) : Token {
    override val length: Int
        get() = kw.keyword.length
    override val valid: Boolean
        get() = true
    override val span: VersionSpan
        get() = kw.span
}

// TODO lex within comments
data class TkComment(override val str: String, val finished: Boolean) : StringToken {
    override val valid: Boolean
        get() = finished
}

inline class TkIdentifier(override val str: String) : StringToken {
    override val valid: Boolean get() = str.isNotEmpty() && str[0] !in '0'..'9' && str.all(Char::isValidInsideIdentifier)
}
inline class TkWhitespace(override val str: String) : StringToken {
    override val valid: Boolean
        get() = true
}

data class TkLabel(val str: String, override val length: Int, override val valid: Boolean) : Token {
    companion object : Versioned {
        override val span = VersionSpan.since52
    }

    override val span: VersionSpan
        get() = Companion.span
}

inline class TkOperator(val operator: Operator) : Token {
    override val length: Int
        get() = operator.length
    override val valid: Boolean
        get() = true
    override val span: VersionSpan
        get() = operator.span
}

object TkOpenParen : SingleCharacterToken
object TkCloseParen : SingleCharacterToken
object TkOpenBrace : SingleCharacterToken
object TkCloseBrace : SingleCharacterToken
object TkOpenBracket : SingleCharacterToken
object TkCloseBracket : SingleCharacterToken

object TkDot : SingleCharacterToken
object TkComma : SingleCharacterToken
object TkColon : SingleCharacterToken
object TkSemicolon : SingleCharacterToken

object TkEof : Token {
    override val length: Int
        get() = 0
    override val valid: Boolean
        get() = true
}
