package me.viluon.kobalt.extensions

sealed class Nat(val value: Int)
object Z : Nat(0)
class S<n : Nat>(pred: n) : Nat(pred.value + 1)
