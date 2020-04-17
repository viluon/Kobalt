package me.viluon.kobalt.compiler.ir

import me.viluon.kobalt.extensions.text.*

sealed class Constant<T : LuaConstantType>(val type: T) : Pretty, TableCell

data class ConstI(val n: Long) : Constant<TyInteger>(TyInteger) {
    override fun pretty(): Text = Text() + None + n.toString()
    override fun asCell(): TableData = TableData(pretty())
}
data class ConstF(val n: Double) : Constant<TyDouble>(TyDouble) {
    override fun pretty(): Text = Text() + None + n.toString()
    override fun asCell(): TableData = TableData(pretty())
}
