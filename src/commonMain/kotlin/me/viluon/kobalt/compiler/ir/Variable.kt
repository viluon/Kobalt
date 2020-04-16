package me.viluon.kobalt.compiler.ir

import me.viluon.kobalt.compiler.syntax.TkIdentifier
import me.viluon.kobalt.extensions.text.Cyan
import me.viluon.kobalt.extensions.text.Pretty
import me.viluon.kobalt.extensions.text.Text

data class Renamed<T : LuaType>(val i: Short, val v: Variable<T>)
data class Variable<T : LuaType>(
    val id: TkIdentifier,
    val definedAt: Short,
    val type: T,
    var liveAt: Short = definedAt
) : Verifiable, Pretty {
    override val invariants
        get() = define.invariant(definedAt <= liveAt) {
            "The liveness of a variable should follow its definition."
        }

    override fun pretty(): Text = Text() + id.name + ": " + Cyan + type.name
}
