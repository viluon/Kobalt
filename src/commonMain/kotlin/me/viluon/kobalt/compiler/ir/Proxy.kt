package me.viluon.kobalt.compiler.ir

import me.viluon.kobalt.extensions.Nat
import me.viluon.kobalt.extensions.S
import me.viluon.kobalt.extensions.Z
import me.viluon.kobalt.extensions.text.TableCell
import me.viluon.kobalt.extensions.text.TabularCell
import me.viluon.kobalt.extensions.text.Text

/**
 *  Px guards builder boundaries.
 */
inline class Px<T : LuaType, n : Nat>(private val proxy: Proxy<T, n>) {
    infix fun extract(builder: InnerBlockBuilder<*>): Proxy<T, n> =
        if (proxy.builder == builder) proxy
        else throw IllegalArgumentException(
            "${proxy.v.id.name}_${proxy.usages}: ${proxy.v.type.name} is not in scope here. " +
                    "A proxy can only be used from its declaring builder."
        )
}

class Proxy<T : LuaType, n : Nat>(
    val builder: InnerBlockBuilder<*>,
    val v: Variable<T>,
    val usages: n
) : TabularCell {
    companion object {
        @Suppress("NOTHING_TO_INLINE")
        inline operator fun <T : LuaType> invoke(builder: InnerBlockBuilder<*>, v: Variable<T>): Proxy<T, Z> {
            return Proxy(builder, v, Z)
        }
    }

    // TODO room for optimisation
    val next: Proxy<T, S<n>> by lazy { Proxy(builder, v, S(usages)) }

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
