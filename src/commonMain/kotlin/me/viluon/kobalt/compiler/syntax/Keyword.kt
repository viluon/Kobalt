package me.viluon.kobalt.compiler.syntax

import me.viluon.kobalt.standard.VersionSpan
import me.viluon.kobalt.standard.Versioned

enum class Keyword(kw: String? = null, override val span: VersionSpan = VersionSpan.permanent) : Versioned {
    KwFunction,
    KwElseif,
    KwRepeat,
    KwReturn,
    KwLocal,
    KwBreak,
    KwUntil,
    KwWhile,
    KwFalse,
    KwTrue,
    KwThen,
    KwElse,
    KwAnd,
    KwEnd,
    KwFor,
    KwNot,
    KwNil,
    KwIf,
    KwOr,
    KwDo,
    KwIn,

    KwGoto(span = VersionSpan.since52),
    ;

    val keyword: String = kw ?: name.substring(2).toLowerCase()
}

