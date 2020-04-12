package me.viluon.kobalt.compiler.syntax

import me.viluon.kobalt.extensions.isValidInsideIdentifier
import me.viluon.kobalt.standard.VersionSpan
import me.viluon.kobalt.standard.Versioned

sealed class Token : Versioned {
    abstract val length: Int
    abstract val valid: Boolean
    override val span get() = VersionSpan.permanent
}

abstract class StringToken : Token() {
    abstract val str: String
    override val length get() = str.length
}

open class SingleCharacterToken : Token() {
    override val length get() = 1
    override val valid get() = true
}

// TODO string interpolation
data class TkStringLiteral(val str: String, val finished: Boolean, override val length: Int) : Token() {
    override val valid get() = finished
}

data class TkIntegerLiteral(val n: Long, override val length: Int, override val valid: Boolean) : Token()

data class TkDoubleLiteral(val n: Double, override val length: Int, override val valid: Boolean) : Token()

data class TkKeyword(val kw: Keyword) : Token() {
    override val length get() = kw.keyword.length
    override val valid get() = true
    override val span get() = kw.span
}

// TODO lex within comments
data class TkComment(override val str: String, val finished: Boolean) : StringToken() {
    override val valid get() = finished
}

data class TkIdentifier(override val str: String) : StringToken() {
    override val valid: Boolean get() = str.isNotEmpty() && str[0] !in '0'..'9' && str.all(Char::isValidInsideIdentifier)
}

data class TkWhitespace(override val str: String) : StringToken() {
    override val valid get() = true
}

data class TkLabel(val str: String, override val length: Int, override val valid: Boolean) : Token() {
    companion object : Versioned {
        override val span = VersionSpan.since52
    }

    override val span get() = Companion.span
}

data class TkOperator(val operator: Operator) : Token() {
    override val length get() = operator.length
    override val valid get() = true
    override val span get() = operator.span
}

object TkOpenParen : SingleCharacterToken()
object TkCloseParen : SingleCharacterToken()
object TkOpenBrace : SingleCharacterToken()
object TkCloseBrace : SingleCharacterToken()
object TkOpenBracket : SingleCharacterToken()
object TkCloseBracket : SingleCharacterToken()

object TkDot : SingleCharacterToken()
object TkComma : SingleCharacterToken()
object TkColon : SingleCharacterToken()
object TkSemicolon : SingleCharacterToken()

object TkEof : Token() {
    override val length get() = 0
    override val valid get() = true
}
