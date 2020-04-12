package me.viluon.kobalt.compiler.syntax

import me.viluon.kobalt.standard.VersionSpan
import me.viluon.kobalt.standard.VersionSpan.Companion.since53
import me.viluon.kobalt.standard.Versioned

enum class Operator(val str: String, override val span: VersionSpan = VersionSpan.permanent) : Versioned {
    OpAssign("="),

    OpEqual("=="),
    OpNotEqual("~="),
    OpLessOrEqual("<="),
    OpLessThan("<"),
    OpGreaterOrEqual(">="),
    OpGreaterThan(">"),

    OpConcat(".."),
    OpAdd("+"),
    OpSub("-"),
    OpMul("*"),
    OpDiv("/"),
    OpMod("%"),
    OpPow("^"),
    OpLen("#"),

    OpBitAnd("&", since53),
    OpBitOr("|", since53),
    OpBitRshift(">>", since53),
    OpBitLshift("<<", since53),
    OpTilde("~", since53),
    ;

    inline val length get() = str.length
}