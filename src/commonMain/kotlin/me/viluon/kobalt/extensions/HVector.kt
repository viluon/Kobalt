package me.viluon.kobalt.extensions

sealed class HVector<out A, n : Nat>(val length: n) : Collection<A> {
    override fun isEmpty(): Boolean = length == Z
    override val size: Int get() = length.value
}

object HNil : HVector<Nothing, Z>(Z) {
    override fun contains(element: Nothing): Boolean = false
    override fun containsAll(elements: Collection<Nothing>): Boolean = false
    override fun iterator(): Iterator<Nothing> = iterator { }
}

data class HCons<out X, out XS, out Bound, n>(val head: X, val tail: XS) : HVector<Bound, S<n>>(S(tail.length))
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
}

fun <A, B> hvectOf(a: A): HCons<A, HNil, B, Z> where A : B {
    return HCons(a, HNil)
}
