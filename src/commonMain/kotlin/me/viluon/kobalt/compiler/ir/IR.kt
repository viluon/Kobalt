package me.viluon.kobalt.compiler.ir

import me.viluon.kobalt.compiler.syntax.TkIdentifier

data class Renamed<T : LuaType>(val i: Short, val v: Variable<T>)

data class Variable<T : LuaType>(
    val id: TkIdentifier,
    val definedAt: Short,
    val type: T,
    var liveAt: Short = definedAt
) : Verifiable {
    override val invariants
        get() = define.invariant(definedAt <= liveAt) {
            "The liveness of a variable must follow its definition."
        }
}

sealed class BasicBlock : Verifiable {
    open val variables: MutableList<Variable<*>> = ArrayList(4)
    open val instructions: MutableList<Instruction> = ArrayList(8)
    open val predecessors: MutableList<BasicBlock> = ArrayList(2)
    val followers: MutableList<BasicBlock> = ArrayList(2)

    fun <T : LuaType> alloc(id: TkIdentifier, at: Short, type: T): Variable<T> {
        val v = Variable(id, at, type)
        variables.add(v)
        return v
    }

    open fun emit(instr: Instruction): BasicBlock {
        instructions.add(instr)

        // FIXME this isn't how terminators should behave
        return if (instr is Terminator) {
            val next = InnerBlock()
            followers.add(next)
            next.predecessors.add(this)
            next
        } else this
    }

    override val verifiableChildren: Iterable<Verifiable>
        get() = variables + instructions + predecessors + followers

    override val invariants
        get() = define
            .invariant(instructions.firstOrNull { it is Terminator } == instructions.lastOrNull()) {
                "There must be exactly one terminator instruction in a non-empty block, and it must be the last one."
            }
            .invariant(variables.size <= instructions.size) {
                "The number of variables must not exceed the number of instructions."
            }
}

class InnerBlock : BasicBlock()

@Suppress("UNCHECKED_CAST")
class RootBlock : BasicBlock() {
    companion object {
        private val empty: MutableList<out Any> = ArrayList(0)
    }

    override val variables: MutableList<Variable<*>> = empty as MutableList<Variable<*>>
    override val instructions: MutableList<Instruction> = empty as MutableList<Instruction>
    override val predecessors: MutableList<BasicBlock> = empty as MutableList<BasicBlock>

    override fun emit(instr: Instruction): BasicBlock {
        val next = InnerBlock()
        followers.add(next)
        return next.emit(instr)
    }
}
