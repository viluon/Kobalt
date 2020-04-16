package me.viluon.kobalt.compiler.ir

typealias Error = String
typealias LazyError = (ancestors: List<Verifiable>) -> Error

interface Verifiable {
    class Invariants(private val invs: MutableList<Pair<Boolean, LazyError>> = ArrayList()) {
        fun invariant(condition: Boolean, message: LazyError): Invariants {
            invs.add(Pair(condition, message))
            return this
        }

        fun verify(ancestors: List<Verifiable>): List<Error> = invs
            .filterNot { it.first }
            .map { "${ancestors.last()::class.simpleName}: " + it.second(ancestors) }
    }

    val verifiableChildren: Iterable<Verifiable> get() = listOf()
    val none: Invariants get() = Invariants()
    val invariants: Invariants

    fun checkInvariants(greatAncestors: List<Verifiable> = listOf(), processed: Set<Verifiable> = setOf()): List<Error> {
        val visited = processed + this
        val ancestors = greatAncestors + this
        return verifiableChildren
            .filterNot { visited.contains(it) }
            .fold(Pair(invariants.verify(ancestors), visited)) { (errors, processed), verifiable ->
                Pair(
                    errors + verifiable.checkInvariants(ancestors, processed),
                    processed + verifiable
                )
            }.first
    }
}

val Verifiable.define: Verifiable.Invariants get() = Verifiable.Invariants()
