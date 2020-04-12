package me.viluon.kobalt.compiler

import me.viluon.kobalt.compiler.syntax.*
import kotlin.test.Test
import kotlin.test.assertEquals

class LexerTest {
    private fun lexAll(str: String): List<Token> {
        val lex = Lexer(Source.fromString("<stdin>", str))
        val result = ArrayList<Token>()

        do {
            val tk = lex.lex()
            result.add(tk)
        } while (tk != TkEof)

        return result
    }

    private val space = TkWhitespace(" ")

    @Test
    fun simpleInput() {
        var lexer = Lexer(Source.fromString("<stdin>", "local x = 3"))
        assertEquals(TkKeyword(Keyword.KwLocal), lexer.lex())
        assertEquals(space, lexer.lex())
        assertEquals(TkIdentifier("x"), lexer.lex())
        assertEquals(space, lexer.lex())
        assertEquals(TkOperator(Operator.OpAssign), lexer.lex())
        assertEquals(space, lexer.lex())
        assertEquals(TkIntegerLiteral(3L, 1, true), lexer.lex())

        lexer = Lexer(
            Source.fromString(
                "<stdin>", """
            if foo then
                bar()
            end
        """.trimIndent()
            )
        )
        assertEquals(TkKeyword(Keyword.KwIf), lexer.lex())
        lexer.lex()
        assertEquals(TkIdentifier("foo"), lexer.lex())
        assertEquals(space, lexer.lex())
        assertEquals(TkKeyword(Keyword.KwThen), lexer.lex())
        lexer.lex()
        assertEquals(TkIdentifier("bar"), lexer.lex())
        assertEquals(TkOpenParen, lexer.lex())
        assertEquals(TkCloseParen, lexer.lex())
        lexer.lex()
        assertEquals(TkKeyword(Keyword.KwEnd), lexer.lex())
    }

    @Test
    fun emptyInput() {
        assertEquals(listOf(TkEof), lexAll(""))
    }

    @Test
    fun functionDefinition() {
        val source = """
            -- my function
            local function foo(true_a, for_b, C)
                if true_a < for_b then
                    return true and C
                end

                return foo(for_b, true_a)
            end
        """.trimIndent()

        val actual = lexAll(source)
        val expected = listOf(
            TkComment("-- my function", true),
            TkWhitespace("\n"),
            TkKeyword(Keyword.KwLocal),
            space,
            TkKeyword(Keyword.KwFunction),
            space,
            TkIdentifier("foo"),
            TkOpenParen,
            TkIdentifier("true_a"),
            TkComma,
            space,
            TkIdentifier("for_b"),
            TkComma,
            space,
            TkIdentifier("C"),
            TkCloseParen,
            TkWhitespace("\n    "),
            TkKeyword(Keyword.KwIf),
            space,
            TkIdentifier("true_a"),
            space,
            TkOperator(Operator.OpLessThan),
            space,
            TkIdentifier("for_b"),
            space,
            TkKeyword(Keyword.KwThen),
            TkWhitespace("\n        "),
            TkKeyword(Keyword.KwReturn),
            space,
            TkKeyword(Keyword.KwTrue),
            space,
            TkKeyword(Keyword.KwAnd),
            space,
            TkIdentifier("C"),
            TkWhitespace("\n    "),
            TkKeyword(Keyword.KwEnd),
            TkWhitespace("\n\n    "),
            TkKeyword(Keyword.KwReturn),
            space,
            TkIdentifier("foo"),
            TkOpenParen,
            TkIdentifier("for_b"),
            TkComma,
            space,
            TkIdentifier("true_a"),
            TkCloseParen,
            TkWhitespace("\n"),
            TkKeyword(Keyword.KwEnd),
            TkEof
        )

        assertEquals(expected, actual)
        assertEquals(source.length, actual.map { it.length }.sum())
    }
}