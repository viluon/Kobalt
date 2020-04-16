package me.viluon.kobalt.compiler.ir

/**
 * A terminator instruction introduces a branch or returns from a function,
 * and thus can only occur as the last instruction in a basic block.
 */
interface Terminator
sealed class Instruction : Verifiable

data class Return1<T : LuaType>(val v: Proxy<T, *>) : Instruction(), Terminator {
    override val invariants = none
}

sealed class BinaryInstruction<T : LuaType>(
    val target: Proxy<T, *>,
    val left: Proxy<T, *>,
    val right: Proxy<T, *>
) : Instruction() {
    override val invariants
        get() = define.invariant(target != left && target != right) {
            "The target variable should not be either of the source variables."
        }
}

typealias PI = Proxy<TyInteger, *>
typealias PF = Proxy<TyDouble, *>

class InstrAddI(target: PI, left: PI, right: PI) : BinaryInstruction<TyInteger>(target, left, right)
class InstrAddF(target: PF, left: PF, right: PF) : BinaryInstruction<TyDouble>(target, left, right)
