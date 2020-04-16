package me.viluon.kobalt.compiler.ir

import me.viluon.kobalt.compiler.syntax.TkIdentifier
import me.viluon.kobalt.extensions.Z

interface BlockBuilder {
    fun alloc(name: String): Proxy<TyUnknown, Z> = alloc(name, TyUnknown)
    fun alloc(id: TkIdentifier): Proxy<TyUnknown, Z> = alloc(id, TyUnknown)
    fun <T : LuaType> alloc(name: String, type: T): Proxy<T, Z> = alloc(TkIdentifier(name), type)

    fun <T : LuaType> alloc(id: TkIdentifier, type: T): Proxy<T, Z>
    fun <T : LuaType> ret(v: Proxy<T, *>): Terminator
    infix fun emit(instr: Instruction): BasicBlock
}

data class InnerBlockBuilder(private val block: InnerBlock) : BlockBuilder {
    private inline val instructions get() = block.instructions
    private inline val variables get() = block.variables
    private inline val predecessors get() = block.predecessors
    private inline val followers get() = block.followers

    override fun <T : LuaType> alloc(id: TkIdentifier, type: T): Proxy<T, Z> {
        val v = Variable(id, instructions.size.toShort(), type)
        variables.add(v)
        return Proxy(this, v)
    }

    override infix fun emit(instr: Instruction): BasicBlock {
        instructions.add(instr)

        // FIXME this isn't how terminators should behave
        return if (instr is Terminator) {
            val next = InnerBlock()
            followers.add(next)
            next.predecessors.add(block)
            next
        } else block
    }

    override fun <T : LuaType> ret(v: Proxy<T, *>): Terminator {
        val instr = Return1(v)
        instructions.add(instr)
        return instr
    }
}
