package me.viluon.kobalt.compiler.ir

import me.viluon.kobalt.extensions.text.None
import me.viluon.kobalt.extensions.text.Pretty
import me.viluon.kobalt.extensions.text.Text

sealed class Constant<T : LuaConstantType>(val type: T) : Pretty

data class ConstI(val n: Long) : Constant<TyInteger>(TyInteger) {
    override fun pretty(): Text = Text() + None + n.toString()
}
data class ConstF(val n: Double) : Constant<TyDouble>(TyDouble) {
    override fun pretty(): Text = Text() + None + n.toString()
}
