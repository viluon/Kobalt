package me.viluon.kobalt.compiler.ir

sealed class LuaType {
    inline val name: String get() = this::class.simpleName!!.substring(2)
}

sealed class LuaConstantType : LuaType()

object TyBoolean : LuaConstantType()
object TyInteger : LuaConstantType()
object TyDouble : LuaConstantType()
object TyString : LuaConstantType()
object TyNil : LuaConstantType()

object TyCoroutine : LuaType()
object TyFunction : LuaType()
object TyUnknown : LuaType()
object TyTable : LuaType()
