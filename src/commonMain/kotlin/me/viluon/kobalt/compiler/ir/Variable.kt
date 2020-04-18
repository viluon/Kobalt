package me.viluon.kobalt.compiler.ir

import me.viluon.kobalt.compiler.syntax.TkIdentifier
import me.viluon.kobalt.extensions.text.*

data class Variable<T : LuaType>(
    val id: TkIdentifier,
    val definedAt: Short,
    val type: T,
    var liveAt: Short = definedAt
) : Verifiable, TabularRow {
    companion object {
        fun arg(name: String): Variable<TyUnknown> = arg(name, TyUnknown)

        fun <T : LuaType> arg(name: String, type: T): Variable<T> {
            return Variable(TkIdentifier(name), 0, type)
        }
    }

    override val invariants
        get() = define.invariant(definedAt <= liveAt) {
            "The liveness of a variable should follow its definition."
        }

    override fun asRow(): TableRow = TableRow(TableCell(Text() + id.name + ":"), TableCell(Text() + Cyan + type.name))
}
