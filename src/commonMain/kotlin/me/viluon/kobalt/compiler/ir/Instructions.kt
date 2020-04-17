package me.viluon.kobalt.compiler.ir

import me.viluon.kobalt.extensions.text.*

typealias PI = Proxy<TyInteger, *>
typealias PF = Proxy<TyDouble, *>

interface Phi
sealed class Instruction : Verifiable, Pretty, PrettyRowData {
    protected val instructionName: String = this::class.simpleName!!.substring(5)
}

/**
 * A terminator instruction introduces a branch or returns from a function,
 * and thus can only occur as the last instruction in a basic block.
 */
sealed class Terminator : Instruction() {
    override fun pretty(): Text = Text() + Yellow + instructionName + " "
}

data class InstrReturn1<T : LuaType>(val v: Proxy<T, *>) : Terminator() {
    override val invariants = none
    override fun pretty(): Text = super.pretty() + v.pretty()
    override fun asRow(): TableRow = TableRow(listOf(TableData(super.pretty()), v.asCell()))
}

data class InstrJump(val target: BasicBlock) : Terminator() {
    override val invariants = none
    override fun pretty(): Text = super.pretty() + Magenta + "#${target.id}"
    override fun asRow(): TableRow =
        TableRow(TableData(super.pretty()), TableData(Text() + Magenta + "#${target.id}", listOf(target.id)))
}

data class InstrEqI(val left: PI, val right: PI, val targetEq: BasicBlock, val targetNeq: BasicBlock) : Terminator() {
    override val invariants get() = none
    override fun pretty(): Text =
        super.pretty() + left.pretty() + " " + right.pretty() + Magenta + " #${targetEq.id} #${targetNeq.id}"

    override fun asRow(): TableRow = TableRow(
        TableData(super.pretty()),
        left.asCell(),
        right.asCell(),
        TableData(Text() + Magenta + "#${targetEq.id} ", listOf(targetEq.id)),
        TableData(Text() + Magenta + "#${targetNeq.id} ", listOf(targetNeq.id))
    )
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

    override fun pretty(): Text = Text() +
            Blue + instructionName +
            " " + target.pretty() +
            " " + left.pretty() +
            " " + right.pretty()

    override fun asRow(): TableRow = TableRow(
        TableData(Text() + Blue + instructionName),
        target.asCell(),
        left.asCell(),
        right.asCell()
    )
}

class InstrAddI(target: PI, left: PI, right: PI) : BinaryInstruction<TyInteger>(target, left, right)
class InstrAddF(target: PF, left: PF, right: PF) : BinaryInstruction<TyDouble>(target, left, right)

data class InstrLoadI(val target: PI, val n: ConstI) : Instruction() {
    override val invariants = none
    override fun pretty(): Text = Text() + Blue + instructionName + " " + target.pretty() + " " + n.pretty()
    override fun asRow(): TableRow = TableRow(
        TableData(Text() + Blue + instructionName),
        target.asCell(),
        n.asCell()
    )
}

data class InstrLoadF(val target: PF, val n: ConstF) : Instruction() {
    override val invariants = none
    override fun pretty(): Text = Text() + Blue + instructionName + " " + target.pretty() + " " + n.pretty()
}

data class InstrPhiI(val target: PI, val arguments: List<PI>) : Instruction(), Phi {
    constructor(target: PI, vararg args: PI) : this(target, args.asList())

    override val invariants
        get() = define
            .invariant(arguments.none { it == target }) {
                "No variable can be assigned to itself."
            }

    override fun pretty(): Text =
        arguments.fold(Text() + Green + instructionName + " " + target.pretty()) { acc, arg -> acc + " " + arg.pretty() }

    override fun asRow(): TableRow = TableRow(listOf(
        TableData(Text() + Green + instructionName),
        target.asCell()
    ) + arguments.map { it.asCell() })
}
