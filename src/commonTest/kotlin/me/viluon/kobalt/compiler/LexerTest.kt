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
        assertEquals(TkKwLocal, lexer.lex())
        assertEquals(space, lexer.lex())
        assertEquals(TkIdentifier("x"), lexer.lex())
        assertEquals(space, lexer.lex())
        assertEquals(TkOpAssign, lexer.lex())
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
        assertEquals(TkKwIf, lexer.lex())
        lexer.lex()
        assertEquals(TkIdentifier("foo"), lexer.lex())
        assertEquals(space, lexer.lex())
        assertEquals(TkKwThen, lexer.lex())
        lexer.lex()
        assertEquals(TkIdentifier("bar"), lexer.lex())
        assertEquals(TkOpenParen, lexer.lex())
        assertEquals(TkCloseParen, lexer.lex())
        lexer.lex()
        assertEquals(TkKwEnd, lexer.lex())
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
            TkKwLocal,
            space,
            TkKwFunction,
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
            TkKwIf,
            space,
            TkIdentifier("true_a"),
            space,
            TkOpLessThan,
            space,
            TkIdentifier("for_b"),
            space,
            TkKwThen,
            TkWhitespace("\n        "),
            TkKwReturn,
            space,
            TkKwTrue,
            space,
            TkKwAnd,
            space,
            TkIdentifier("C"),
            TkWhitespace("\n    "),
            TkKwEnd,
            TkWhitespace("\n\n    "),
            TkKwReturn,
            space,
            TkIdentifier("foo"),
            TkOpenParen,
            TkIdentifier("for_b"),
            TkComma,
            space,
            TkIdentifier("true_a"),
            TkCloseParen,
            TkWhitespace("\n"),
            TkKwEnd,
            TkEof
        )

        assertEquals(expected, actual)
        assertEquals(source.length, actual.map { it.length }.sum())
    }
}