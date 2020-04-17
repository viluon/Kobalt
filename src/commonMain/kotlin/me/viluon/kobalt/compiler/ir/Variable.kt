package me.viluon.kobalt.compiler.ir

import me.viluon.kobalt.compiler.syntax.TkIdentifier
import me.viluon.kobalt.extensions.text.*

data class Variable<T : LuaType>(
    val id: TkIdentifier,
    val definedAt: Short,
    val type: T,
    var liveAt: Short = definedAt
) : Verifiable, TabularRow {
    override val invariants
        get() = define.invariant(definedAt <= liveAt) {
            "The liveness of a variable should follow its definition."
        }

    override fun asRow(): TableRow = TableRow(TableCell(Text() + id.name + ":"), TableCell(Text() + Cyan + type.name))
}
