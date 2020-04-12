package me.viluon.kobalt.compiler.syntax

import me.viluon.kobalt.standard.VersionSpan.Companion.permanent
import me.viluon.kobalt.standard.VersionSpan.Companion.since52
import me.viluon.kobalt.standard.VersionSpan.Companion.since53
import me.viluon.kobalt.standard.Versioned

sealed class Token : Versioned {
    override val span = permanent
}

interface Operator {
    val operator: String
}
interface Keyword
interface Literal {
    val length: Int
}
interface SingleCharacter

// TODO how do we store lex errors inside the string?
data class TkStringLiteral(val str: String, override val length: Int, val valid: Boolean) : Token(), Literal
data class TkIntegerLiteral(val n: Long, override val length: Int, val valid: Boolean) : Token(), Literal
data class TkDoubleLiteral(val n: Double, override val length: Int, val valid: Boolean) : Token(), Literal

object TkKwFunction : Token(), Keyword
object TkKwElseif : Token(), Keyword
object TkKwRepeat : Token(), Keyword
object TkKwReturn : Token(), Keyword
object TkKwLocal : Token(), Keyword
object TkKwBreak : Token(), Keyword
object TkKwUntil : Token(), Keyword
object TkKwWhile : Token(), Keyword
object TkKwFalse : Token(), Keyword
object TkKwTrue : Token(), Keyword
object TkKwThen : Token(), Keyword
object TkKwElse : Token(), Keyword
object TkKwAnd : Token(), Keyword
object TkKwEnd : Token(), Keyword
object TkKwFor : Token(), Keyword
object TkKwNot : Token(), Keyword
object TkKwNil : Token(), Keyword
object TkKwIf : Token(), Keyword
object TkKwOr : Token(), Keyword
object TkKwDo : Token(), Keyword
object TkKwIn : Token(), Keyword
object TkKwGoto : Token(), Keyword {
    override val span = since52
}

data class TkComment(val contents: String, val finished: Boolean) : Token()
data class TkIdentifier(val name: String) : Token()
data class TkWhitespace(val contents: String) : Token()
data class TkLabel(val name: String, val length: Int, val valid: Boolean) : Token() {
    companion object {
        val dummy = TkLabel("<dummy>", 0, false)
    }
}

object TkOpAssign : Token(), Operator {
    override val operator = ("=")
}

object TkOpEqual : Token(), Operator {
    override val operator = ("==")
}

object TkOpNotEqual : Token(), Operator {
    override val operator = ("~=")
}

object TkOpLessOrEqual : Token(), Operator {
    override val operator = ("<=")
}

object TkOpLessThan : Token(), Operator {
    override val operator = ("<")
}

object TkOpGreaterOrEqual : Token(), Operator {
    override val operator = (">=")
}

object TkOpGreaterThan : Token(), Operator {
    override val operator = (">")
}

object TkOpConcat : Token(), Operator {
    override val operator = ("..")
}

object TkOpAdd : Token(), Operator {
    override val operator = ("+")
}

object TkOpSub : Token(), Operator {
    override val operator = ("-")
}

object TkOpMul : Token(), Operator {
    override val operator = ("*")
}

object TkOpDiv : Token(), Operator {
    override val operator = ("/")
}

object TkOpMod : Token(), Operator {
    override val operator = ("%")
}

object TkOpPow : Token(), Operator {
    override val operator = ("^")
}

object TkOpLen : Token(), Operator {
    override val operator = ("#")
}

object TkOpBitAnd : Token(), Operator {
    override val operator = ("&")
    override val span = since53
}

object TkOpBitOr : Token(), Operator {
    override val operator = ("|")
    override val span = since53
}

object TkOpBitRshift : Token(), Operator {
    override val operator = (">>")
    override val span = since53
}

object TkOpBitLshift : Token(), Operator {
    override val operator = ("<<")
    override val span = since53
}

object TkOpTilde : Token(), Operator {
    override val operator = ("~")
    override val span = since53
}

object TkOpenParen : Token(), SingleCharacter
object TkCloseParen : Token(), SingleCharacter
object TkOpenBrace : Token(), SingleCharacter
object TkCloseBrace : Token(), SingleCharacter
object TkOpenBracket : Token(), SingleCharacter
object TkCloseBracket : Token(), SingleCharacter

object TkDot : Token(), SingleCharacter
object TkComma : Token(), SingleCharacter
object TkColon : Token(), SingleCharacter
object TkSemicolon : Token(), SingleCharacter

object TkEof : Token()
