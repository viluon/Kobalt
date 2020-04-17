package me.viluon.kobalt.compiler.ir

import me.viluon.kobalt.extensions.Nat
import me.viluon.kobalt.extensions.S
import me.viluon.kobalt.extensions.Z
import me.viluon.kobalt.extensions.text.*

class Proxy<T : LuaType, n : Nat>(
    var builder: InnerBlockBuilder,
    val v: Variable<T>,
    private val usages: n
) : TabularCell {
    companion object {
        @Suppress("NOTHING_TO_INLINE")
        inline operator fun <T : LuaType> invoke(builder: InnerBlockBuilder, v: Variable<T>): Proxy<T, Z> {
            return Proxy(builder, v, Z)
        }
    }

    // TODO room for optimisation
    val next: Proxy<T, S<n>> by lazy { Proxy(builder, v, S(usages)).also { builder.proxies.add(it) } }

    override fun asCell(): TableCell = TableCell(Text() + v.id.name + "<sub>${usages.value}</sub>")

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

internal inline fun <reified A : LuaType, T : LuaType> Proxy<T, *>.castBinOp(
    f: (t: Proxy<A, *>, l: Proxy<A, *>, r: Proxy<A, *>) -> Instruction,
    left: Proxy<*, *>,
    right: Proxy<*, *>
): Instruction {
    v.type as A
    @Suppress("UNCHECKED_CAST")
    // TODO try to enforce use of next in the type signatures
    return f(next as Proxy<A, *>, left as Proxy<A, *>, right as Proxy<A, *>)
}

fun <n : Nat, m : Nat, k : Nat, T : LuaType> Proxy<T, n>.add(
    left: Proxy<T, S<m>>,
    right: Proxy<T, S<k>>
): Proxy<T, S<n>> {
    @Suppress("USELESS_CAST")
    builder emit (when (v.type as LuaType) {
        TyInteger -> castBinOp(::InstrAddI, left, right)
        TyDouble -> castBinOp(::InstrAddF, left, right)
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

typealias LoadKConstructor<A> = (Proxy<A, *>, Constant<A>) -> Instruction

internal inline fun <reified A : LuaConstantType, T : LuaConstantType> Proxy<T, *>.castLoad(
    f: (t: Proxy<A, *>, n: Constant<A>) -> Instruction,
    const: Constant<T>
): Instruction {
    v.type as A
    @Suppress("UNCHECKED_CAST")
    return f(next as Proxy<A, *>, const as Constant<A>)
}

infix fun <n : Nat, T : LuaConstantType> Proxy<T, n>.loadK(const: Constant<T>): Proxy<T, S<n>> {
    @Suppress("USELESS_CAST", "UNCHECKED_CAST")
    builder emit (when (v.type as LuaConstantType) {
        TyBoolean -> TODO()
        TyInteger -> castLoad(::InstrLoadI as LoadKConstructor<TyInteger>, const)
        TyDouble -> castLoad(::InstrLoadF as LoadKConstructor<TyDouble>, const)
        TyString -> TODO()
        TyNil -> TODO()
    })

    return next
}

fun <n : Nat, m : Nat, k : Nat> Proxy<TyInteger, n>.phiI(
    a: Proxy<TyInteger, S<m>>,
    b: Proxy<TyInteger, S<k>>
): Proxy<TyInteger, S<n>> {
    builder emit InstrPhiI(next as PI, a, b)
    return next
}

fun <n : Nat, m : Nat, k : Nat, j : Nat> Proxy<TyInteger, n>.phiI(
    a: Proxy<TyInteger, S<m>>,
    b: Proxy<TyInteger, S<k>>,
    c: Proxy<TyInteger, S<j>>
): Proxy<TyInteger, S<n>> {
    builder emit InstrPhiI(next as PI, a, b, c)
    return next
}

fun <n : Nat, m : Nat, k : Nat, j : Nat, l : Nat> Proxy<TyInteger, n>.phiI(
    a: Proxy<TyInteger, S<m>>,
    b: Proxy<TyInteger, S<k>>,
    c: Proxy<TyInteger, S<j>>,
    d: Proxy<TyInteger, S<l>>
): Proxy<TyInteger, S<n>> {
    builder emit InstrPhiI(next as PI, a, b, c, d)
    return next
}
