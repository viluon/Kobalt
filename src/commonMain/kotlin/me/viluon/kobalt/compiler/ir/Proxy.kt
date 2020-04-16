package me.viluon.kobalt.compiler.ir

import me.viluon.kobalt.extensions.Nat
import me.viluon.kobalt.extensions.S
import me.viluon.kobalt.extensions.Z

class Proxy<T : LuaType, n : Nat>(
    val builder: InnerBlockBuilder,
    val v: Variable<T>,
    private val usages: n
) {
    companion object {
        @Suppress("NOTHING_TO_INLINE")
        inline operator fun <T : LuaType> invoke(builder: InnerBlockBuilder, v: Variable<T>): Proxy<T, Z> {
            return Proxy(builder, v, Z)
        }
    }

    // TODO room for optimisation
    val next: Proxy<T, S<n>> by lazy { Proxy(builder, v, S(usages)) }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as Proxy<*, *>
        return v == other.v && usages == other.usages
    }

    override fun hashCode(): Int {
        return 31 * v.hashCode() + usages.value
    }
}

inline fun <reified A : LuaType, T : LuaType> Proxy<T, *>.cast(
    f: (t: Proxy<A, *>, l: Proxy<A, *>, r: Proxy<A, *>) -> Instruction,
    left: Proxy<*, *>,
    right: Proxy<*, *>
): Instruction {
    v.type as A
    @Suppress("UNCHECKED_CAST")
    return f(next as Proxy<A, *>, left as Proxy<A, *>, right as Proxy<A, *>)
}

fun <n : Nat, m : Nat, k : Nat, T : LuaType> Proxy<T, n>.add(
    left: Proxy<T, m>,
    right: Proxy<T, k>
): Proxy<T, S<n>> {
    @Suppress("USELESS_CAST")
    builder emit (when (v.type as LuaType) {
        TyInteger -> cast(::InstrAddI, left, right)
        TyDouble -> cast(::InstrAddF, left, right)
        TyString -> TODO("adding strings should parse them <5.4")
        TyCoroutine -> TODO()
        TyFunction -> TODO()
        TyUnknown -> TODO()
        TyBoolean -> TODO()
        TyTable -> TODO()
        TyNil -> TODO()
    })

    return next
}
