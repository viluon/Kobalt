package me.viluon.kobalt.compiler.ir

import me.viluon.kobalt.extensions.*

typealias vBound = Variable<*>
typealias pBound = Proxy<*, *>
typealias HC<T, XS, n> = HCons<Variable<T>, XS, vBound, n>

class TypeValidationCertificate<Sg : BlockSignature, P : BlockParams> private constructor(
    @Suppress("UNUSED_PARAMETER") signature: Sg,
    val params: P
) {
    companion object {
        val HNil.check0: TypeValidationCertificate<HNil, HNil> get() = TypeValidationCertificate(HNil, HNil)

        infix fun HNil.check0(params: HNil): TypeValidationCertificate<HNil, HNil> {
            return TypeValidationCertificate(this, params)
        }

        infix fun <Sg, P, A> Sg.check1(params: P): TypeValidationCertificate<Sg, P> where A : LuaType, Sg : HC<A, HNil, Z>, P : HCons<Proxy<A, *>, HNil, pBound, Z> {
            return TypeValidationCertificate(this, params)
        }
    }
}

inline fun <reified A : LuaType> InnerBlockBuilder<HVector<Variable<A>, S<Z>>>.phi1(): Proxy<A, S<Z>> {
    val v = (self.signature as HC<A, *, *>).head
    val proxy0 = alloc(v.id, v.type)

    @Suppress("USELESS_CAST", "UNCHECKED_CAST")
    val instr = when (v.type as LuaType) {
        TyBoolean -> TODO()
        TyInteger -> InstrPhiI(proxy0 as Proxy<TyInteger, *>) // TODO
        TyDouble -> TODO()
        TyString -> TODO()
        TyNil -> TODO()
        TyCoroutine -> TODO()
        TyFunction -> TODO()
        TyUnknown -> TODO()
        TyTable -> TODO()
    }

    return (proxy0 as Proxy<A, Z>).next
}

inline fun <reified A : LuaType, reified B : LuaType> InnerBlockBuilder<HC<A, HC<B, HNil, Z>, S<Z>>>.phi2(): Pair<Proxy<A, S<Z>>, Proxy<B, S<Z>>> {
    this.self
    TODO()
}
