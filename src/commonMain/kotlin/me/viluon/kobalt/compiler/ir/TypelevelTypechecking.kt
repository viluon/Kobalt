package me.viluon.kobalt.compiler.ir

import me.viluon.kobalt.extensions.*

typealias BlockPxs = HVector<Px<*, *>, *>
typealias vStar = Variable<*>
typealias pStar = Px<*, *>
typealias HCV<T, XS> = HCons<Variable<T>, XS, vStar, *>
typealias HCP<T, XS> = HCons<Px<T, *>, XS, pStar, *>

class TypeValidationCertificate<Sg : BlockSignature, P : BlockPxs> private constructor(
    @Suppress("UNUSED_PARAMETER") signature: Sg,
    val pxs: P
) {
    companion object {
        @Suppress("FunctionName")
        @Deprecated("This function is intended solely for internal use in TypelevelTypechecking.")
        fun <Sg : BlockSignature, P : BlockPxs> __construct_unsafe_bypassing_check(
            s: Sg,
            p: P
        ): TypeValidationCertificate<Sg, P> = TypeValidationCertificate(s, p)
    }
}

@Suppress("NOTHING_TO_INLINE", "DEPRECATION", "unused")
object TypelevelParamChecks {
    // JVM function signatures need to be disambiguated,
    // hence the division into objects by parameter lengths
    object Zero {
        inline fun HNil.check(@Suppress("UNUSED_PARAMETER") params: HNil = HNil): TypeValidationCertificate<HNil, HNil> =
            TypeValidationCertificate.__construct_unsafe_bypassing_check(HNil, HNil)
    }

    object One {
        inline infix fun <Sg, P, A> Sg.check(params: P): TypeValidationCertificate<Sg, P>
                where A : LuaType, Sg : HCV<A, HNil>, P : HCP<A, HNil> =
            TypeValidationCertificate.__construct_unsafe_bypassing_check(this, params)
    }

    object Two {
        inline infix fun <Sg, P, A, B> Sg.check(params: P): TypeValidationCertificate<Sg, P>
                where A : LuaType, B : LuaType, Sg : HCV<A, HCV<B, HNil>>, P : HCP<A, HCP<B, HNil>> =
            TypeValidationCertificate.__construct_unsafe_bypassing_check(this, params)
    }

    object Three {
        inline infix fun <Sg, P, A, B, C> Sg.check(params: P): TypeValidationCertificate<Sg, P>
                where A : LuaType, B : LuaType, C : LuaType, Sg : HCV<A, HCV<B, HCV<C, HNil>>>, P : HCP<A, HCP<B, HCP<C, HNil>>> =
            TypeValidationCertificate.__construct_unsafe_bypassing_check(this, params)
    }
}

@Suppress("NOTHING_TO_INLINE")
inline fun <A : LuaType> InnerBlockBuilder<HCV<A, HNil>>.phi(): Px<A, S<Z>> {
    val (a) = self.signature
    val proxyA0 = alloc(a.id, a.type) extract this

    return Px(proxyA0.next)
}

@Suppress("NOTHING_TO_INLINE")
inline fun <A : LuaType, B : LuaType> InnerBlockBuilder<HCV<A, HCV<B, HNil>>>.phi(): Pair<Px<A, S<Z>>, Px<B, S<Z>>> {
    val (a, b) = self.signature
    val proxyA0 = alloc(a.id, a.type) extract this
    val proxyB0 = alloc(b.id, b.type) extract this

    return Pair(Px(proxyA0.next), Px(proxyB0.next))
}

@Suppress("NOTHING_TO_INLINE")
inline fun <A : LuaType, B : LuaType, C : LuaType> InnerBlockBuilder<HCV<A, HCV<B, HCV<C, HNil>>>>.phi(): Triple<Px<A, S<Z>>, Px<B, S<Z>>, Px<C, S<Z>>> {
    val (a, b, c) = self.signature
    val proxyA0 = alloc(a.id, a.type) extract this
    val proxyB0 = alloc(b.id, b.type) extract this
    val proxyC0 = alloc(c.id, c.type) extract this

    return Triple(Px(proxyA0.next), Px(proxyB0.next), Px(proxyC0.next))
}
