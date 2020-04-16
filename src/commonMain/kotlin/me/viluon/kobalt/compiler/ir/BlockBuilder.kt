package me.viluon.kobalt.compiler.ir

import me.viluon.kobalt.compiler.syntax.TkIdentifier
import me.viluon.kobalt.extensions.Nat
import me.viluon.kobalt.extensions.S
import me.viluon.kobalt.extensions.Z

interface BlockBuilder {
    fun alloc(name: String): Proxy<TyUnknown, Z> = alloc(name, TyUnknown)
    fun alloc(id: TkIdentifier): Proxy<TyUnknown, Z> = alloc(id, TyUnknown)
    fun <T : LuaType> alloc(name: String, type: T): Proxy<T, Z> = alloc(TkIdentifier(name), type)

    fun <T : LuaType> alloc(id: TkIdentifier, type: T): Proxy<T, Z>
    fun const(n: Long): ConstI
    fun const(n: Double): ConstF
    fun <T : LuaType, n : Nat> ret(v: Proxy<T, S<n>>): Terminator
    fun jmp(target: BasicBlock): Terminator
    fun <n : Nat, m : Nat> eqI(
        left: Proxy<TyInteger, S<n>>,
        right: Proxy<TyInteger, S<m>>,
        targetEq: BasicBlock,
        targetNeq: BasicBlock
    ): Terminator

    infix fun emit(instr: Instruction): BasicBlock
    fun block(f: BlockBuilder.() -> Terminator): BasicBlock
    val self: BasicBlock
}

data class InnerBlockBuilder(private val block: InnerBlock) : BlockBuilder {
    internal val proxies: MutableList<Proxy<*, *>> = ArrayList()
    private inline val instructions get() = block.instructions
    private inline val constants get() = block.constants
    private inline val variables get() = block.variables
    private inline val predecessors get() = block.predecessors
    override val self get() = block

    private fun addFollower(follower: BasicBlock): BasicBlock {
        block.followers.add(follower)
        follower.predecessors.add(block)
        return follower
    }

    private fun <T : LuaType> addProxy(v: Variable<T>): Proxy<T, Z> = Proxy(this, v).also { proxies.add(it) }

    override fun <T : LuaType> alloc(id: TkIdentifier, type: T): Proxy<T, Z> {
        val v = Variable(id, instructions.size.toShort(), type)
        variables.add(v)
        return addProxy(v)
    }

    override infix fun emit(instr: Instruction): BasicBlock {
        instructions.add(instr)
        return block
    }

    override fun <T : LuaType, n : Nat> ret(v: Proxy<T, S<n>>): Terminator =
        InstrReturn1(v).also { instructions.add(it) }

    override fun jmp(target: BasicBlock): Terminator = InstrJump(target).also { instructions.add(it) }

    override fun <n : Nat, m : Nat> eqI(
        left: Proxy<TyInteger, S<n>>,
        right: Proxy<TyInteger, S<m>>,
        targetEq: BasicBlock,
        targetNeq: BasicBlock
    ): Terminator {
        val instr = InstrEqI(left, right, targetEq, targetNeq)
        instructions.add(instr)
        addFollower(targetEq)
        addFollower(targetNeq)
        return instr
    }

    override fun block(f: BlockBuilder.() -> Terminator): BasicBlock = InnerBlock().apply {
        open {
            this@InnerBlockBuilder.proxies.forEach { it.builder = this as InnerBlockBuilder }
            f()
        }
    }.also {
        proxies.forEach { it.builder = this }
    }

    override fun const(n: Double): ConstF = ConstF(n).also { constants.add(it) }
    override fun const(n: Long): ConstI = ConstI(n).also { constants.add(it) }
}
