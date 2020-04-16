package me.viluon.kobalt.compiler.ir

sealed class BasicBlock : Verifiable {
    open val variables: MutableList<Variable<*>> = ArrayList(4)
    open val instructions: MutableList<Instruction> = ArrayList(8)
    open val predecessors: MutableList<BasicBlock> = ArrayList(2)
    val followers: MutableList<BasicBlock> = ArrayList(2)

    override val verifiableChildren: Iterable<Verifiable>
        get() = variables + instructions + predecessors + followers

    override val invariants
        get() = define
            .invariant(instructions.isNotEmpty() && instructions.firstOrNull { it is Terminator } == instructions.last()) {
                "There should be exactly one terminator instruction in a block, and it should be the last one."
            }
            .invariant(predecessors.isNotEmpty()) {
                "There should be at least one predecessor."
            }
            .invariant(instructions.isNotEmpty()) {
                "The instruction list should not be empty."
            }
}

class InnerBlock : BasicBlock() {
    inline fun open(builder: BlockBuilder.() -> Terminator): InnerBlock {
        builder(InnerBlockBuilder(this))
        return this
    }
}

@Suppress("UNCHECKED_CAST")
class RootBlock : BasicBlock() {
    companion object {
        private val empty: MutableList<out Any> = ArrayList(0)
    }

    override val variables: MutableList<Variable<*>> = empty as MutableList<Variable<*>>
    override val instructions: MutableList<Instruction> = empty as MutableList<Instruction>
    override val predecessors: MutableList<BasicBlock> = empty as MutableList<BasicBlock>

    inline fun open(builder: BlockBuilder.() -> Terminator): RootBlock {
        val first = InnerBlock()
        this.followers.add(first)
        first.predecessors.add(this)

        first.open(builder)
        return this
    }

    override val invariants
        get() = define
            .invariant(empty.isEmpty()) {
                "The root block should have no variables, no instructions, and no predecessors."
            }
            .invariant(followers.size == 1) {
                "The root block should have exactly one follower."
            }
}
