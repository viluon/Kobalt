package me.viluon.kobalt.compiler.ir

import me.viluon.kobalt.compiler.syntax.TkIdentifier
import me.viluon.kobalt.extensions.Nat
import me.viluon.kobalt.extensions.S
import me.viluon.kobalt.extensions.Z

data class InnerBlockBuilder(private val block: InnerBlock) {
    val proxies: MutableList<Proxy<*, *>> = ArrayList()
    private inline val instructions get() = block.instructions
    private inline val constants get() = block.constants
    private inline val variables get() = block.variables
    private inline val predecessors get() = block.predecessors
    val self get() = block

    private fun addFollower(follower: BasicBlock): BasicBlock {
        block.followers.add(follower)
        follower.predecessors.add(block)
        return follower
    }

    private fun <T : LuaType> addProxy(v: Variable<T>): Proxy<T, Z> = Proxy(this, v).also { proxies.add(it) }

    fun <T : LuaType> alloc(id: TkIdentifier, type: T): Proxy<T, Z> {
        val v = Variable(id, instructions.size.toShort(), type)
        variables.add(v)
        return addProxy(v)
    }

    infix fun emit(instr: Instruction): BasicBlock {
        instructions.add(instr)
        return block
    }

    fun <T : LuaType, n : Nat> ret(v: Proxy<T, S<n>>): Terminator =
        InstrReturn1(v).also { instructions.add(it) }

    fun jmp(target: BasicBlock): Terminator = InstrJump(target).also {
        instructions.add(it)
        addFollower(target)
    }

    fun <n : Nat, m : Nat> eqI(
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

    // FIXME there's a possibility of information loss when new proxies emerge in the nested block, since
    //  they're added to a different list and not copied back
    inline fun block(f: InnerBlockBuilder.() -> Terminator): BasicBlock = InnerBlock().apply {
        open {
            this@InnerBlockBuilder.proxies.forEach { it.builder = this }
            f()
        }
    }.also {
        proxies.forEach { it.builder = this }
    }

    fun const(n: Long): ConstI = ConstI(n).also { constants.add(it) }
    fun const(n: Double): ConstF = ConstF(n).also { constants.add(it) }
    fun alloc(name: String): Proxy<TyUnknown, Z> = alloc(name, TyUnknown)
    fun alloc(id: TkIdentifier): Proxy<TyUnknown, Z> = alloc(id, TyUnknown)
    fun <T : LuaType> alloc(name: String, type: T): Proxy<T, Z> = alloc(TkIdentifier(name), type)
}
