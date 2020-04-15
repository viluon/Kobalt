package me.viluon.kobalt.compiler

import me.viluon.kobalt.compiler.syntax.*
import me.viluon.kobalt.extensions.isValidInsideIdentifier
import me.viluon.kobalt.standard.Version
import me.viluon.kobalt.standard.Versioned

class Lexer(private val source: Source, private val version: Version = Version.Lua51) {
    private var pos: Int = 0
    private val builder: StringBuilder = StringBuilder()

    fun lex(): Token {
        if (!hasNext()) return TkEof
        return when (val ch = advance()) {
            in 'a'..'z' -> lexLowercase(ch)
            '_', in 'A'..'Z' -> lexRestOfIdentifier(ch, peek())
            ' ', '\t', '\n', '\r' -> lexWhitespace(ch)
            in '0'..'9' -> parseNumber(ch)
            '.' -> TODO("doubles? table access, concatenation, varargs")
            ':' -> lexColon()
            ',' -> TkComma
            ';' -> TkSemicolon
            '-' -> lexCommentOrMinus()
            '"', '\'' -> lexShortString(ch)
            '+' -> TkOpAdd
            '*' -> TkOpMul
            '#' -> TkOpLen
            '%' -> TkOpMod
            '^' -> TkOpPow
            '&' -> TkOpBitAnd
            '|' -> TkOpBitOr
            '=', '<', '>', '~' -> lexOperator(ch)
            else -> lexParens(ch)
            // TODO there are illegal characters too, e.g. $, !, ?, @, \, `, ...
        }
    }

    private fun <T> consume(x: T): T {
        advance()
        return x
    }

    private fun lexColon(): Token = if (peek() == ':' && isEnabled(TkLabel.dummy)) {
        advance()
        when (peek()) {
            null -> TkLabel("", 2, false)
            else -> {
                val id = lexRestOfIdentifier(advance(), peek())
                when (peek()) {
                    ':' -> {
                        advance()
                        when (peek()) {
                            ':' -> consume(TkLabel(id.name, id.length + 4, true))
                            else -> TkLabel(id.name, id.length + 3, false)
                        }
                    }
                    else -> TkLabel(id.name, id.length + 2, false)
                }
            }
        }
    } else TkColon

    private fun lexShortString(delimiter: Char): TkStringLiteral {
        builder.clear()
        var len = 0

        do {
            val next = peek() ?: break
            builder.append(advance())
            len++
        } while (next != delimiter)

        return when (peek()) {
            delimiter -> TkStringLiteral(builder.substring(0, builder.length - 1), len - 1, true)
            null -> TkStringLiteral(builder.toString(), len, false)
            else -> throw IllegalStateException()
        }
    }

    private fun lexCommentOrMinus(): Token = when (peek()) {
        '-' -> {
            advance()
            when (val third = peek()) {
                '[' -> TODO("long comment")
                else -> {
                    builder.clear().append("--")
                    var n = third
                    while (n != null && n != '\n') {
                        builder.append(advance())
                        n = peek()
                    }
                    TkComment(builder.toString(), true)
                }
            }
        }
        else -> (TkOpSub)
    }

    private fun lexOperator(first: Char): Token {
        val next = peek()
        return when (first) {
            '=' -> when (next) {
                '=' -> consume(TkOpEqual)
                else -> TkOpAssign
            }
            '<' -> when (next) {
                '=' -> consume(TkOpLessOrEqual)
                '<' -> consume(TkOpBitLshift)
                else -> TkOpLessThan
            }
            '>' -> when (next) {
                '=' -> consume(TkOpGreaterOrEqual)
                '>' -> consume(TkOpBitRshift)
                else -> TkOpGreaterThan
            }
            '~' -> when (next) {
                '=' -> consume(TkOpNotEqual)
                else -> TkOpTilde
            }
            else -> throw IllegalArgumentException()
        }
    }

    // TODO doubles, different bases
    private fun parseNumber(first: Char): Token {
        var next = first
        var len = 0
        var n = 0L

        do {
            len++
            n = n * 10 + (next - '0')
            if (!hasNext()) break
            next = advance()
        } while (next in '0'..'9')

        return TkIntegerLiteral(n, len, true)
    }

    private fun lexParens(first: Char): Token = when (first) {
        '(' -> TkOpenParen
        ')' -> TkCloseParen
        '[' -> TkOpenBrace // TODO multiline strings
        ']' -> TkCloseBrace
        '{' -> TkOpenBracket
        '}' -> TkCloseBracket
        else -> throw IllegalStateException()
    }

    private fun lexRestOfIdentifierWithoutClearing(_next: Char?): TkIdentifier = when {
        _next == null || !_next.isValidInsideIdentifier() -> TkIdentifier(builder.toString())
        else -> {
            builder.append(advance())
            var next = peek()
            while (next != null && next.isValidInsideIdentifier()) {
                builder.append(advance())
                next = peek()
            }

            TkIdentifier(builder.toString())
        }
    }

    private fun lexRestOfIdentifier(first: Char, _next: Char?): TkIdentifier = when {
        _next == null || !_next.isValidInsideIdentifier() -> TkIdentifier(first.toString())
        else -> {
            builder.clear().append(first).append(advance())
            var next = peek()
            while (next != null && next.isValidInsideIdentifier()) {
                builder.append(advance())
                next = peek()
            }

            TkIdentifier(builder.toString())
        }
    }

    private fun lexLowercase(first: Char): Token {
        val next = peek()
        if (next == null) return lexRestOfIdentifier(first, next)

        builder.clear().append(first)
        return when (first) {
            'a' -> lex_a(next)
            'b' -> lexKeywordOrIdentifier(TkKwBreak, next)
            'd' -> lexKeywordOrIdentifier(TkKwDo, next)
            'e' -> when (next) {
                'n' -> lexKeywordOrIdentifier(TkKwEnd, next)
                'l' -> {
                    val tk = lexKeywordOrIdentifier(TkKwElse, next)
                    if (tk is TkIdentifier && tk.name == TkKwElseif.keyword) TkKwElseif
                    else tk
                }
                else -> lexRestOfIdentifierWithoutClearing(next)
            }
            'f' -> when (next) {
                'a' -> lexKeywordOrIdentifier(TkKwFalse, next)
                'o' -> lexKeywordOrIdentifier(TkKwFor, next)
                'u' -> lexKeywordOrIdentifier(TkKwFunction, next)
                else -> lexRestOfIdentifierWithoutClearing(next)
            }
            'g' -> lexKeywordOrIdentifier(TkKwGoto, next)
            'i' -> when (next) {
                'f' -> lexKeywordOrIdentifier(TkKwIf, next)
                'n' -> lexKeywordOrIdentifier(TkKwIn, next)
                else -> lexRestOfIdentifierWithoutClearing(next)
            }
            'l' -> lexKeywordOrIdentifier(TkKwLocal, next)
            'n' -> when (next) {
                'i' -> lexKeywordOrIdentifier(TkKwNil, next)
                'o' -> lexKeywordOrIdentifier(TkKwNot, next)
                else -> lexRestOfIdentifierWithoutClearing(next)
            }
            'o' -> lexKeywordOrIdentifier(TkKwOr, next)
            'r' -> when (next) {
                'e' -> {
                    builder.append(advance())
                    when (val third = peek()) {
                        'p' -> lexKeywordOrIdentifier(TkKwRepeat, third, 2)
                        't' -> lexKeywordOrIdentifier(TkKwReturn, third, 2)
                        else -> lexRestOfIdentifierWithoutClearing(third)
                    }
                }
                else -> lexRestOfIdentifierWithoutClearing(next)
            }
            't' -> when (next) {
                'h' -> lexKeywordOrIdentifier(TkKwThen, next)
                'r' -> lexKeywordOrIdentifier(TkKwTrue, next)
                else -> lexRestOfIdentifierWithoutClearing(next)
            }
            'u' -> lexKeywordOrIdentifier(TkKwUntil, next)
            'w' -> lexKeywordOrIdentifier(TkKwWhile, next)
            else -> lexRestOfIdentifierWithoutClearing(next)
        }
    }

    private fun <Tk> lexKeywordOrIdentifier(kw: Tk, next: Char, offset: Int = 1): Token where Tk : Token, Tk : Keyword {
        var i = 1 + offset
        if (next != kw.keyword[offset] || !isEnabled(kw)) return lexRestOfIdentifierWithoutClearing(next)
        builder.append(advance())

        while (hasNext() && i < kw.keyword.length) {
            val n = peek()
            if (kw.keyword[i] != n) return lexRestOfIdentifierWithoutClearing(n)
            builder.append(advance())
            i++
        }

        val n = peek()
        if (n?.isValidInsideIdentifier() == true) {
            return lexRestOfIdentifierWithoutClearing(n)
        }

        return kw
    }

    private fun lex_a(next: Char): Token {
        return if (next == 'n') {
            advance()
            val third = peek()
            if (third == 'd') {
                advance()
                val fourth = peek()
                if (fourth?.isValidInsideIdentifier() == true) {
                    // we have an identifier which starts with "and"
                    builder.append("nd")
                    lexRestOfIdentifierWithoutClearing(fourth)
                } else {
                    TkKwAnd
                }
            } else {
                // prefix "an"
                builder.append('n')
                lexRestOfIdentifierWithoutClearing(third)
            }
        } else {
            // prefix "a$next"
            lexRestOfIdentifierWithoutClearing(next)
        }
    }

    private fun lexWhitespace(first: Char): TkWhitespace {
        builder.clear().append(first)
        var next = peek()
        while (next == ' ' || next == '\t' || next == '\n' || next == '\r') {
            builder.append(advance())
            next = peek()
        }

        return TkWhitespace(builder.toString())
    }

    private fun hasNext(): Boolean = source.contents.size > pos
    private fun advance(): Char = source.contents[pos++]
    private fun peek(): Char? = if (!hasNext()) null else source.contents[pos]
    private fun isEnabled(feature: Versioned): Boolean = version in feature.span
}
