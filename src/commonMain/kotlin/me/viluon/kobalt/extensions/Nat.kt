package me.viluon.kobalt.extensions

sealed class Nat(val value: Int) {
    override fun toString(): String = value.toString()
}
object Z : Nat(0)
class S<n : Nat>(pred: n) : Nat(pred.value + 1)
