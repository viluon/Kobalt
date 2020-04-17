package me.viluon.kobalt.compiler.ir

import me.viluon.kobalt.compiler.syntax.TkIdentifier
import me.viluon.kobalt.extensions.*

data class InnerBlockBuilder<out Sg : BlockSignature>(private val block: InnerBlock<Sg>) {
    val proxies: MutableList<Proxy<*, *>> = ArrayList()
    private inline val instructions get() = block.instructions
    private inline val constants get() = block.constants
    private inline val variables get() = block.variables
    private inline val predecessors get() = block.predecessors
    val self get() = block

    private fun addFollower(follower: BasicBlock<*>): BasicBlock<*> {
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

    infix fun emit(instr: Instruction): BasicBlock<Sg> {
        instructions.add(instr)
        return block
    }

    fun <T : LuaType, n : Nat> ret(v: Proxy<T, S<n>>): Terminator =
        InstrReturn1(v).also { instructions.add(it) }

    fun <Params : BlockParams, Sign : BlockSignature> jmp(
        cert: TypeValidationCertificate<Sign, Params>,
        target: BasicBlock<Sign>
    ): Terminator =
        InstrJump(cert.params, target).also {
            instructions.add(it)
            addFollower(target)
        }

    fun <n : Nat, m : Nat, EqP : BlockParams, NeqP : BlockParams, EqSg : BlockSignature, NeqSg : BlockSignature> eqI(
        left: Proxy<TyInteger, S<n>>,
        right: Proxy<TyInteger, S<m>>,
        eqCert: TypeValidationCertificate<EqSg, EqP>,
        neqCert: TypeValidationCertificate<NeqSg, NeqP>,
        targetEq: BasicBlock<EqSg>,
        targetNeq: BasicBlock<NeqSg>
    ): Terminator = InstrEqI(left, right, eqCert.params, neqCert.params, targetEq, targetNeq).also {
        instructions.add(it)
        addFollower(targetEq)
        addFollower(targetNeq)
    }

    // FIXME there's a possibility of information loss when new proxies emerge in the nested block, since
    //  they're added to a different list and not copied back
    // FIXME plus this is now useless, remove it and check block ID in proxies
    inline fun <Sign : BlockSignature> block(
        signature: Sign,
        f: InnerBlockBuilder<Sign>.() -> Terminator
    ): BasicBlock<Sign> =
        InnerBlock(signature).apply {
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
