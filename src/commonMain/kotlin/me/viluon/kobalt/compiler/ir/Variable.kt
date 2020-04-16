package me.viluon.kobalt.compiler.ir

import me.viluon.kobalt.compiler.syntax.TkIdentifier

data class Renamed<T : LuaType>(val i: Short, val v: Variable<T>)
data class Variable<T : LuaType>(
    val id: TkIdentifier,
    val definedAt: Short,
    val type: T,
    var liveAt: Short = definedAt
) : Verifiable {
    override val invariants
        get() = define.invariant(definedAt <= liveAt) {
            "The liveness of a variable should follow its definition."
        }
}
