package me.viluon.kobalt.extensions

sealed class Nat {
    abstract val value: Int
}

object Z : Nat() {
    override val value: Int = 0
}

class S<n : Nat>(pred: n) : Nat() {
    override val value: Int = pred.value + 1
}
