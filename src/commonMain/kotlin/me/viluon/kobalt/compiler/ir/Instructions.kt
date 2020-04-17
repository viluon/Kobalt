package me.viluon.kobalt.compiler.ir

import me.viluon.kobalt.extensions.text.*

typealias PI = Proxy<TyInteger, *>
typealias PF = Proxy<TyDouble, *>

interface Phi
sealed class Instruction : Verifiable, TabularRow {
    protected val instructionName: String = this::class.simpleName!!.substring(5)
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

    override fun asRow(): TableRow = TableRow(
        TableCell(Text() + Blue + instructionName),
        target.asCell(),
        left.asCell(),
        right.asCell()
    )
}

sealed class Load<T : LuaConstantType>(
    val target: Proxy<T, *>,
    val const: Constant<T>
) : Instruction() {
    override fun asRow(): TableRow = TableRow(
        TableCell(Text() + Blue + instructionName),
        target.asCell(),
        const.asCell()
    )
}

/**
 * A terminator instruction introduces a branch or returns from a function,
 * and thus can only occur as the last instruction in a basic block.
 */
sealed class Terminator : Instruction() {
    override fun asRow(): TableRow = TableRow(TableCell(Text() + Yellow + instructionName))
}

data class InstrReturn1<T : LuaType>(val v: Proxy<T, *>) : Terminator() {
    override val invariants = none
    override fun asRow(): TableRow = super.asRow() + v.asCell()
}

data class InstrJump<P : BlockParams, Sg : BlockSignature>(val params: P, val target: BasicBlock<Sg>) : Terminator() {
    override val invariants = none
    override fun asRow(): TableRow = super.asRow() + TableCell(Text() + Magenta + "#${target.id}", target.id)
}

data class InstrEqI<EqP : BlockParams, NeqP : BlockParams, EqSg : BlockSignature, NeqSg : BlockSignature>(
    val left: PI,
    val right: PI,
    val eqParams: EqP,
    val neqParams: NeqP,
    val targetEq: BasicBlock<EqSg>,
    val targetNeq: BasicBlock<NeqSg>
) : Terminator() {
    override val invariants get() = none

    override fun asRow(): TableRow = super.asRow() +
            left.asCell() +
            right.asCell() +
            TableCell(Text() + Magenta + "#${targetEq.id} ", targetEq.id) +
            TableCell(Text() + Magenta + "#${targetNeq.id} ", targetNeq.id)
}

class InstrAddI(target: PI, left: PI, right: PI) : BinaryInstruction<TyInteger>(target, left, right)
class InstrAddF(target: PF, left: PF, right: PF) : BinaryInstruction<TyDouble>(target, left, right)

class InstrLoadI(target: PI, const: ConstI) : Load<TyInteger>(target, const) {
    override val invariants = none
}

class InstrLoadF(target: PF, const: ConstF) : Load<TyDouble>(target, const) {
    override val invariants = none
}

data class InstrPhiI(val target: PI, val arguments: List<PI>) : Instruction(), Phi {
    constructor(target: PI, vararg args: PI) : this(target, args.asList())

    override val invariants
        get() = define
            .invariant(arguments.none { it == target }) {
                "No variable can be assigned to itself."
            }

    override fun asRow(): TableRow = TableRow(listOf(
        TableCell(Text() + Green + instructionName),
        target.asCell()
    ) + arguments.map { it.asCell() })
}
