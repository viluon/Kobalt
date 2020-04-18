package me.viluon.kobalt.compiler.ir

import me.viluon.kobalt.compiler.syntax.TkIdentifier
import me.viluon.kobalt.extensions.Nat
import me.viluon.kobalt.extensions.S
import me.viluon.kobalt.extensions.Z

typealias LoadKCtor<A> = (Proxy<A, *>, Constant<A>) -> Instruction

data class InnerBlockBuilder<out Sg : BlockSignature>(private val block: InnerBlock<Sg>) {
    private inline val instructions get() = block.instructions
    private inline val constants get() = block.constants
    private inline val variables get() = block.variables
    private inline val predecessors get() = block.predecessors
    val self get() = block

    private val <T : LuaType, n : Nat> Px<T, n>.proxy: Proxy<T, n> inline get() = extract(this@InnerBlockBuilder)
    private val <Sig : BlockSignature, P : BlockPxs> TypeValidationCertificate<Sig, P>.params: BlockParams
        inline get() = pxs.map { it.proxy }

    private fun addFollower(follower: BasicBlock<*>): BasicBlock<*> {
        block.followers.add(follower)
        follower.predecessors.add(block)
        return follower
    }

    private fun <T : LuaType> addProxy(v: Variable<T>): Px<T, Z> = Px(Proxy(this, v))

    fun <T : LuaType> alloc(id: TkIdentifier, type: T): Px<T, Z> {
        val v = Variable(id, instructions.size.toShort(), type)
        variables.add(v)
        return addProxy(v)
    }

    infix fun emit(instr: Instruction): BasicBlock<Sg> {
        instructions.add(instr)
        return block
    }

    fun <T : LuaType> ret(v: Px<T, S<*>>): Terminator =
        InstrReturn1(v.proxy).also { instructions.add(it) }

    fun <Params : BlockPxs, Sign : BlockSignature> jmp(
        cert: TypeValidationCertificate<Sign, Params>,
        target: BasicBlock<Sign>
    ): Terminator =
        InstrJump(cert.params, target).also {
            instructions.add(it)
            addFollower(target)
        }

    fun <EqP : BlockPxs, NeqP : BlockPxs, EqSg : BlockSignature, NeqSg : BlockSignature> eqI(
        left: Px<TyInteger, S<*>>,
        right: Px<TyInteger, S<*>>,
        eqCert: TypeValidationCertificate<EqSg, EqP>,
        neqCert: TypeValidationCertificate<NeqSg, NeqP>,
        targetEq: BasicBlock<EqSg>,
        targetNeq: BasicBlock<NeqSg>
    ): Terminator = InstrEqI(left.proxy, right.proxy, eqCert.params, neqCert.params, targetEq, targetNeq)
        .also {
            instructions.add(it)
            addFollower(targetEq)
            addFollower(targetNeq)
        }

    inline fun <Sig : BlockSignature> block(
        signature: Sig,
        f: InnerBlockBuilder<Sig>.() -> Terminator
    ): BasicBlock<Sig> = InnerBlock(signature).apply {
        open(f)
    }

    private inline fun <reified A : LuaType, T : LuaType> castBinOp(
        f: (t: Proxy<A, *>, l: Proxy<A, *>, r: Proxy<A, *>) -> Instruction,
        target: Proxy<T, *>,
        left: Proxy<*, *>,
        right: Proxy<*, *>
    ): Instruction {
        target.v.type as A
        @Suppress("UNCHECKED_CAST")
        // TODO try to enforce use of next in the type signatures
        return f(target.next as Proxy<A, *>, left as Proxy<A, *>, right as Proxy<A, *>)
    }

    fun <n : Nat, T : LuaType> add(
        target: Px<T, n>,
        left: Px<T, S<*>>,
        right: Px<T, S<*>>
    ): Px<T, S<n>> {
        val t = target.proxy
        val l = left.proxy
        val r = right.proxy
        @Suppress("USELESS_CAST")
        emit(
            when (t.v.type as LuaType) {
                TyInteger -> castBinOp(::InstrAddI, t, l, r)
                TyDouble -> castBinOp(::InstrAddF, t, l, r)
                TyString -> TODO("adding strings should parse them <5.4")
                TyCoroutine -> TODO()
                TyFunction -> TODO()
                TyUnknown -> TODO()
                TyBoolean -> TODO()
                TyTable -> TODO()
                TyNil -> TODO()
            }
        )

        return Px(t.next)
    }

    private inline fun <reified A : LuaConstantType, T : LuaConstantType> castLoad(
        f: LoadKCtor<A>,
        target: Proxy<T, *>,
        const: Constant<T>
    ): Instruction {
        target.v.type as A
        @Suppress("UNCHECKED_CAST")
        return f(target.next as Proxy<A, *>, const as Constant<A>)
    }

    fun <n : Nat, T : LuaConstantType> loadK(target: Px<T, n>, const: Constant<T>): Px<T, S<n>> {
        val t = target.proxy
        @Suppress("USELESS_CAST", "UNCHECKED_CAST")
        emit(
            when (t.v.type as LuaConstantType) {
                TyBoolean -> TODO()
                TyInteger -> castLoad(::InstrLoadI as LoadKCtor<TyInteger>, t, const)
                TyDouble -> castLoad(::InstrLoadF as LoadKCtor<TyDouble>, t, const)
                TyString -> TODO()
                TyNil -> TODO()
            }
        )

        return Px(t.next)
    }


    fun const(n: Long): ConstI = ConstI(n).also { constants.add(it) }
    fun const(n: Double): ConstF = ConstF(n).also { constants.add(it) }
    fun alloc(name: String): Px<TyUnknown, Z> = alloc(name, TyUnknown)
    fun alloc(id: TkIdentifier): Px<TyUnknown, Z> = alloc(id, TyUnknown)
    fun <T : LuaType> alloc(name: String, type: T): Px<T, Z> = alloc(TkIdentifier(name), type)
}
