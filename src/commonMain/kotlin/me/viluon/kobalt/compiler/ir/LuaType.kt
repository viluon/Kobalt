package me.viluon.kobalt.compiler.ir

sealed class LuaType {
}

object TyCoroutine : LuaType()
object TyFunction : LuaType()
object TyUnknown : LuaType()
object TyBoolean : LuaType()
object TyInteger : LuaType()
object TyDouble : LuaType()
object TyString : LuaType()
object TyTable : LuaType()
object TyNil : LuaType()
