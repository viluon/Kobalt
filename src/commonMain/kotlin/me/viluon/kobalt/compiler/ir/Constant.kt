package me.viluon.kobalt.compiler.ir

import me.viluon.kobalt.extensions.text.*

sealed class Constant<T : LuaConstantType>(val type: T) : TabularCell

data class ConstI(val n: Long) : Constant<TyInteger>(TyInteger) {
    override fun asCell(): TableCell = TableCell(Text() + None + n.toString())
}
data class ConstF(val n: Double) : Constant<TyDouble>(TyDouble) {
    override fun asCell(): TableCell = TableCell(Text() + None + n.toString())
}
