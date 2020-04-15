package me.viluon.kobalt.compiler.ir

/**
 * A terminator instruction introduces a branch or returns from a function,
 * and thus can only occur as the last instruction in a basic block.
 */
interface Terminator
sealed class Instruction : Verifiable

data class InstrAddI(
    val target: Renamed<TyInteger>,
    val left: Renamed<TyInteger>,
    val right: Renamed<TyInteger>
) : Instruction() {
    override val invariants
        get() = define.invariant(target != left && target != right) {
            "The target variable must not be either of the source variables."
        }
}
