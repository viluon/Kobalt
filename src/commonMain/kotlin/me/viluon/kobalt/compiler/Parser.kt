package me.viluon.kobalt.compiler

import me.viluon.kobalt.compiler.syntax.*

// TODO opening paren on a new line needs special care
class Parser(private val tokens: List<Token>) {
    private val state: ParserState = ParserState()
    var pos = 0

    fun parse(): Any? {
        if (!hasNext()) return null

        when (val token = advance()) {
            is TkStringLiteral -> state.stringLiterals.add(token.str.toCharArray())
            is TkIntegerLiteral -> state.integerLiterals.add(token.n)
            is TkDoubleLiteral -> state.doubleLiterals.add(token.n)
            TkKwFunction -> TODO()
            TkKwElseif -> TODO()
            TkKwRepeat -> TODO()
            TkKwReturn -> TODO()
            TkKwLocal -> TODO()
            TkKwBreak -> TODO()
            TkKwUntil -> TODO()
            TkKwWhile -> TODO()
            TkKwFalse -> TODO()
            TkKwTrue -> TODO()
            TkKwThen -> TODO()
            TkKwElse -> TODO()
            TkKwAnd -> TODO()
            TkKwEnd -> TODO()
            TkKwFor -> TODO()
            TkKwNot -> TODO()
            TkKwNil -> TODO()
            TkKwIf -> TODO()
            TkKwOr -> TODO()
            TkKwDo -> TODO()
            TkKwIn -> TODO()
            TkKwGoto -> TODO()
            is TkComment -> TODO()
            is TkIdentifier -> TODO()
            is TkWhitespace -> TODO()
            is TkLabel -> TODO()
            TkOpAssign -> TODO()
            TkOpEqual -> TODO()
            TkOpNotEqual -> TODO()
            TkOpLessOrEqual -> TODO()
            TkOpLessThan -> TODO()
            TkOpGreaterOrEqual -> TODO()
            TkOpGreaterThan -> TODO()
            TkOpConcat -> TODO()
            TkOpAdd -> TODO()
            TkOpSub -> TODO()
            TkOpMul -> TODO()
            TkOpDiv -> TODO()
            TkOpMod -> TODO()
            TkOpPow -> TODO()
            TkOpLen -> TODO()
            TkOpBitAnd -> TODO()
            TkOpBitOr -> TODO()
            TkOpBitRshift -> TODO()
            TkOpBitLshift -> TODO()
            TkOpTilde -> TODO()
            TkOpenParen -> TODO()
            TkCloseParen -> TODO()
            TkOpenBrace -> TODO()
            TkCloseBrace -> TODO()
            TkOpenBracket -> TODO()
            TkCloseBracket -> TODO()
            TkDot -> TODO()
            TkComma -> TODO()
            TkColon -> TODO()
            TkSemicolon -> TODO()
            TkEof -> TODO()
        }

        TODO()
    }

    private fun advance(): Token = tokens[pos++]
    private fun hasNext(): Boolean = pos < tokens.size
    private fun peek(): Token? = if (hasNext()) tokens[pos] else null
}
