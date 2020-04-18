package me.viluon.kobalt.extensions

sealed class HVector<out A, n : Nat>(val length: n) : Collection<A> {
    override fun isEmpty(): Boolean = length == Z
    override val size: Int get() = length.value
    abstract fun <B> map(f: (A) -> B): HVector<B, n>
}

object HNil : HVector<Nothing, Z>(Z) {
    override fun contains(element: Nothing): Boolean = false
    override fun containsAll(elements: Collection<Nothing>): Boolean = false
    override fun iterator(): Iterator<Nothing> = iterator { }
    override fun <B> map(f: (Nothing) -> B): HVector<B, Z> = this
}

class HCons<out X, out XS, out Bound, n>(val head: X, val tail: XS) : HVector<Bound, S<n>>(S(tail.length))
        where X : Bound, XS : HVector<Bound, n>, n : Nat {
    override fun contains(element: @UnsafeVariance Bound): Boolean = element == head || tail.contains(element)
    override fun containsAll(elements: Collection<@UnsafeVariance Bound>): Boolean = elements.all { contains(it) }
    override fun iterator(): Iterator<Bound> = iterator {
        var pos: HVector<Bound, *> = this@HCons
        while (pos is HCons<*, HVector<Bound, *>, *, *>) {
            yield(head)
            pos = pos.tail
        }
    }

    override fun <B> map(f: (Bound) -> B): HVector<B, S<n>> = HCons(f(head), tail.map(f))
}

@Suppress("NOTHING_TO_INLINE")
inline fun <X, B> hvectOf(x: X): HCons<X, HNil, B, Z> where X : B = HCons(x, HNil)

@Suppress("NOTHING_TO_INLINE")
inline fun <X, Y, B> hvectOf(x: X, y: Y): HCons<X, HCons<Y, HNil, B, Z>, B, S<Z>>
        where X : B, Y : B {
    return HCons(x, hvectOf(y))
}

@Suppress("NOTHING_TO_INLINE")
inline fun <X, Y, ZZ, B> hvectOf(x: X, y: Y, z: ZZ): HCons<X, HCons<Y, HCons<ZZ, HNil, B, Z>, B, S<Z>>, B, S<S<Z>>>
        where X : B, Y : B, ZZ : B {
    return HCons(x, hvectOf(y, z))
}

@Suppress("NOTHING_TO_INLINE")
inline operator fun <X, B> HCons<X, *, B, *>.component1(): X where X : B {
    return head
}

@Suppress("NOTHING_TO_INLINE")
inline operator fun <X, B> HCons<*, HCons<X, *, B, *>, B, *>.component2(): X where X : B {
    return tail.component1()
}

@Suppress("NOTHING_TO_INLINE")
inline operator fun <X, B> HCons<*, HCons<*, HCons<X, *, B, *>, B, *>, B, *>.component3(): X where X : B {
    return tail.component2()
}
