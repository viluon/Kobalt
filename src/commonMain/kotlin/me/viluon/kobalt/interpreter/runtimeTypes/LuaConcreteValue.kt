package me.viluon.kobalt.interpreter.runtimeTypes

import me.viluon.kobalt.compiler.ir.*

typealias LuaValue<T> = LuaConcreteValue<T>?
val LuaNil: LuaValue<TyNil> = null

sealed class LuaConcreteValue<T : LuaType>

object LuaTrue : LuaConcreteValue<TyBoolean>()
object LuaFalse : LuaConcreteValue<TyBoolean>()
data class LuaInteger(val n: Long) : LuaConcreteValue<TyInteger>()
data class LuaDouble(val n: Double) : LuaConcreteValue<TyDouble>()
data class LuaString(val string: String) : LuaConcreteValue<TyString>()
